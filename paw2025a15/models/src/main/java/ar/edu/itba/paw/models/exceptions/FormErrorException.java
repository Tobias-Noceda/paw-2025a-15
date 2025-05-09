package ar.edu.itba.paw.models.exceptions;

public class FormErrorException  extends RuntimeException {

    public FormErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormErrorException(String message) {
        super(message, null);
    }
    
}
