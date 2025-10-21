package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    //Relationer
    @OneToMany(mappedBy = "category")
    @Builder.Default // Sørger for at Lombok initialiserer HashSettet
    @EqualsAndHashCode.Exclude // Sikrer mod stackOwerflow-error
    @ToString.Exclude // Sikrer mod stackOwerflow-error
    private Set<Quote> quotes = new HashSet<>();

}
