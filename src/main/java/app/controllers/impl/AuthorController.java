package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.AuthorDAO;
import app.dtos.AuthorDTO;
import app.entities.Author;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class AuthorController implements IController<AuthorDTO, Integer> {

    private final AuthorDAO authorDAO;

    public AuthorController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.authorDAO = new AuthorDAO(emf);
    }

    @Override
    public void read(Context ctx) {
        // Id hentes fra client request
        int id = Integer.parseInt(ctx.pathParam("id"));

        // Tjek om id er valid og findes i DB
        boolean result = validatePrimaryKey(id);
        if (!result) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id");
            return;
        }

        // AuthorDTO hentes fra DB
        AuthorDTO authorDTO = authorDAO.read(id);

        if (authorDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Author not found");
            return;
        }

        // Respons sendes til client
        ctx.status(HttpStatus.OK);
        ctx.json(authorDTO);
    }

    @Override
    public void readAll(Context ctx) {
        // Hent liste fra DB
        List<AuthorDTO> allAuthors = authorDAO.readAll();

        // Send response til klient
        if (!allAuthors.isEmpty()) {
            ctx.status(HttpStatus.OK);
            ctx.json(allAuthors);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Error retrieving authors");
        }
    }

    @Override
    public void create(Context ctx) {
        // Info hentes fra client request og valideres
        AuthorDTO jsonRequest = validateEntity(ctx);

        // Author gemmes
        AuthorDTO createdAuthor = authorDAO.create(jsonRequest);

        // Response til client
        if (createdAuthor != null) {
            ctx.status(HttpStatus.CREATED);
            ctx.json(createdAuthor);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Something went wrong, author could not be created");
        }
    }

    @Override
    public void update(Context ctx) {
        // Info hentes fra client request
        int id = Integer.parseInt(ctx.pathParam("id"));
        Author author = ctx.bodyAsClass(Author.class);

        // Tjek om id er valid og findes i DB
        boolean result = validatePrimaryKey(id);
        if (!result) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id");
            return;
        }

        AuthorDTO updatedAuthorDTO = authorDAO.update(id, new AuthorDTO(author));

        if (updatedAuthorDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Author not found");
            return;
        }

        // Response til client ved succes
        ctx.status(HttpStatus.OK);
        ctx.json(updatedAuthorDTO);
    }

    @Override
    public void delete(Context ctx) {
        // Client request
        int id = Integer.parseInt(ctx.pathParam("id"));

        // Tjek om id er valid og findes i DB
        boolean result = validatePrimaryKey(id);
        if (!result) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id, author could not be deleted");
        } else {
            authorDAO.delete(id);

            // Response
            ctx.status(HttpStatus.NO_CONTENT);
            ctx.result("Author with id: " + id + " deleted");
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0 && authorDAO.validatePrimaryKey(id);
    }

    @Override
    public AuthorDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(AuthorDTO.class)
                .check(a -> a.getName() != null && !a.getName().isEmpty(), "Not a valid author name")
                .check(a -> a.getCountry() != null && !a.getCountry().isEmpty(), "Not a valid author country")
                .check(a -> a.getDateOfBirth() != null, "Not a valid date of birth")
                .get();
    }
}
