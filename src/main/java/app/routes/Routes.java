package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private final QuoteRoutes quoteRoutes = new QuoteRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/quotes", quoteRoutes.getRoutes());
            //path("/rooms", roomRoute.getRoutes());
        };
    }
}
