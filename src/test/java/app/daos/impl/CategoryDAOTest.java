package app.daos.impl;

import app.config.HibernateConfig;
import app.config.QuotePopulator;
import app.dtos.CategoryDTO;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryDAOTest {

    private EntityManagerFactory emf;

    private CategoryDAO categoryDAO;

    CategoryDTO c1;

    CategoryDTO c2;

    CategoryDTO c3;

    private List<CategoryDTO> categoryDTOList;

    @BeforeAll
    void initOnce () {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        categoryDAO = new CategoryDAO(emf);
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
        QuotePopulator.populate(emf);
        categoryDTOList = categoryDAO.readAll();
        if (categoryDTOList.size() == 3) {
            c1 = categoryDTOList.get(0);
            c2 = categoryDTOList.get(1);
            c3 = categoryDTOList.get(2);
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

        //Arrange
        int expectedId = 1;

        //Act
        CategoryDTO categoryDTO = categoryDAO.read(1);

        //Assert
        assertEquals(expectedId, categoryDTO.getId());


    }

    @Test
    void readAll() {

        //Arrange
        List<CategoryDTO> allCategories;
        int expectedSize = 3;

        //Act
        allCategories = categoryDAO.readAll();

        //Assert
        assertEquals(expectedSize, allCategories.size());
    }

    @Test
    void create() {

        //Arrange
        CategoryDTO c4 = CategoryDTO.builder()
                .title("philosophy")
                .build();

        int expectedId = 4;

        String expectedTitle = "philosophy";

        //Act
        CategoryDTO createdCategoryDTO = categoryDAO.create(c4);


        //Assert
        assertEquals(expectedId, createdCategoryDTO.getId());
        assertEquals(expectedTitle, createdCategoryDTO.getTitle());

    }

    @Test
    void update() {

        //Arrange



        //Act






        //Assert




    }

    @Test
    void delete() {

        //Arrange




        //Act


        //Assert


    }

    @Test
    void validatePrimaryKey() {
    }
}