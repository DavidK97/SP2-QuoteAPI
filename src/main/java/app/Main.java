package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.QuotePopulator;
import app.daos.impl.CategoryDAO;
import app.dtos.CategoryDTO;
import app.security.daos.ISecurityDAO;
import app.security.daos.impl.SecurityDAO;
import app.security.entities.User;
import app.security.enums.Role;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EnumType;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        CategoryDAO categoryDAO = new CategoryDAO(emf);
        ISecurityDAO securityDAO = new SecurityDAO(emf);

        //QuotePopulator.populate(emf);

        Javalin app = ApplicationConfig.startServer(7076);
        CategoryDTO c1 = CategoryDTO.builder()
                .title("Philosophy").build();
        CategoryDTO c2 = CategoryDTO.builder()
                .title("Motivation").build();

        categoryDAO.create(c1);
        categoryDAO.create(c2);

        User user1 = securityDAO.createUser("Jeppe", "Gymshark25");

    }
}