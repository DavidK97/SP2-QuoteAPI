package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.QuoteDAO;
import app.dtos.QuoteDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class QuoteController implements IController<QuoteDTO, Integer> {
    private QuoteDAO quoteDAO;

    public QuoteController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.quoteDAO = new QuoteDAO(emf);
    }


    @Override
    public void read(Context ctx) {
        // Id hentes fra client request
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean result = validatePrimaryKey(id);

        // Tjek om id er valid og om det findes i DB
        if (result != true) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Not a valid id");
            return;
        }

        // QuoteDTO hentes fra DB
        QuoteDTO quoteDTO = quoteDAO.read(id);

        // Respons sendes til client
        ctx.status(HttpStatus.OK);
        ctx.json(quoteDTO);
    }

    @Override
    public void readAll(Context ctx) {

    }

    @Override
    public void create(Context ctx) {

    }

    @Override
    public void update(Context ctx) {

    }

    @Override
    public void delete(Context ctx) {

    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        //TODO overvej yderligere validering
        return quoteDAO.validatePrimaryKey(integer);
    }

    @Override
    public QuoteDTO validateEntity(Context ctx) {
        return null;
    }
}
