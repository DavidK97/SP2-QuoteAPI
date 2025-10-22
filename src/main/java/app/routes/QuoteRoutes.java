package app.routes;

import app.controllers.impl.QuoteController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class QuoteRoutes {
    private final QuoteController quoteController = new QuoteController();


    protected EndpointGroup getRoutes() {
        return () -> {
            // get("/populate", hotelController::populate);
            post("/", quoteController::create, Role.USER);
            get("/", quoteController::readAll, Role.USER);
            get("/{id}", quoteController::read, Role.ANYONE);
            put("/{id}", quoteController::update, Role.ADMIN);
            delete("/{id}", quoteController::delete, Role.ADMIN);
        };
    }
}
