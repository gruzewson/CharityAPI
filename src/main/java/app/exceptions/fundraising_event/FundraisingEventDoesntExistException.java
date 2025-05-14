package app.exceptions.fundraising_event;

public class FundraisingEventDoesntExistException extends FundraisingEventException {
    public FundraisingEventDoesntExistException() {
        super("Fundraising event does not exist");
    }
}
