package app.exceptions;

public class FundraisingEventDoesntExistException extends Exception {
    public FundraisingEventDoesntExistException() {
        super("Fundraising event does not exist");
    }
}
