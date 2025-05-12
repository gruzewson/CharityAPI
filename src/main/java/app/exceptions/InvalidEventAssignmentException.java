package app.exceptions;

public class InvalidEventAssignmentException extends Exception {
    public InvalidEventAssignmentException(String errorMessage) {
        super(errorMessage);
    }
}

