package app.exceptions.arguments;

public class InvalidCurrencyException extends ArgumentsException {
    public InvalidCurrencyException(String currency) {
        super("Invalid currency: " + currency);
    }
}

