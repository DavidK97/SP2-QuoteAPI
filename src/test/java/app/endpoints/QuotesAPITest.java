package app.endpoints;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.impl.QuoteDAO;
import app.dtos.QuoteDTO;
import app.exceptions.ApiException;
import app.populators.QuotePopulator;
import app.security.daos.impl.SecurityDAO;
import app.security.entities.User;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuotesAPITest {
    private Javalin app;
    private EntityManagerFactory emf;
    private QuoteDAO quoteDAO;
    private SecurityDAO securityDAO;
    private String token;

    private QuoteDTO q1;
    private QuoteDTO q2;
    private QuoteDTO q3;
    private QuoteDTO q4;
    private QuoteDTO q5;

    private List<QuoteDTO> quoteDTOList;

    @BeforeAll
    public void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        quoteDAO = new QuoteDAO(emf);
        app = ApplicationConfig.startServer(7076);
        this.quoteDTOList = new ArrayList<>();
    }

    @BeforeEach
    void setUp() {
        // Restart all tables
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE author, category, quote, role, users, users_favorite_quotes, users_roles RESTART IDENTITY CASCADE")
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to truncate tables", e);
        }

        // Populate tables
        quoteDTOList = QuotePopulator.populate(emf);
        if (quoteDTOList.size() == 5) {
            q1 = quoteDTOList.get(0);
            q2 = quoteDTOList.get(1);
            q3 = quoteDTOList.get(2);
            q4 = quoteDTOList.get(3);
            q5 = quoteDTOList.get(4);
        } else {
            throw new ApiException(500, "Populator doesnt work");
        }

        // TODO test
        System.out.println("Users in DB: " +
                emf.createEntityManager()
                        .createQuery("SELECT u FROM User u", User.class)
                        .getResultList()
        );


        // Login an Admin user and save token
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

    }


    @AfterAll
    public void afterAll() {
        ApplicationConfig.stopServer(app);

        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }


    @Test
    void read() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/quotes/1")

                .then()
                .statusCode(200)
                .body("text", is("Life is like riding a bicycle. To keep your balance, you must keep moving."))
                .body("category.title", is("Life"))
                .body("author.name", is("Albert Einstein"));
    }

    @Test
    void readAll() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/quotes/")

                .then()
                .statusCode(200)
                .body("text", containsInAnyOrder(q1.getText(), q2.getText(), q3.getText(), q4.getText(), q5.getText()))
                .body("author.name", hasItems("Albert Einstein", "Mark Twain", "Maya Angelou"))
                .body("category.title", hasItems("Life", "Motivation", "Humor"));
    }

    @Test
    void create() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String JsonRequest = """
                {
                  "text": "A quote text",
                  "createdAt": "2025-10-22",
                  "category": {
                    "id": 2
                  },
                  "author": {
                    "name": "A Author",
                    "dateOfBirth": "1879-03-14",
                    "country": "Germany"
                  },
                  "user": {
                    "username": "admin"
                  }
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(JsonRequest)

                .when()
                .post("/quotes")

                .then()
                .statusCode(201)
                .body("id", is(6))
                .body("text", is("A quote text"))
                .body("author.name", is("A Author"));
    }

    @Test
    void update() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String JsonRequestToUpdate = """
                {
                    "text": "Opdateret tekst",
                        "createdAt": "1111-10-10",
                        "category": {
                    "id": 2
                },
                    "author": {
                    "name": "Ny author",
                            "dateOfBirth": "1879-03-14",
                            "country": "Germany"
                },
                    "user": {
                    "username": "admin"
                }
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(JsonRequestToUpdate)

                .when()
                .put("/quotes/1")

                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("text", is("Opdateret tekst"))
                .body("createdAt", equalTo("1111-10-10"));
    }

    @Test
    void delete() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/quotes/1")
                .then()
                .statusCode(204);
    }


    // Non-Happy path cases
    @Test
    void readNonExistingQuote() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/quotes/100")
                .then()
                .statusCode(404)
                .body(is("Not a valid id"));
    }

    @Test
    void readWithNoToken() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .when()
                .get("/quotes/1")
                .then()
                .statusCode(401)
                .body(is("Authorization header is missing"));
    }

    @Test
    void deleteNonExistingQuote() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/quotes/100")
                .then()
                .statusCode(404)
                .body(is("Not a valid id, quote could not be deleted"));
    }

    @Test
    void updateNonExistingQuote() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String JsonRequestToUpdate = """
                {
                    "text": "Opdateret tekst",
                        "createdAt": "1111-10-10",
                        "category": {
                    "id": 2
                },
                    "author": {
                    "name": "Ny author",
                            "dateOfBirth": "1879-03-14",
                            "country": "Germany"
                },
                    "user": {
                    "username": "admin"
                }
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(JsonRequestToUpdate)

                .when()
                .put("/quotes/100")

                .then()
                .statusCode(404)
                .body(is("Not a valid id"));
    }

    /* //TODO hvis date format er forkert, hvis category ikke findes
    @Test
    void createQuoteInvalidInput() {
        RestAssured.baseURI = "http://localhost:7076/api/v1";

        String invalidJson = """
                {
                  "text": "A quote text",
                  "createdAt": "2025-10-22",
                  "category": {
                    "id": 2
                  },
                  "author": {
                    "name": "A Author",
                    "dateOfBirth": "1879-03-14",
                    "country": "Germany"
                  },
                  "user": {
                    "username": "admin"
                  }
                }
                """;

        given()
                .body(invalidJson)
                .when()
                .post("/quotes")
                .then()
                .statusCode(400)
                .body(is("Something went wrong, quote could not be created"));

    }

     */


}