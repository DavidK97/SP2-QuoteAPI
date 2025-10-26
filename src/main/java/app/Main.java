package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;


import app.populators.AuthorPopulator;
import app.populators.QuotePopulator;

import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EnumType;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // QuotePopulator.populate(emf);



        Javalin app = ApplicationConfig.startServer(7070);
    }
}