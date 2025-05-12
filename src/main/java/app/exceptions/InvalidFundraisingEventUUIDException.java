package app.exceptions;

public class InvalidFundraisingEventUUIDException extends Exception {
    public InvalidFundraisingEventUUIDException(String errorMessage) {
        super(errorMessage);
    }
}