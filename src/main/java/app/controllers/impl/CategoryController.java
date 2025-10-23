package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.CategoryDAO;
import app.dtos.CategoryDTO;
import app.entities.Category;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;

public class CategoryController implements IController<CategoryDTO, Integer> {

    private final CategoryDAO categoryDAO;

    public CategoryController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.categoryDAO = new CategoryDAO(emf);
    }


    @Override
    public void read(Context ctx)  {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        CategoryDTO categoryDTO = categoryDAO.read(id);
        // response
        ctx.res().setStatus(200);
        ctx.json(categoryDTO, CategoryDTO.class);
    }


    @Override
    public void readAll(Context ctx) {
            // List of DTOS
            List<CategoryDTO> allCategories = categoryDAO.readAll();
            // response
            if(!allCategories.isEmpty()) {
                ctx.res().setStatus(200);
                ctx.json(allCategories, CategoryDTO.class);
            }
        }


    @Override
    public void create(Context ctx) {

        // request
        CategoryDTO jsonRequest = ctx.bodyAsClass(CategoryDTO.class);
        // DTO
        CategoryDTO categoryDTO = categoryDAO.create(jsonRequest);

        if(categoryDTO == null) {
            ctx.status(400);
        }

        // response
        ctx.res().setStatus(201);
        ctx.json(categoryDTO, CategoryDTO.class);
    }


    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        CategoryDTO categoryDTO = categoryDAO.update(id, validateEntity(ctx));

        if(categoryDTO == null) {
            ctx.status(400);
        }
        // response
        ctx.res().setStatus(200);
        ctx.json(categoryDTO, Category.class);

    }


    @Override
    public void delete(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        categoryDAO.delete(id);
        // response
        ctx.res().setStatus(204);

    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return categoryDAO.validatePrimaryKey(integer);
    }

    @Override
    public CategoryDTO validateEntity(Context ctx) {
            return ctx.bodyValidator(CategoryDTO.class)
                    .check( c -> c.getTitle() != null && !c.getTitle().isEmpty(), "Title must be set")
                    .get();
    }
}


