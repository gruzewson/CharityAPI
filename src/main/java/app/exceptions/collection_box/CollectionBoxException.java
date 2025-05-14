package app.exceptions.collection_box;

public abstract class CollectionBoxException extends Exception {
    protected CollectionBoxException(String message) {
        super(message);
    }
}
