package app.exceptions.collection_box;

public class InvalidCollectionBoxException extends CollectionBoxException {
    public InvalidCollectionBoxException(String errorMessage) {
        super(errorMessage);
    }
}