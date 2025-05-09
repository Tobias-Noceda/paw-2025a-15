package ar.edu.itba.paw.models.exceptions;

public class AppointmentAlreadyTakenException extends RuntimeException {
    public AppointmentAlreadyTakenException(String message, Throwable cause) {
        super("Not found: " + message, cause);
    }

    public AppointmentAlreadyTakenException(String message) {
        this(message, null);
    }    
}
