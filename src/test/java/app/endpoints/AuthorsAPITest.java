package app.endpoints;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.QuotePopulator;
import app.daos.impl.AuthorDAO;
import app.dtos.AuthorDTO;
import app.populators.AuthorPopulator;
import app.security.daos.impl.SecurityDAO;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorsAPITest {

    private Javalin app;
    private EntityManagerFactory emf;
    private AuthorDAO authorDAO;
    private SecurityDAO securityDAO;
    private String token;

    private AuthorDTO a1;
    private AuthorDTO a2;
    private AuthorDTO a3;

    @BeforeAll
    void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        authorDAO = new AuthorDAO(emf);
        app = ApplicationConfig.startServer(7076);
    }

    @BeforeEach
    void setUpEach() {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE author, category, quote, role, users, users_favorite_quotes, users_roles RESTART IDENTITY CASCADE")
                    .executeUpdate();
            em.getTransaction().commit();
        }

        AuthorPopulator.populate(emf);
        QuotePopulator.populate(emf);

        String loginJson = """
                {
                  "username": "admin",
                  "password": "admin123"
                }
                """;

        token = given()
                .body(loginJson)
                .when()
                .post("http://localhost:7076/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        List<AuthorDTO> authors = authorDAO.readAll();
        a1 = authors.get(0);
        a2 = authors.get(1);
        a3 = authors.get(2);
    }

    @AfterAll
    void tearDown() {
        ApplicationConfig.stopServer(app);
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Test
    void read() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/authors/1")
                .then()
                .statusCode(200)
                .body("name", is(a1.getName()))
                .body("country", is(a1.getCountry()));
    }

    @Test
    void readAll() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/authors/")
                .then()
                .statusCode(200)
                .body("name", hasItems(a1.getName(), a2.getName(), a3.getName()))
                .body("country", hasItems("Germany", "USA", "USA"));
    }

    @Test
    void create() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String newAuthor = """
                {
                    "name": "New Author",
                    "dateOfBirth": "1985-05-05",
                    "country": "Denmark"
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(newAuthor)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("New Author"))
                .body("country", is("Denmark"));
    }

    @Test
    void update() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String updateAuthor = """
                {
                    "name": "Updated Author",
                    "dateOfBirth": "1970-01-01",
                    "country": "Sweden"
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(updateAuthor)
                .when()
                .put("/authors/1")
                .then()
                .statusCode(200)
                .body("name", is("Updated Author"))
                .body("country", is("Sweden"));
    }

    @Test
    void delete() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/authors/1")
                .then()
                .statusCode(204);
    }

    // Non-happy path tests
    @Test
    void readNonExistingAuthor() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/authors/999")
                .then()
                .statusCode(404)
                .body(is("Not a valid id"));
    }

    @Test
    void deleteNonExistingAuthor() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/authors/999")
                .then()
                .statusCode(404)
                .body(is("Not a valid id, author could not be deleted"));
    }

    @Test
    void createWithoutToken() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String newAuthor = """
                {
                    "name": "NoToken Author",
                    "dateOfBirth": "1990-02-02",
                    "country": "Norway"
                }
                """;

        given()
                .body(newAuthor)
                .when()
                .post("/authors")
                .then()
                .statusCode(401)
                .body(is("Authorization header is missing"));
    }
}
