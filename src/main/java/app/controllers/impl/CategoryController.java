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
    public void read(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        CategoryDTO payload = ctx.bodyValidator(CategoryDTO.class)
                .check(Objects::nonNull, "Body is required")
                .check(d -> d.getTitle() != null && !d.getTitle().isBlank(), "Title is required")
                .get();

        payload.setId(null); // ignorér klientens id

        CategoryDTO updated = categoryDAO.update(id, payload);
        ctx.status(200).json(updated, CategoryDTO.class);


    }

    @Override
    public void readAll(Context ctx) {
            // List of DTOS
            List<CategoryDTO> allCategories = categoryDAO.readAll();
            // response
            ctx.res().setStatus(200);
            ctx.json(allCategories, CategoryDTO.class);
        }


    @Override
    public void create(Context ctx) {

        // request
        CategoryDTO jsonRequest = ctx.bodyAsClass(CategoryDTO.class);
        // DTO
        CategoryDTO categoryDTO = categoryDAO.create(jsonRequest);
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
        return null;
    }
}
