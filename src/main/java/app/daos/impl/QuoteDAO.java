package app.daos.impl;

import app.daos.IDAO;
import app.dtos.QuoteDTO;
import app.entities.Quote;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class QuoteDAO implements IDAO<QuoteDTO, Integer> {
    private EntityManagerFactory emf;

    // Dependency injection, hjælper os når vi skal teste klassens metoder
    public QuoteDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public QuoteDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Quote quote = em.find(Quote.class, integer);
            return new QuoteDTO(quote);
        }
    }

    @Override
    public List<QuoteDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Quote> quotes = em.createQuery("SELECT q FROM Quote q", Quote.class).getResultList();

            List<QuoteDTO> quoteDTOs = QuoteDTO.toDTOList(quotes);
            return quoteDTOs;
        } catch (Exception e) {
            throw new IllegalStateException("Could not retrieve quotes from database", e);
        }
    }

    @Override
    public QuoteDTO create(QuoteDTO quoteDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Quote quote = new Quote(quoteDTO);
            em.persist(quote);
            em.getTransaction().commit();
            return new QuoteDTO(quote);
        }
    }

    @Override
    public QuoteDTO update(Integer integer, QuoteDTO quoteDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Quote quote = em.find(Quote.class, integer);

            quote.setText(quoteDTO.getText());
            quote.setCreatedAt(quoteDTO.getCreatedAt());

            Quote mergedQuote = em.merge(quote);
            em.getTransaction().commit();
            return new QuoteDTO(mergedQuote);
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Quote quote = em.find(Quote.class, integer);
            if (quote != null) {
                // Slet tilknytning til users favorite quotes
                quote.getFavoritedByUsers().forEach(user -> user.getFavoriteQuotes().remove(quote));
                em.remove(quote);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Quote quote = em.find(Quote.class, integer);
            return quote != null;
        }
    }
}
