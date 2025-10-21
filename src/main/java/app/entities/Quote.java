package app.entities;

import app.dtos.QuoteDTO;
import app.security.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Setter
    private String text;

    @Setter
    private LocalDate createdAt;

    private LocalDateTime postedAt;


    // Relationer
    @ManyToOne
    private Category category;

    @Setter
    @ManyToOne
    private Author author;

    @ManyToOne
    private User user;


    @ManyToMany(mappedBy = "favoriteQuotes")
    private Set<User> favoritedByUsers;


    public Quote(QuoteDTO quoteDTO) {
        this.id = quoteDTO.getId();
        this.text = quoteDTO.getText();
        this.createdAt = quoteDTO.getCreatedAt();
        this.postedAt = quoteDTO.getPostedAt();
        this.category = quoteDTO.getCategory();
        this.author = quoteDTO.getAuthor();
        this.user = quoteDTO.getUser();
        this.favoritedByUsers = new HashSet<>();
    }

    //Hjælpemetoder
    public void favoritedByUser (User user) {
        this.favoritedByUsers.add(user);
        if (user != null) {
            user.addFavoriteQuote(this);
        }
    }

    @PrePersist
    protected void prePersist() {
        this.postedAt = LocalDateTime.now();
    }
}
