package app.exceptions.arguments;

public class InvalidAmountException extends ArgumentsException {
    public InvalidAmountException(Double amount) {
        super("Invalid amount: " + amount);
    }
}
