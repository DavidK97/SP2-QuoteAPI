package app.controllers.impl;

import app.controllers.IController;
import app.dtos.QuoteDTO;
import io.javalin.http.Context;

public class AuthorController implements IController<QuoteDTO, Integer> {

    @Override
    public void read(Context ctx) {

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
        return false;
    }

    @Override
    public QuoteDTO validateEntity(Context ctx) {
        return null;
    }
}
