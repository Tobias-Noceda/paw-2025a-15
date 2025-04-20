package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String errorMessage;

        // Verificar si el usuario existe
        if (username != null && !username.isEmpty() && userService.getUserByEmail(username).isEmpty()) {
            errorMessage = "userNotFound"; // Clave para el mensaje "El usuario no existe"
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "credentialsNotFound"; // Por ejemplo, si no se proporcionan credenciales
        } else {
            errorMessage = "invalidCredentials"; // Contraseña incorrecta u otro error
        }

        // Redirigir a /login con el parámetro de error específico
        setDefaultFailureUrl("/login?error=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}