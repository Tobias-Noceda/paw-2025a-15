package ar.edu.itba.paw.models.exceptions;

public  class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Throwable cause) {
        super("Not found: " + message, cause);
    }

    public NotFoundException(String message) {
        this(message, null);
    }    
}
