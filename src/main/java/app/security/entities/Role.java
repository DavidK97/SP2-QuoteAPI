package app.security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    @Column(nullable = false)
    private String rolename;


    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();


    public Role(String rolename){
        this.rolename = rolename;
        this.users = new HashSet<>();
    }
}
