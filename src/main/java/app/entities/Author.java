package app.entities;


import app.dtos.AuthorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private LocalDate dateOfBirth;

    private String country;

    // Relationer
    @OneToMany(mappedBy = "author")
    @Builder.Default // Sørger for at Lombok initialiserer HashSettet
    @EqualsAndHashCode.Exclude // Sikrer mod stackOwerflow-error
    @ToString.Exclude // Sikrer mod stackOwerflow-error
    private Set<Quote> quotes = new HashSet<>();

    public Author(AuthorDTO authorDTO){

        this.id = authorDTO.getId();
        this.name = authorDTO.getName();
        this.dateOfBirth = authorDTO.getDateOfBirth();
        this.country = authorDTO.getCountry();

    }

    public void addQuote (Quote quote) {
        this.quotes.add(quote);
        if (quote != null) {
            quote.setAuthor(this);
        }
    }

}
