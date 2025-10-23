package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.QuoteDAO;
import app.dtos.QuoteDTO;
import app.entities.Quote;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class QuoteController implements IController<QuoteDTO, Integer> {
    private final QuoteDAO quoteDAO;

    public QuoteController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.quoteDAO = new QuoteDAO(emf);
    }


    @Override
    public void read(Context ctx) {
        // Id hentes fra client request
        int id = Integer.parseInt(ctx.pathParam("id"));

        // Tjek om id er valid og om det findes i DB
        boolean result = validatePrimaryKey(id);
        if (result != true) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id");
            return;
        }

        // QuoteDTO hentes fra DB
        QuoteDTO quoteDTO = quoteDAO.read(id);

        if (quoteDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Quote not found");
            return;
        }
        // Respons sendes til client
        ctx.status(HttpStatus.OK);
        ctx.json(quoteDTO);
    }

    @Override
    public void readAll(Context ctx) {
        // Hent list fra DB
        List<QuoteDTO> allQuotes = quoteDAO.readAll();

        // Send response til klient
        if (!allQuotes.isEmpty()) {
            ctx.status(HttpStatus.OK);
            ctx.json(allQuotes);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Error retrieving Quotes");
        }
    }

    @Override
    public void create(Context ctx) {
        // Info hentes om client request og det valideres
        QuoteDTO jsonRequest = validateEntity(ctx);

        // Quote gemmes
        QuoteDTO createdQuote = quoteDAO.create(jsonRequest);


        // Response til client
        if (createdQuote != null) {
            ctx.status(HttpStatus.CREATED);
            ctx.json(createdQuote);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Something went wrong, quote could not be created");
        }
    }

    @Override
    public void update(Context ctx) {
        // Info hentes fra client request
        int id = Integer.parseInt(ctx.pathParam("id"));
        Quote quote = ctx.bodyAsClass(Quote.class);

        // Tjek om id er valid og om det findes i DB
        boolean result = validatePrimaryKey(id);
        if (result != true) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id");
            return;
        }

        QuoteDTO updatedQuoteDTO = quoteDAO.update(id, new QuoteDTO(quote));

        if (updatedQuoteDTO == null) {
            //Response til client ved error
            ctx.status(HttpStatus.NOT_FOUND).result("Quote not found");
            return;
        }

        // Response til client ved succes
        ctx.status(HttpStatus.OK);
        ctx.json(updatedQuoteDTO);

    }

    @Override
    public void delete(Context ctx) {
        // Client request
        int id = Integer.parseInt(ctx.pathParam("id"));

        // Tjek om id er valid og om det findes i DB
        boolean result = validatePrimaryKey(id);
        if (result != true) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id, quote could not be deleted");
        } else {
            quoteDAO.delete(id);

            // Response
            ctx.status(HttpStatus.NO_CONTENT);
            ctx.result("Quote with: " + id + " deleted");
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0 && quoteDAO.validatePrimaryKey(id);
    }

    @Override
    public QuoteDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(QuoteDTO.class)
                .check(q -> q.getText() != null, "Not a valid quote text")
                .check(q -> q.getCategory() != null, "Not a valid category type")
                .get();
    }
}
