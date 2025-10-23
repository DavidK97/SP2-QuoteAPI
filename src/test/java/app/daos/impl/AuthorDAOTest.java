package app.daos.impl;

import app.populators.AuthorPopulator;
import app.config.HibernateConfig;
import app.dtos.AuthorDTO;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorDAOTest {

    private EntityManagerFactory emf;
    private AuthorDAO authorDAO;

    private AuthorDTO a1;
    private AuthorDTO a2;
    private AuthorDTO a3;
    private AuthorDTO a4;
    private AuthorDTO a5;

    private List<AuthorDTO> authorDTOList;

    @BeforeAll
    void initOnce() {

        emf = HibernateConfig.getEntityManagerFactoryForTest();
        authorDAO = AuthorDAO.getInstance(emf);

    }

    @BeforeEach
    void setUp() {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE author RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to truncate author table", e);
        }

        authorDTOList = AuthorPopulator.populate(emf);
        if (authorDTOList.size() == 5) {
            a1 = authorDTOList.get(0);
            a2 = authorDTOList.get(1);
            a3 = authorDTOList.get(2);
            a4 = authorDTOList.get(3);
            a5 = authorDTOList.get(4);
        } else {
            throw new ApiException(500, "AuthorPopulator is broken...");
        }

    }

    @AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) {

            emf.close();

        }

    }

    @Test
    void getInstance() {

        assertNotNull(emf);
        assertNotNull(authorDAO);

    }

    @Test
    void read() {

        // Arrange
        Integer id = a1.getId();

        // Act
        AuthorDTO actual = authorDAO.read(id);

        // Assert
        assertNotNull(actual);
        assertEquals(a1.getName(), actual.getName());
        assertEquals(a1.getCountry(), actual.getCountry());

    }

    @Test
    void readAll() {

        // Arrange
        int expected = 5;

        // Act
        List<AuthorDTO> allAuthors = authorDAO.readAll();

        // Assert
        assertEquals(expected, allAuthors.size());
        assertThat(allAuthors, containsInAnyOrder(a1, a2, a3, a4, a5));

    }

    @Test
    void create() {

        // Arrange
        AuthorDTO newAuthor = AuthorDTO.builder()
                .name("Fyodor Dostoevsky")
                .dateOfBirth(LocalDate.of(1821, 11, 11))
                .country("Russia")
                .build();

        // Act
        AuthorDTO savedAuthor = authorDAO.create(newAuthor);

        // Assert
        assertNotNull(savedAuthor.getId());
        assertThat(savedAuthor.getId(), is(6));
        assertEquals(newAuthor.getName(), savedAuthor.getName());

    }

    @Test
    void update() {

        // Arrange
        AuthorDTO updatedInfo = AuthorDTO.builder()
                .name("Albert Einstein Jr.")
                .dateOfBirth(LocalDate.of(1879, 3, 15))
                .country("Switzerland")
                .build();

        // Act
        AuthorDTO updated = authorDAO.update(a1.getId(), updatedInfo);

        // Assert
        assertNotNull(updated);
        assertThat(updated.getId(), is(a1.getId()));
        assertEquals("Albert Einstein Jr.", updated.getName());
        assertEquals(LocalDate.of(1879, 3, 15), updated.getDateOfBirth());
        assertEquals("Switzerland", updated.getCountry());

    }

    @Test
    void delete() {

        // Act
        authorDAO.delete(a1.getId());
        List<AuthorDTO> allAuthors = authorDAO.readAll();

        // Assert
        assertEquals(4, allAuthors.size());
        assertThat(allAuthors, containsInAnyOrder(a2, a3, a4, a5));

    }

    @Test
    void validatePrimaryKey() {

        // Act
        boolean result1 = authorDAO.validatePrimaryKey(a1.getId());
        boolean result2 = authorDAO.validatePrimaryKey(999);

        // Assert
        assertTrue(result1);
        assertFalse(result2);

    }

}
