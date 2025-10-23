package app.routes;

import app.controllers.impl.QuoteController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class QuoteRoutes {
    private final QuoteController quoteController = new QuoteController();

    //TODO angiv roller!!!

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/", quoteController::create, Role.USER, Role.ADMIN);
            get("/", quoteController::readAll, Role.USER, Role.ADMIN);
            get("/{id}", quoteController::read, Role.USER, Role.ADMIN);
            put("/{id}", quoteController::update, Role.ADMIN);
            delete("/{id}", quoteController::delete, Role.ADMIN);
        };
    }
}
