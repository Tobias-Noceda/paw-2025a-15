package ar.edu.itba.paw.webapp.config.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * If the default servlets always add a JSESSIONID cookie,
 * write your own servlet.
 */
public class SpaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final InputStream in = getServletContext().getResourceAsStream("/index.html");

        if (in == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "index.html not found");
            return;
        }

        response.setContentType("text/html");

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter out = response.getWriter()) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        }
    }
}
