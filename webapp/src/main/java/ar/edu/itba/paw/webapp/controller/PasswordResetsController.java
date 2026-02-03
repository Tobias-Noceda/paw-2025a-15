package ar.edu.itba.paw.webapp.controller;

import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.input.NewPassWordRequestDto;
import ar.edu.itba.paw.webapp.dto.input.NewPasswordSetDto;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

@Path("/password-resets")
@Component
public class PasswordResetsController {

    private static final long MAIL_TOKEN_EXPIRY_TIME = TimeUnit.HOURS.toMillis(2);

    @Autowired
    private UserService us;

    @Autowired
    private EmailService es;

    @Autowired
    private JwtTokenUtil jtu;

    @Context
    private UriInfo uriInfo;
    
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response createToken(
        @Valid @NonEmptyBody NewPassWordRequestDto newPassRequestDto
    ) {
        final String token = jtu.createMailToken(newPassRequestDto.getEmail(), MAIL_TOKEN_EXPIRY_TIME);
        
        final User user = us.getUserByEmail(newPassRequestDto.getEmail()).orElse(null);
        if (user != null) {
            es.sendPasswordResetEmail(user, token);
        }

        return Response.created(uriInfo.getBaseUriBuilder().path("password-resets").path(token).build()).build();
    }

    @PUT
    @Path("/{token}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response resetPassword(
        @PathParam("token") String token,
        @Valid @NonEmptyBody NewPasswordSetDto newPassSetDto
    ) {
        JwtTokenUtil.SessionInfo sessionInfo = jtu.parse(token);

        if (sessionInfo == null) {
            return Response.accepted().build();
        }

        if (sessionInfo.expired()) {
            return Response.status(Response.Status.GONE).build();
        }

        final User user = us.getUserByEmail(sessionInfo.user().getUsername()).orElse(null);
        if (user == null) {
            return Response.accepted().build();
        }

        us.changePasswordByID(user.getId(), newPassSetDto.getNewPassword());

        return Response.accepted().build();
    }
}
