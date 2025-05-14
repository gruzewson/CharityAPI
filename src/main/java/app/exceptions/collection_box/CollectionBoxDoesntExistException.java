package app.exceptions.collection_box;

public class CollectionBoxDoesntExistException extends CollectionBoxException {
    public CollectionBoxDoesntExistException() {
        super("Collection box does not exist");
    }
}
