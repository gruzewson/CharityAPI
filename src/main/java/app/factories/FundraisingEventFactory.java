package app.factories;

import app.models.FundraisingEvent;

public class FundraisingEventFactory {
    public static FundraisingEvent createFundraisingEvent() {
        return new FundraisingEvent();
    }

    public static FundraisingEvent createFundraisingEvent(String name, String currency) {
        return new FundraisingEvent(name, currency);
    }
}
