package ar.edu.itba.paw.webapp.controller;

import java.net.http.HttpConnectTimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import ar.edu.itba.paw.models.exceptions.*;

@ControllerAdvice
public class ErrorControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        IllegalArgumentException.class,
        AlreadyExistsException.class,
        AppointmentAlreadyTakenException.class,
        FormErrorException.class
    })
    public ModelAndView handle400Exception() {
        ModelAndView mav = new ModelAndView("errorPages/400");
        return mav;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
        AccessDeniedException.class
    })
    public ModelAndView handle403Exception() {
        return new ModelAndView("403");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        NotFoundException.class,
        NoHandlerFoundException.class,
        UnauthorizedException.class
    })
    public ModelAndView handle404Exception() {
        ModelAndView mav = new ModelAndView("errorPages/404");
        return mav;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handle405Exception() {
        return new ModelAndView("errorPages/405");
    }

    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(HttpConnectTimeoutException.class)
    public ModelAndView handle408Exception() {
        return new ModelAndView("errorPages/408");
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(MediaTypeException.class)
    public ModelAndView handle415Exception() {
        return new ModelAndView("errorPages/415");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
        Exception.class,
        RuntimeException.class
    })
    public ModelAndView handle500Exception() {
        return new ModelAndView("errorPages/500");
    }
}
