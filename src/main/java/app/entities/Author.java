package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
}
