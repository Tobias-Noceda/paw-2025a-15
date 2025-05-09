package ar.edu.itba.paw.models.exceptions;

public class MediaTypeException extends RuntimeException {
    
    public MediaTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MediaTypeException(String message) {
        super(message, null);
    }
}
