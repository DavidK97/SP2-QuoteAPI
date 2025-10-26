package app.populators;

import app.dtos.AuthorDTO;
import app.entities.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuthorPopulator {

    public static List<AuthorDTO> populate(EntityManagerFactory emf) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<AuthorDTO> authorDTOList = new ArrayList<>();
        List<Author> authorList = new ArrayList<>();

        try {
            tx.begin();

            Author einstein = Author.builder()
                    .name("Albert Einstein")
                    .dateOfBirth(LocalDate.of(1879, 3, 14))
                    .country("Germany")
                    .build();

            Author twain = Author.builder()
                    .name("Mark Twain")
                    .dateOfBirth(LocalDate.of(1835, 11, 30))
                    .country("USA")
                    .build();

            Author angelou = Author.builder()
                    .name("Maya Angelou")
                    .dateOfBirth(LocalDate.of(1928, 4, 4))
                    .country("USA")
                    .build();

            Author shakespeare = Author.builder()
                    .name("William Shakespeare")
                    .dateOfBirth(LocalDate.of(1564, 4, 23))
                    .country("England")
                    .build();

            Author kierkegaard = Author.builder()
                    .name("Søren Kierkegaard")
                    .dateOfBirth(LocalDate.of(1813, 5, 5))
                    .country("Denmark")
                    .build();

            // Persister alle
            em.persist(einstein);
            em.persist(twain);
            em.persist(angelou);
            em.persist(shakespeare);
            em.persist(kierkegaard);

            tx.commit();

            // Tilføj til liste
            authorList.add(einstein);
            authorList.add(twain);
            authorList.add(angelou);
            authorList.add(shakespeare);
            authorList.add(kierkegaard);

            System.out.println("Database populated with sample authors!");

            // Konverter til DTO
            authorDTOList = authorList.stream()
                    .map(AuthorDTO::new)
                    .toList();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return authorDTOList;

    }

}
