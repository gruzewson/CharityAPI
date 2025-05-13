package app.exceptions;

public class InvalidCollectionBoxException extends Exception {
    public InvalidCollectionBoxException(String errorMessage) {
        super(errorMessage);
    }
}