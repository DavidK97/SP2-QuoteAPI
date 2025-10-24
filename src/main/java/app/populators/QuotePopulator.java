package app.populators;

import app.dtos.QuoteDTO;
import app.entities.Author;
import app.entities.Category;
import app.entities.Quote;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuotePopulator {

    public static List<QuoteDTO> populate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<QuoteDTO> quoteDTOList = new ArrayList<>();
        List<Quote> quoteList = new ArrayList<>();

        try {
            tx.begin();

            // USERS
            User admin = new User("admin", "admin123");
            User user1 = new User("john_doe", "password123");
            User user2 = new User("jane_smith", "password456");

            Role adminRole = new Role("ADMIN");
            Role userRole = new Role("USER");
            Role anyoneRole = new Role("ANYONE");

            admin.addRole(adminRole);
            user1.addRole(userRole);
            user2.addRole(userRole);

            if (em.find(Role.class, "ADMIN") == null)
                em.persist(new Role("ADMIN"));
            if (em.find(Role.class, "USER") == null)
                em.persist(new Role("USER"));
            if (em.find(Role.class, "ANYONE") == null)
                em.persist(new Role("ANYONE"));
            em.persist(admin);
            em.persist(user1);
            em.persist(user2);

            //  AUTHORS
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

            em.persist(einstein);
            em.persist(twain);
            em.persist(angelou);

            //  CATEGORIES
            Category motivation = Category.builder().title("Motivation").build();
            Category life = Category.builder().title("Life").build();
            Category humor = Category.builder().title("Humor").build();

            em.persist(motivation);
            em.persist(life);
            em.persist(humor);

            // QUOTES
            Quote q1 = Quote.builder()
                    .text("Life is like riding a bicycle. To keep your balance, you must keep moving.")
                    .createdAt(LocalDate.now())
                    .author(einstein)
                    .category(life)
                    .user(admin)
                    .build();

            Quote q2 = Quote.builder()
                    .text("The secret of getting ahead is getting started.")
                    .createdAt(LocalDate.now())
                    .author(twain)
                    .category(motivation)
                    .user(user1)
                    .build();

            Quote q3 = Quote.builder()
                    .text("If you don't like something, change it. If you can't change it, change your attitude.")
                    .createdAt(LocalDate.now())
                    .author(angelou)
                    .category(motivation)
                    .user(user2)
                    .build();

            Quote q4 = Quote.builder()
                    .text("Get your facts first, then you can distort them as you please.")
                    .createdAt(LocalDate.now())
                    .author(twain)
                    .category(humor)
                    .user(user1)
                    .build();

            Quote q5 = Quote.builder()
                    .text("Try not to become a man of success, but rather try to become a man of value.")
                    .createdAt(LocalDate.now())
                    .author(einstein)
                    .category(life)
                    .user(admin)
                    .build();

            em.persist(q1);
            em.persist(q2);
            em.persist(q3);
            em.persist(q4);
            em.persist(q5);

            //  FAVORITES (many-to-many)
            user1.addFavoriteQuote(q1);
            user1.addFavoriteQuote(q3);
            user2.addFavoriteQuote(q2);
            admin.addFavoriteQuote(q4);

            em.merge(user1);
            em.merge(user2);
            em.merge(admin);

            tx.commit();

            quoteList.add(q1);
            quoteList.add(q2);
            quoteList.add(q3);
            quoteList.add(q4);
            quoteList.add(q5);


            System.out.println("Database populated with sample data!");

            quoteDTOList = QuoteDTO.toDTOList(quoteList);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return quoteDTOList;
    }
}
