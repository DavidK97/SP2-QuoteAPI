package app.config;

import app.routes.Routes;
import app.security.controllers.ISecurityController;
import app.security.controllers.impl.SecurityController;
import app.security.routes.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;


public class ApplicationConfig {
    private static Routes routes = new Routes();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final ISecurityController securityController = SecurityController.getInstance();

    public static void configuration (JavalinConfig config) {
        config.showJavalinBanner = false; // Javalin Banner printes ikke i konsol
        config.bundledPlugins.enableRouteOverview("/routes");  // Endpoint hvor man kan se routes
        config.router.contextPath = "/api/v1"; // Base Path for alle endpoints
        config.router.apiBuilder(routes.getRoutes()); //Registrerer alle routes
        config.router.apiBuilder(new SecurityRoutes().getSecurityRoute);
        config.router.apiBuilder(new SecurityRoutes().getSecuredRoutes());
    }


    public static Javalin startServer (int port) {
        routes = new Routes();
        Javalin app = Javalin.create(ApplicationConfig::configuration);
        app.start(port);


        // Exception håndtering for forkert Json-input
        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(400).json(Map.of("error", "Invalid input, try again"));
            logger.error("IllegalStateException: " + e.getMessage(), e);
        });


        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of("error", "Internal server error, try again"));
            logger.error("An exception occurred: " + e.getMessage(), e);
        });

        // Logging til requests
        app.before(ctx -> {
            logger.info("Request: " + ctx.method() + ctx.path() + " Body: " + ctx.body());
        });

        // Logging til responses
        app.after(ctx -> {
            logger.info("Response: " + ctx.method() + ctx.path() + " Status: " + ctx.status() + " Body: " + ctx.result());
        });

        // Alle endpoints skal igennem nedenstående metoder inden andre endpoints kan nås
        app.beforeMatched(securityController.authenticate()); // check if there is a valid token in the header
        app.beforeMatched(securityController.authorize()); // check if the user has the required role

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }


}
