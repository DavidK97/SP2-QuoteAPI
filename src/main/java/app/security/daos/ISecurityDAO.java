package app.security.daos;

import app.exceptions.EntityNotFoundException;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;

public interface ISecurityDAO {
    User getVerifiedUser(String username, String password) throws ValidationException; // For login
    User createUser(String username, String password); // Used to register new User
    Role createRole(String role); // To create new Role in DB
    User addUserRole(String username, String role) throws EntityNotFoundException; // Finds existing role in DB and adds to User
}
