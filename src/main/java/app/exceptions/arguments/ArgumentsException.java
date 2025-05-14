package app.exceptions.arguments;

public abstract class ArgumentsException extends Exception {
    protected ArgumentsException(String message) {
        super(message);
    }
}
