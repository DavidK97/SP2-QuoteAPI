package app.dtos;

import app.entities.Author;
import app.entities.Category;
import app.entities.Quote;
import app.security.dtos.UserDTOShort;
import app.security.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuoteDTO {
    private Integer id;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Til test, sammenligning
    private LocalDate createdAt;
    private LocalDateTime postedAt;
    private CategoryDTO category;
    private AuthorDTO author;
    private UserDTOShort user;
    private int favoritedCount; // For at undgå at overeksponere Userdata til frontend

    public QuoteDTO(Quote quote) {
        this.id = quote.getId();
        this.user = new UserDTOShort(quote.getUser());
       this.author = AuthorDTO.builder()
               .id(quote.getAuthor().getId())
               .name(quote.getAuthor().getName())
               .country(quote.getAuthor().getCountry())
               .dateOfBirth(quote.getAuthor().getDateOfBirth())
               .build();
        this.postedAt = quote.getPostedAt();
        this.category = new CategoryDTO(quote.getCategory());
        this.createdAt = quote.getCreatedAt();
        this.text = quote.getText();
        this.favoritedCount = quote.getFavoritedByUsers().size();
    }

    public static List<QuoteDTO> toDTOList(List<Quote> quotes) {
        return quotes
                .stream()
                .map(quote -> new QuoteDTO(quote))
                .toList();
    }
}
