package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Path("/hello")
@Component
public class JerseyHelloWorld {
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response sayHello() {
        return Response.ok(
            "{\"message\":\"Hello, World!\"}"
        ).build();
    }
}
