package app.daos.impl;

import app.daos.IDAO;
import app.dtos.CategoryDTO;
import app.entities.Category;
import app.entities.Quote;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CategoryDAO implements IDAO<CategoryDTO, Integer> {

    private final EntityManagerFactory emf;

    public CategoryDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public CategoryDTO read(Integer id) {
        try(EntityManager em = emf.createEntityManager()) {
            Category c = em.find(Category.class, id);
            return new CategoryDTO(c);
        }
    }

    @Override
    public List<CategoryDTO> readAll() {
        try(EntityManager em = emf.createEntityManager()) {
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c", Category.class);
            return CategoryDTO.toDTOList(query.getResultList());
        }
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Category category = new Category(categoryDTO);
            em.persist(category);
            em.getTransaction().commit();
            return new CategoryDTO(category);
        }
    }

    @Override
    public CategoryDTO update(Integer id, CategoryDTO categoryDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Category c = em.find(Category.class, id);
            c.setTitle(categoryDTO.getTitle());
            Category mergedCategory = em.merge(c);
            em.getTransaction().commit();
            return mergedCategory != null ? new CategoryDTO(mergedCategory) : null;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Category category = em.find(Category.class, id);

            if (category != null) {
                // Kopiér til ny liste for at undgå ConcurrentModification
                var quotes = List.copyOf(category.getQuotes());

                for (Quote q : quotes) {
                    // Fjern quote fra alle brugeres favorit-lister (ManyToMany)
                    var users = List.copyOf(q.getFavoritedByUsers());
                    for (User u : users) {
                        u.getFavoriteQuotes().remove(q);
                    }
                    // (valgfrit) fjern også fra kategoriens liste
                    category.getQuotes().remove(q);

                    // Nu kan quote fjernes sikkert
                    em.remove(q);
                }
                em.remove(category);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Category category = em.find(Category.class, integer);
            return category != null;
        }
    }
}


