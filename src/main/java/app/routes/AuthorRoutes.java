package app.routes;

import app.controllers.impl.AuthorController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AuthorRoutes {

    private final AuthorController authorController = new AuthorController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", authorController::create, Role.USER, Role.ADMIN);

            get("/", authorController::readAll, Role.USER, Role.ADMIN);

            get("/{id}", authorController::read, Role.USER, Role.ADMIN);

            put("/{id}", authorController::update, Role.ADMIN);

            delete("/{id}", authorController::delete, Role.ADMIN);

            get("/{id}/quotes", authorController::readAuthorWithQuotes, Role.USER, Role.ADMIN);

        };

    }

}
