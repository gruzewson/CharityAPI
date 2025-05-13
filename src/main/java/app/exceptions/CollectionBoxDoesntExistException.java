package app.exceptions;

public class CollectionBoxDoesntExistException extends Exception {
    public CollectionBoxDoesntExistException() {
        super("Collection box does not exist");
    }
}
