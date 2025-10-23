package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private final QuoteRoutes quoteRoutes = new QuoteRoutes();
    private final CategoryRoutes categoryRoutes = new CategoryRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/quotes", quoteRoutes.getRoutes());
            path("/categories", categoryRoutes.getRoutes());
            //path("/rooms", roomRoute.getRoutes());
        };
    }
}
