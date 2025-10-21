package app.security.enums;

import io.javalin.security.RouteRole;

//RouteRole gør at man kan koble nedenstående roller direkte på ens routes
public enum Role implements RouteRole {
    ANYONE,
    USER,
    ADMIN;
}
