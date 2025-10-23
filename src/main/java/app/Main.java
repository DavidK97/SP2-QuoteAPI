package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.QuotePopulator;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        QuotePopulator.populate(emf);

        Javalin app = ApplicationConfig.startServer(7076);


    }
}