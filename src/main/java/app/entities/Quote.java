package app.entities;

import app.security.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

@Entity
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    private LocalDate createdAt;

    private LocalDateTime postedAt;


    // Relationer
    @ManyToOne
    private Category category;

    @ManyToOne
    private Author author;

    @ManyToOne
    private User user;


    @ManyToMany(mappedBy = "favoriteQuotes")
    private Set<User> favoritedByUsers;

    //Hjælpemetoder
    public void favoritedByUser (User user) {
        this.favoritedByUsers.add(user);
        if (user != null) {
            user.addFavoriteQuote(this);
        }
    }


    @PrePersist
    protected void onCreate() {
        this.postedAt = LocalDateTime.now();
    }
}
