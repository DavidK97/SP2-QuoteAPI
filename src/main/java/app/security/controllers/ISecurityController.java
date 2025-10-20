package app.security.controllers;

import io.javalin.http.Handler;

public interface ISecurityController {
    Handler login(); // To get a token after checking username and password
    Handler register(); // To make a new User and get a token
    Handler authenticate(); // To verify that a token was sent with the request and that it is a valid, non-expired token
    Handler authorize(); // To verify user roles
}
