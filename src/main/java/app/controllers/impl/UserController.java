package app.controllers.impl;

import app.config.HibernateConfig;
import app.daos.impl.QuoteDAO;
import app.daos.impl.UserDAO;
import app.security.daos.impl.SecurityDAO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;


public class UserController {
    private final UserDAO userDAO;

    public UserController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.userDAO = new UserDAO(emf);
    }

    public void addFavorite(Context ctx) {
        // Request
        String username = ctx.pathParam("username");
        int quoteId = Integer.parseInt(ctx.pathParam("quoteId"));

        try {
            userDAO.addFavoriteQuote(username, quoteId);

            // Response
            ctx.status(HttpStatus.OK);
            ctx.result("Quote with id " + quoteId + " added to favorites");
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("Something went wrong: " + e.getMessage());
        }
    }

    public void removeFavorite(Context ctx) {
        // Request
        String username = ctx.pathParam("username");
        int quoteId = Integer.parseInt(ctx.pathParam("quoteId"));

        try {
            userDAO.removeFavoriteQuote(username, quoteId);

            // Response
            ctx.status(HttpStatus.OK);
            ctx.result("Quote with id " + quoteId + " removed from favorites");
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("Something went wrong: " + e.getMessage());
        }
    }
}
