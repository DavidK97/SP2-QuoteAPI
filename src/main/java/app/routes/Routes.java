package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    public EndpointGroup getRoutes() {
        return () -> {
            //path("/hotels", hotelRoute.getRoutes());
            //path("/rooms", roomRoute.getRoutes());
        };
    }
}
