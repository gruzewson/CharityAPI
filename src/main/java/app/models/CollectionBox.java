package app.models;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class CollectionBox {
    private UUID uuid;
    private UUID FundraisingEvent;
    private Map<String, Double> money = new Hashtable<>();

    public CollectionBox() {
        UUID uuid = UUID.randomUUID();
        for(int i = 0; i < Currencies.values().length; i++)
        {
            money.put(Currencies.values()[i].toString(), 0.0);
        }
    }

    public void putMoney(String currency, double amount)
    {
        if (amount < 0 || currency == null) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (!money.containsKey(currency)) {
            throw new IllegalArgumentException("Invalid currency");
        }
        money.put(currency, amount);
    }


    public Double getMoney(String currency)
    {
        return money.get(currency);
    }
}
