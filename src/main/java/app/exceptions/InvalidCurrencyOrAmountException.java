package app.exceptions;

public class InvalidCurrencyOrAmountException extends Exception {
    public InvalidCurrencyOrAmountException(String errorMessage) {
        super(errorMessage);
    }
}

