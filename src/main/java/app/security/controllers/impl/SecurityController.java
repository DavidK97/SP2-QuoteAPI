package app.security.controllers.impl;


import app.config.HibernateConfig;
import app.exceptions.ApiException;
import app.security.controllers.ISecurityController;
import app.security.daos.ISecurityDAO;
import app.security.daos.impl.SecurityDAO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.NotAuthorizedException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.*;
import jakarta.persistence.EntityManagerFactory;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityController implements ISecurityController {
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;
    ObjectMapper objectMapper = new ObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();

    private SecurityController() { }

    public static SecurityController getInstance() { // Singleton because we don't want multiple instances of the same class
        if (instance == null) {
            instance = new SecurityController();
        }
        securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }


    @Override
    public Handler login() {
        return (Context ctx) -> {
            //1. Vi hiver klient input ud af request
            User user = ctx.bodyAsClass(User.class);

            //2. Authorize: Vi tjekker at password og username er korrekt
            User checkedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

            //3. Authenticate: Vi tjekker hvilke roller useren har og mapper dem til Strings
            Set<String> roles = checkedUser
                    .getRoles()
                    .stream()
                    .map(role -> role.getRolename())
                    .collect(Collectors.toSet());

            //4. Vi laver en UserDTO så det eneste data vi eksponerer er username og roles
            UserDTO userDTOForToken = new UserDTO(checkedUser.getUsername(), roles);

            //5. Vi laver et token for Useren
            String token = createToken(userDTOForToken);

            //6. Vi bygger et Json-response og sender dem token og username
            ObjectNode response = objectMapper.createObjectNode()
                    .put("token", token)
                    .put("username", userDTOForToken.getUsername());
            ctx.json(response).status(200);
        };
    }

    @Override
    public Handler register() {
        return (Context ctx) -> {
            // 1. Vi hiver klient input ud af request
            User user = ctx.bodyAsClass(User.class);

            // 2. Alle Users får "User" role
            Role userRole = new Role("User");
            user.addRole(userRole);

            // 3. User gemmes i DB
            User createdUser = securityDAO.createUser(user.getUsername(), user.getPassword());

            // 4. Role tilføjes til User i DB
            User createdUserWithRole = securityDAO.addUserRole(createdUser.getUsername(), "User");

            // 5. Authenticate: Vi tjekker hvilke roller useren har og mapper dem til Strings
            Set<String> roles = createdUserWithRole
                    .getRoles()
                    .stream()
                    .map(role -> role.getRolename())
                    .collect(Collectors.toSet());

            // 6. Vi laver en UserDTO så det eneste data vi eksponerer er username og roles
            UserDTO userDTOForToken = new UserDTO(createdUser.getUsername(), roles);

            // 7. Vi laver et token for Useren
            String token = createToken(userDTOForToken);

            // 8. Vi bygger et Json-response og sender dem token og username
            ObjectNode response = objectMapper
                    .createObjectNode()
                    .put("token", token)
                    .put("username", userDTOForToken.getUsername());
            ctx.json(response).status(200);
        };
    }

    // Sikrer at useren er gyldig
    @Override
    public Handler authenticate() {
        return (Context ctx) -> {

            // Pre-flight request (behøves ikke authentication), sender bare en respons
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }

            // Vi henter de tilladte roller fra routes-klassen for det pågældende endpoint
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            // Vi kigger på om endpointet er åbent for alle (ANYONE), hvis true springes authentication over
            // Hvis false (dvs. der kræves en rolle), så skal User authenticates
            if (isOpenEndpoint(allowedRoles))
                return;

            // Useren bliver authenticated og tokenet bliver gemt i en UserDTO og gemt i et Context-objekt
            UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
            ctx.attribute("user", verifiedTokenUser); // -> ctx.attribute("user") in ApplicationConfig beforeMatched filter
        };
    }


    //Sikrer at useren har de rette roller
    @Override
    public Handler authorize() {
        return (Context ctx) -> {

            // Vi henter de tilladte roller for det pågældende endpoint
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            // Vi kigger på om endpointet er åbent for alle (ANYONE), hvis true springes authentication over
            // Hvis false (dvs. der kræves en rolle), så skal User authenticates
            if (isOpenEndpoint(allowedRoles))
                return;

            // Vi henter user fra context som blev gemt i authenticate()
            UserDTO user = ctx.attribute("user");
            if (user == null) {
                throw new ForbiddenResponse("No user was added from the token");
            }

            // Hvis user ikke har en af de tilladte roller kastes en exception, og ellers bliver de authorized
            if (!userHasAllowedRole(user, allowedRoles))
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        };
    }


    //Private hjælpemetoder

    private String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            // Hvis deployed
            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");

                // Hvis vi kører lokalt/dev
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }

            // Eksternt bibliotek laver token
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            //    logger.error("Could not create token", e);
            throw new ApiException(500, "Could not create token");
        }
    }

    // Henter token fra et request
    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing");
        }

        // Hvis Authorization Header er forkert formateret kastet en exception
        String token = header.split(" ")[1];
        if (token == null) {
            throw new UnauthorizedResponse("Authorization header is malformed");
        }
        return token;
    }

    // Validerer et token (String) og returnerer en User
    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token");
        }
        return verifiedTokenUser;
    }


    private UserDTO verifyToken(String token) {
        // Ser om applikationen er deployed og vælger SECRET_KEY ud for det
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ?
                System.getenv("SECRET_KEY")
                :
                Utils.getPropertyValue("SECRET_KEY", "config.properties");

        // Tjekker om Token er valid med eksternt bibliotek && om Token ikke er udløbet
        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | NotAuthorizedException | JOSEException e) {
            // logger.error("Could not create token", e);
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }


    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        // Hvis endpointet ikke har specificeret nogle roller
        if (allowedRoles.isEmpty())
            return true;

        // Tjekker om det er åbent for "ANYONE"
        if (allowedRoles.contains("ANYONE")) {
            return true;
        }

        // Hvis det indeholder roller, fx "ADMIN" eller "USER" så er endpointet ikke åbent
        return false;
    }

    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        return user.getRoles()
                .stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }
}
