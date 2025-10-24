package app.daos.impl;

import app.entities.Quote;
import app.exceptions.EntityNotFoundException;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserDAO {
    private EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void addFavoriteQuote(String username, int quoteId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Find user og quote i DB
            User foundUser = em.find(User.class, username);
            Quote foundQuote = em.find(Quote.class, quoteId);

            //Valider fundene
            if (foundUser == null || foundQuote == null) {
                throw new EntityNotFoundException("User or quote not found");
            }

            // Add til favorites
            foundUser.addFavoriteQuote(foundQuote);
            em.merge(foundUser);
            em.getTransaction().commit();
        }
    }

    public void removeFavoriteQuote(String username, int quoteId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find user og quote i DB
            User foundUser = em.find(User.class, username);
            Quote foundQuote = em.find(Quote.class, quoteId);

            //Valider fundene
            if (foundUser == null || foundQuote == null) {
                throw new EntityNotFoundException("User or quote not found");
            }

            // Fjern fra favorites
            foundUser.removeFavoriteQuote(foundQuote);
            em.merge(foundUser);
            em.getTransaction().commit();
        }
    }

    public boolean validatePrimaryKey(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            return user != null;
        }
    }
}
