package app.security.dtos;

import app.security.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTOShort {
    private String username;

    public UserDTOShort(User user) {
        this.username = user.getUsername();
    }
}
