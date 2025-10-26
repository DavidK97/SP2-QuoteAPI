package app.routes;

import app.controllers.impl.CategoryController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class CategoryRoutes {

    private final CategoryController categoryController = new CategoryController();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/", categoryController::create, Role.ANYONE);
            get("/", categoryController::readAll, Role.ANYONE);

            path("{id}", () -> {
                get(categoryController::read, Role.ANYONE);
                put(categoryController::update, Role.ANYONE);
                delete(categoryController::delete, Role.ANYONE);


                path("quotes", () -> {
                    get(categoryController::getAllQuotesByCategory, Role.ANYONE);
                });
            });
        };
    }
}


