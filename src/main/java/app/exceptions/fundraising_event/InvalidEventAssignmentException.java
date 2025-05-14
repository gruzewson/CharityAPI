package app.exceptions.fundraising_event;

public class InvalidEventAssignmentException extends FundraisingEventException {
    public InvalidEventAssignmentException(String errorMessage) {
        super(errorMessage);
    }
}

