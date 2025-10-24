package app.controllers.impl;

import app.config.HibernateConfig;
import app.dtos.AuthorDTO;
import app.populators.AuthorPopulator;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorControllerTest {

    private EntityManagerFactory emf;
    private AuthorController controller;
    private AuthorDTO a1, a2, a3, a4, a5;
    private List<AuthorDTO> authorDTOList;

    @BeforeAll
    void initOnce() {

        emf = HibernateConfig.getEntityManagerFactoryForTest();
        controller = new AuthorController();

    }

    @BeforeEach
    void setUp() {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE author RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        }
        authorDTOList = AuthorPopulator.populate(emf);
        a1 = authorDTOList.get(0);
        a2 = authorDTOList.get(1);
        a3 = authorDTOList.get(2);
        a4 = authorDTOList.get(3);
        a5 = authorDTOList.get(4);

    }

    private Javalin createApp() {

        Javalin app = Javalin.create();
        app.get("/authors/{id}", controller::read);
        app.get("/authors", controller::readAll);
        app.post("/authors", controller::create);
        app.put("/authors/{id}", controller::update);
        app.delete("/authors/{id}", controller::delete);
        return app;

    }

    @Test
    void read_existingAuthor_returnsAuthor() {

        JavalinTest.test(createApp(), (server, client) -> {
            var res = client.get("/authors/" + a1.getId());
            assertEquals(200, res.code());
            var body = res.body().string();
            assertTrue(body.contains(a1.getName()));
            assertTrue(body.contains(a1.getCountry()));
        });

    }

    @Test
    void readAll_returnsListOfAuthors() {

        JavalinTest.test(createApp(), (server, client) -> {
            var res = client.get("/authors");
            assertEquals(200, res.code());
            var body = res.body().string();
            assertTrue(body.contains(a1.getName()));
            assertTrue(body.contains(a5.getName()));
        });

    }

    @Test
    void create_createsNewAuthor() {

        JavalinTest.test(createApp(), (server, client) -> {
            var json = """
                    {
                      "name": "Fyodor Dostoevsky",
                      "country": "Russia",
                      "dateOfBirth": "1821-11-11"
                    }
                    """;
            var res = client.post("/authors", json);
            assertEquals(201, res.code());
            var body = res.body().string();
            assertTrue(body.contains("Fyodor Dostoevsky"));
            assertTrue(body.contains("Russia"));
        });

    }

    @Test
    void update_updatesExistingAuthor() {

        JavalinTest.test(createApp(), (server, client) -> {
            var json = """
                    {
                      "name": "Albert Einstein Jr.",
                      "country": "Switzerland",
                      "dateOfBirth": "1879-03-14"
                    }
                    """;
            var res = client.put("/authors/" + a1.getId(), json);
            assertEquals(200, res.code());
            var body = res.body().string();
            assertTrue(body.contains("Albert Einstein Jr."));
            assertTrue(body.contains("Switzerland"));
        });

    }

    @Test
    void delete_deletesAuthor() {

        JavalinTest.test(createApp(), (server, client) -> {
            var deleteRes = client.delete("/authors/" + a1.getId());
            var getRes = client.get("/authors");
            assertEquals(204, deleteRes.code());
            var body = getRes.body().string();
            assertFalse(body.contains(a1.getName()));
        });

    }

    @AfterAll
    void tearDown() {

        if (emf != null && emf.isOpen()) emf.close();

    }

}
