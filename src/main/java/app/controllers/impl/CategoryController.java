package app.controllers.impl;

import app.controllers.IController;
import app.dtos.CategoryDTO;
import io.javalin.http.Context;

public class CategoryController implements IController<CategoryDTO, Integer> {
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
    public CategoryDTO validateEntity(Context ctx) {
        return null;
    }
}
