package app;

import app.config.HibernateConfig;
import app.config.QuotePopulator;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        QuotePopulator.populate(emf);

        emf.close();
    }
}