package app.dtos;

import app.entities.Author;
import app.entities.Category;
import app.entities.Quote;
import app.security.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor // Til Builder
@Data
public class QuoteDTO {
    private Integer id;
    private String text;
    private LocalDate createdAt;
    private LocalDateTime postedAt;
    private Category category;
    private Author author;
    private User user;
    private int favoritedCount; // For at undgå at overeksponere Userdata til frontend

    public QuoteDTO(Quote quote) {
        this.id = quote.getId();
        this.user = quote.getUser();
        this.author = quote.getAuthor();
        this.postedAt = quote.getPostedAt();
        this.category = quote.getCategory();
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
