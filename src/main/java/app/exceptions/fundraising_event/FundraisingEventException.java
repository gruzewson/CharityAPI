package app.exceptions.fundraising_event;


public abstract class FundraisingEventException extends Exception {
    protected FundraisingEventException(String message) {
        super(message);
    }
}
