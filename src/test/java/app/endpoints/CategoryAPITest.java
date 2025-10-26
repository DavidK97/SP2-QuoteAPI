package app.endpoints;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.impl.CategoryDAO;
import app.dtos.CategoryDTO;
import app.entities.Category;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryAPITest {

    private Javalin app;
    private EntityManagerFactory emf;
    private CategoryDAO categoryDAO;
    private CategoryDTO c1, c2, c3;

    @BeforeAll
    void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        categoryDAO = new CategoryDAO(emf);
        app = ApplicationConfig.startServer(7076);
        RestAssured.baseURI = "http://localhost:7076/api/v1";
    }

    @BeforeEach
    void setupEach() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Ryd databasen
            em.createNativeQuery("TRUNCATE TABLE author, category, quote, role, users, users_favorite_quotes, users_roles RESTART IDENTITY CASCADE")
                    .executeUpdate();
            // Opret testdata
            em.persist(new Category("Tech"));
            em.persist(new Category("Humor"));
            em.persist(new Category("Life"));
            em.getTransaction().commit();
        }

        // Hent data som DTO’er til brug i assertions
        List<CategoryDTO> all = categoryDAO.readAll();
        c1 = all.get(0);
        c2 = all.get(1);
        c3 = all.get(2);

        assertNotNull(c1);
        assertEquals(3, all.size());
    }


    @AfterAll
    void afterAll() {
        ApplicationConfig.stopServer(app);
        if (emf != null && emf.isOpen()) emf.close();
    }



    // ---------- HAPPY PATHS ----------

    @Test
    void read() {
        given()
                .when()
                .get("/categories/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("title", is(c1.getTitle()));
    }

    @Test
    void readAll() {
        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("title", hasItems(c1.getTitle(), c2.getTitle(), c3.getTitle()));
    }

    @Test
    void create() {
        String payload = """
            { "title": "Sports" }
        """;

        given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/categories")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", is("Sports"));
    }

    @Test
    void update() {
        String payload = """
            { "title": "Funny" }
        """;

        given()
                .contentType("application/json")
                .body(payload)
                .when()
                .put("/categories/{id}", 2)
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("title", is("Funny"));
    }

    @Test
    void delete() {
        given()
                .when()
                .delete("/categories/{id}", 3)
                .then()
                .statusCode(204);
    }
}
