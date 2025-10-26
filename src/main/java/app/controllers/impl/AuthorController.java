package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.AuthorDAO;
import app.daos.impl.QuoteDAO;
import app.dtos.AuthorDTO;
import app.dtos.QuoteDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorController implements IController<AuthorDTO, Integer> {

    private final AuthorDAO authorDAO;

    public AuthorController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.authorDAO = new AuthorDAO(emf);
    }

    @Override
    public void read(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id");
            return;
        }
        AuthorDTO authorDTO = authorDAO.read(id);
        if (authorDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Author not found");
            return;
        }
        ctx.status(HttpStatus.OK).json(authorDTO);
    }

    @Override
    public void readAll(Context ctx) {
        List<AuthorDTO> allAuthors = authorDAO.readAll();
        if (!allAuthors.isEmpty()) {
            ctx.status(HttpStatus.OK).json(allAuthors);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Error retrieving authors");
        }
    }

    @Override
    public void create(Context ctx) {
        AuthorDTO jsonRequest = validateEntity(ctx);
        AuthorDTO createdAuthor = authorDAO.create(jsonRequest);
        if (createdAuthor != null) {
            ctx.status(HttpStatus.CREATED).json(createdAuthor);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("Something went wrong, author could not be created");
        }
    }

    @Override
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        AuthorDTO authorDTO = new AuthorDTO(ctx.bodyAsClass(app.entities.Author.class));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id");
            return;
        }
        AuthorDTO updatedAuthorDTO = authorDAO.update(id, authorDTO);
        if (updatedAuthorDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Author not found");
            return;
        }
        ctx.status(HttpStatus.OK).json(updatedAuthorDTO);
    }

    @Override
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id, author could not be deleted");
            return;
        }
        authorDAO.delete(id);
        ctx.status(HttpStatus.NO_CONTENT).result("Author with id: " + id + " deleted");
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

    public void readAuthorWithQuotes(Context ctx) {
        try {
            int authorId = Integer.parseInt(ctx.pathParam("id"));
            if (!validatePrimaryKey(authorId)) {
                ctx.status(404).result("Not a valid id");
                return;
            }
            AuthorDTO authorDTO = authorDAO.read(authorId);
            if (authorDTO == null) {
                ctx.status(404).result("Author not found");
                return;
            }
            List<QuoteDTO> quotes = authorDAO.readQuotesByAuthor(authorId);
            Map<String, Object> response = new HashMap<>();
            response.put("author", authorDTO);
            response.put("quotes", quotes != null ? quotes : List.of());
            ctx.status(200).json(quotes);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }
}
