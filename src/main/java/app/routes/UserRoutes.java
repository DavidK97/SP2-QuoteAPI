package app.routes;

import app.controllers.impl.QuoteController;
import app.controllers.impl.UserController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class UserRoutes {
    private final UserController userController = new UserController();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/{username}/favorites/{quoteId}", userController::addFavorite, Role.USER, Role.ADMIN);
            delete("/{username}/favorites/{quoteId}", userController::removeFavorite, Role.USER, Role.ADMIN);
        };
    }
}
