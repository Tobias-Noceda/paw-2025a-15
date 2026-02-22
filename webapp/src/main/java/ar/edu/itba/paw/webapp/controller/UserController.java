package ar.edu.itba.paw.webapp.controller;

import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.input.NewPassWordRequestDto;
import ar.edu.itba.paw.webapp.dto.input.NewPasswordSetDto;
import ar.edu.itba.paw.webapp.dto.output.UserDTO;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/users")
@Component
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final long MAIL_TOKEN_EXPIRY_TIME = TimeUnit.HOURS.toMillis(2);

    @Autowired
    private UserService us;

    @Autowired
    private JwtTokenUtil jtu;

    @Context
    private UriInfo uriInfo;
    
    @GET
    @Path("/{id:\\d+}")
    public Response getSelf(@PathParam("id") Long id) {
        User user = us.getUserById(id).orElse(null);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(UserDTO.fromUser(user, uriInfo)).build();
    }

    @POST
    @Consumes(value = VndType.APPLICATION_PASSWORD_CREATE)
    public Response createToken(
        @Valid @NonEmptyBody NewPassWordRequestDto newPassRequestDto
    ) {
        final String token = jtu.createMailToken(newPassRequestDto.getEmail(), MAIL_TOKEN_EXPIRY_TIME);
        try {
            us.askPasswordRecover(newPassRequestDto.getEmail(), token);
        } catch (NotFoundException e) {
            LOGGER.info(e.getMessage());
        }

        return Response.created(uriInfo.getBaseUriBuilder().path(UserController.class).path(token).build()).build();
    }

    @PUT
    @Path("/{token}")
    @Consumes(value = VndType.APPLICATION_PASSWORD_RESET)
    public Response resetPassword(
        @PathParam("token") String token,
        @Valid @NonEmptyBody NewPasswordSetDto newPassSetDto
    ) {
        JwtTokenUtil.SessionInfo sessionInfo = jtu.parse(token);

        if (sessionInfo == null) {
            LOGGER.info("Invalid password reset token: {}", token);
            return Response.accepted().build();
        }

        if (sessionInfo.expired()) {
            return Response.status(Response.Status.GONE).build();
        }

        us.changePasswordByID(sessionInfo.user().getUser().getId(), newPassSetDto.getNewPassword());

        return Response.accepted().build();
    }
}
