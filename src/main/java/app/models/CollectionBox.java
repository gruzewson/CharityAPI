package app.models;

import app.exceptions.InvalidCurrencyOrAmountException;
import app.exceptions.InvalidEventAssignmentException;
import app.exceptions.InvalidFundraisingEventUUIDException;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "collection_boxes")
public class CollectionBox {
    @Id
    @Column(columnDefinition = "UUID")
    private final UUID uuid;

    @Column(name = "fundraising_event", columnDefinition = "UUID")
    private UUID FundraisingEvent;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "collection_box_money",
            joinColumns = @JoinColumn(
                    name            = "collection_box_id",
                    referencedColumnName = "uuid"
            )
    )
    @MapKeyColumn(name = "currency")
    @Column(name = "amount", nullable = false)
    private final Map<String, Double> money = new Hashtable<>();

    public CollectionBox() {
        this.uuid = UUID.randomUUID();
        for(int i = 0; i < Currencies.values().length; i++)
        {
            money.put(Currencies.values()[i].toString(), 0.0);
        }
    }

    public void putMoney(String currency, double amount) throws InvalidCurrencyOrAmountException {
        if (amount < 0 || currency == null) {
            throw new InvalidCurrencyOrAmountException("Invalid amount");
        }
        if (!money.containsKey(currency)) {
            throw new InvalidCurrencyOrAmountException("Invalid currency");
        }
        money.put(currency, amount);
    }

    public Double getMoneyByCurrency(String currency) throws InvalidCurrencyOrAmountException {
        if (currency == null || !money.containsKey(currency)) {
            throw new InvalidCurrencyOrAmountException("Invalid currency");
        }
        return money.get(currency);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void emptyBoxFully() {
        money.replaceAll((c, v) -> 0.0);
    }

    public Boolean isEmpty(){
        for(Currencies currency : Currencies.values())
        {
            if(money.get(currency.toString()) != 0.0)
            {
                return false;
            }
        }
        return true;
    }

    public void assignFundraisingEvent(UUID fundraisingEvent) throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException {
        if (fundraisingEvent == null) {
            throw new InvalidFundraisingEventUUIDException("Fundraising event cannot be null");
        }
        if (this.FundraisingEvent != null) {
            throw new InvalidEventAssignmentException("Fundraising event is already assigned");
        }
        if(!this.isEmpty())
        {
            throw new InvalidEventAssignmentException("Collection box is not empty");
        }
        this.FundraisingEvent = fundraisingEvent;
    }

    public UUID getFundraisingEvent() {
        return FundraisingEvent;
    }

    public Boolean isAssignedToFundraisingEvent() {
        return FundraisingEvent != null;
    }
}
