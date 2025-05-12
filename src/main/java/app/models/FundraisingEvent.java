package app.models;

import java.util.UUID;

public class FundraisingEvent {
    private String name;
    private UUID uuid;
    private String Currency;
    private Double AccountBalance;
    private CollectionBox collectionBox;

    public FundraisingEvent(String name, String currency) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        Currency = currency;
        AccountBalance = 0.0;
    }

    public Double getAccountBalance() {
        return AccountBalance;
    }

    public void assignCollectionBox(UUID uuid) {

    }
}
