package app.daos.impl;

import app.daos.IDAO;
import app.dtos.AuthorDTO;
import app.entities.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.util.List;

public class AuthorDAO implements IDAO<AuthorDTO, Integer> {

    private static AuthorDAO instance;
    private static EntityManagerFactory emf;

    public AuthorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public static AuthorDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AuthorDAO(emf);
        }
        return instance;
    }

    @Override
    public AuthorDTO read(Integer integer){

        try (EntityManager em = emf.createEntityManager()) {
            Author author = em.find(Author.class, integer);
            return author != null ? new AuthorDTO(author) : null;
        }

    }

    @Override
    public List<AuthorDTO> readAll() {

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<AuthorDTO> query = em.createQuery("SELECT new app.dtos.AuthorDTO(a) FROM Author a", AuthorDTO.class);
            return query.getResultList();
        }

    }

    @Override
    public AuthorDTO create(AuthorDTO authorDTO) {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Author author = new Author(authorDTO);
            em.persist(author);
            em.getTransaction().commit();
            return new AuthorDTO(author);
        }

    }

    @Override
    public AuthorDTO update(Integer integer, AuthorDTO authorDTO) {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Author a = em.find(Author.class, integer);
            a.setName(authorDTO.getName());
            a.setDateOfBirth(authorDTO.getDateOfBirth());
            a.setCountry(authorDTO.getCountry());
            Author mergedAuthor = em.merge(a);
            em.getTransaction().commit();
            return new AuthorDTO(mergedAuthor);
        }

    }

        @Override
        public void delete(Integer integer) {

            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                Author author = em.find(Author.class, integer);
                if (author != null){
                    em.remove(author);
                }
                em.getTransaction().commit();
            }

        }

    @Override
    public boolean validatePrimaryKey(Integer integer) {

        try (EntityManager em = emf.createEntityManager()) {
            Author author = em.find(Author.class, integer);
            return author != null;
        }

    }

}
