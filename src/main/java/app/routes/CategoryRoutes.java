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
            // get("/populate", hotelController::populate);
            post("/", categoryController::create, Role.ANYONE);
            get("/", categoryController::readAll, Role.ANYONE);
            get("/{id}", categoryController::read, Role.ANYONE);
            put("/{id}", categoryController::update, Role.ANYONE);
            delete("/{id}", categoryController::delete, Role.ANYONE);
            get("/", categoryController::getAllQuotesByCategory, Role.ANYONE);
        };
    }
}

