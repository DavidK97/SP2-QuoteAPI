package app.security.routes;


import app.security.controllers.ISecurityController;
import app.security.controllers.impl.SecurityController;
import app.security.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class SecurityRoutes {
    private EntityManagerFactory emf;
    ISecurityController securityController = SecurityController.getInstance();
    private static ObjectMapper jsonMapper = new ObjectMapper();

    public EndpointGroup getSecurityRoute = () -> {
        path("/auth",()-> {
            post("/login", securityController.login());
            post("/register", securityController.register());
        });
    };
    public EndpointGroup getSecuredRoutes(){
        return ()->{
            path("/protected", ()->{
                get("/user_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from USER Protected")), Role.USER);
                get("/admin_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from ADMIN Protected")),Role.ADMIN);
            });
        };
    }
}
