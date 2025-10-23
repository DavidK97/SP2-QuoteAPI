package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.QuotePopulator;
import app.daos.impl.CategoryDAO;
import app.dtos.CategoryDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        CategoryDAO categoryDAO = new CategoryDAO(emf);

        //QuotePopulator.populate(emf);

        Javalin app = ApplicationConfig.startServer(7076);
        CategoryDTO c1 = CategoryDTO.builder()
                .title("Philosophy").build();
        CategoryDTO c2 = CategoryDTO.builder()
                .title("Motivation").build();

        categoryDAO.create(c1);
        categoryDAO.create(c2);


    }
}