package app.security.dtos;

import app.security.entities.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class UserDTOShort {
    private String username;

    public UserDTOShort(User user) {
        this.username = user.getUsername();
    }
}
