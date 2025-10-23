package app.daos.impl;

import app.config.HibernateConfig;
import app.dtos.QuoteDTO;
import app.entities.Quote;
import app.exceptions.ApiException;
import app.populators.QuotePopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteDAOTest {
    private EntityManagerFactory emf;
    private QuoteDAO quoteDAO;


    QuoteDTO q1;
    QuoteDTO q2;
    QuoteDTO q3;
    QuoteDTO q4;
    QuoteDTO q5;

    private List<QuoteDTO> quoteDTOList;

    @BeforeAll
    void initOnce () {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        quoteDAO = new QuoteDAO(emf);
    }

    @BeforeEach
    void setUp() {
        // Restart all tables
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE author, category, quote, role, users, users_favorite_quotes, users_roles RESTART IDENTITY CASCADE")
                    .executeUpdate();
            em.getTransaction().commit();
        }
        catch (Exception e) {
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
    }

    @AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void getInstance () {
        assertNotNull(emf);
    }

    @Test
    void read() {
        // Arrange
        Integer id = q1.getId();

        // Act
        QuoteDTO actual = quoteDAO.read(id);

        // Assert
        assertNotNull(actual);
        assertEquals(q1.getText(), actual.getText());
        assertEquals(q1.getAuthor().getName(), actual.getAuthor().getName());
    }

    @Test
    void readAll() {
        // Arrange
        int expected = 5; // Kendes fra populator

        // Act
        List<QuoteDTO> allQuotes = quoteDAO.readAll();

        // Assert
        assertEquals(allQuotes.size(), expected);
        assertThat(allQuotes, containsInAnyOrder(q1, q2, q3, q4, q5));
    }

    @Test
    void create() {
        // Arrange
        QuoteDTO newQuote = QuoteDTO.builder()
                .text("New quote")
                .createdAt(LocalDate.now())
                .author(q1.getAuthor())
                .category(q1.getCategory())
                .user(q1.getUser())
                .build();

        // Act
        QuoteDTO savedQuote = quoteDAO.create(newQuote);

        // Assert
        assertNotNull(savedQuote.getId());
        assertThat(savedQuote.getId(), is(6));
        assertEquals(newQuote.getText(), savedQuote.getText());
    }


    @Test
    void update() {
        // Arrange
        QuoteDTO quoteInfoToUpdate = QuoteDTO.builder()
                .text("Updated quote text")
                .createdAt(LocalDate.of(2025, 10, 22))
                .build();

        // Act
        QuoteDTO updatedDTO = quoteDAO.update(q1.getId(), quoteInfoToUpdate);

        // Assert
        assertNotNull(updatedDTO);
        assertThat(updatedDTO.getId(), is(q1.getId()));
        assertEquals("Updated quote text", updatedDTO.getText());
        assertEquals(LocalDate.of(2025, 10, 22), updatedDTO.getCreatedAt());
    }



    @Test
    void delete() {
        // Arrange

        // Act
        quoteDAO.delete(1);
        List<QuoteDTO> allQuotes = quoteDAO.readAll();

        // Assert
        assertEquals(4, allQuotes.size());
        assertThat(allQuotes, containsInAnyOrder(q2, q3, q4, q5));
    }

    @Test
    void validatePrimaryKey() {
        // Arrange

        // Act
        boolean result = quoteDAO.validatePrimaryKey(q1.getId());
        boolean result2 = quoteDAO.validatePrimaryKey(6);

        // Assert
        assertTrue(result);
        assertFalse(result2);
    }
}