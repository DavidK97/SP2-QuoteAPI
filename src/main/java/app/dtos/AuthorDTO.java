package app.dtos;

import app.entities.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {

    private int id;
    private String name;
    private LocalDate dateOfBirth;
    private String country;
    private Set<QuoteDTO> quotes; // QuoteDTO i stedet for Quote

    public AuthorDTO(Author author){

        this.id = author.getId();
        this.name = author.getName();
        this.dateOfBirth = author.getDateOfBirth();
        this.country = author.getCountry();
        this.quotes = author.getQuotes().stream()
                .map(QuoteDTO::new)
                .collect(Collectors.toSet());

    }

}
