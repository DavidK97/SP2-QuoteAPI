package app.daos.impl;

import app.daos.IDAO;
import app.dtos.AuthorDTO;
import app.dtos.QuoteDTO;
import app.entities.Author;
import app.entities.Category;
import app.entities.Quote;
import app.exceptions.EntityNotFoundException;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

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
    public QuoteDTO create(QuoteDTO quoteDTO) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Quote quote = new Quote(quoteDTO);

            // Tjek om Category findes
            Category category = em.find(Category.class, quoteDTO.getCategory().getId());
            if (category == null) {
                throw new EntityNotFoundException("Category not found in DB");
            } else {
                quote.setCategory(category);
            }

            // Tjek om Author findes i DB
            AuthorDTO authorDTO = quoteDTO.getAuthor();
            Author author;

            Optional<Author> existingAuthor = em.createQuery("SELECT a FROM Author a WHERE a.name = :name", Author.class)
                    .setParameter("name", authorDTO.getName())
                    .getResultStream()
                    .findFirst();

            if (existingAuthor.isPresent()) {
                author = existingAuthor.get();
            } else {
                author = new Author();
                author.setName(authorDTO.getName());
                author.setDateOfBirth(authorDTO.getDateOfBirth());
                author.setCountry(authorDTO.getCountry());
                em.persist(author);
            }

            quote.setAuthor(author);

            // Tjek om username findes i DB
            User user = em.find(User.class, quoteDTO.getUser().getUsername());
            if (user == null) {
                throw new EntityNotFoundException("User not found in DB");
            }
            quote.setUser(user);

            // Quote gemmes i DB
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
    public boolean validatePrimaryKey(Integer integer) throws IllegalArgumentException {
        try (EntityManager em = emf.createEntityManager()) {
            Quote quote = em.find(Quote.class, integer);
            return quote != null;
        }
    }
}
