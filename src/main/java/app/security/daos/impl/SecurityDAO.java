package app.security.daos.impl;


import app.security.daos.ISecurityDAO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

public class SecurityDAO implements ISecurityDAO {
    EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);

            // Tjek af om password passer
            if (foundUser != null && foundUser.verifyPassword(password)) {
                return foundUser;
            } else {
                throw new ValidationException("Username or Password was incorrect");
            }
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            User newUser = new User(username, password);
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
            return newUser;
        }
    }

    @Override
    public Role createRole(String rolename) {
        try(EntityManager em = emf.createEntityManager()){
            Role role = new Role(rolename);
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        }
    }

    @Override
    public User addUserRole(String username, String newRole) throws EntityNotFoundException {
        try(EntityManager em = emf.createEntityManager()){
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, newRole);
            if(foundRole == null || foundUser == null){
                throw new EntityNotFoundException("User or Role does not exist");
            }
            em.getTransaction().begin();
            foundUser.addRole(foundRole);
            em.getTransaction().commit();
            return foundUser;
        }
    }
}
