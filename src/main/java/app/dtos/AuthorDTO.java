package app.dtos;

import app.entities.Author;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorDTO {

    private Integer id;
    private String name;
    private LocalDate dateOfBirth;
    private String country;
  //  private Set<QuoteDTO> quotes;

    public AuthorDTO(Author author){

        this.id = author.getId();
        this.name = author.getName();
        this.dateOfBirth = author.getDateOfBirth();
        this.country = author.getCountry();
       // this.quotes = author.getQuotes().stream()
        //        .map(QuoteDTO::new)
          //      .collect(Collectors.toSet());

    }

}
