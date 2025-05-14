package app.exceptions.fundraising_event;

public class InvalidFundraisingEventException extends FundraisingEventException {
    public InvalidFundraisingEventException(String errorMessage) {
        super(errorMessage);
    }
}