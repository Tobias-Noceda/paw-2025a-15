package ar.edu.itba.paw.models.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message) {
        super(message, null);
    }
}
