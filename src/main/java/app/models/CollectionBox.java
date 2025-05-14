package app.models;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import app.exceptions.arguments.ArgumentsException;
import app.exceptions.arguments.InvalidAmountException;
import app.exceptions.arguments.InvalidCurrencyException;
import app.exceptions.collection_box.CollectionBoxException;
import app.exceptions.collection_box.InvalidCollectionBoxException;
import app.exceptions.fundraising_event.FundraisingEventException;
import app.exceptions.fundraising_event.InvalidEventAssignmentException;
import app.exceptions.fundraising_event.InvalidFundraisingEventException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "collection_boxes")
public class CollectionBox {
    @Id
    private UUID uuid;

    @OneToOne(mappedBy = "collectionBox")
    @JsonBackReference
    private FundraisingEvent fundraisingEvent;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "collection_box_money",
            joinColumns = @JoinColumn(name = "collection_box_id")
    )
    @MapKeyColumn(name = "currency")
    @Column(name = "amount", nullable = false)
    private Map<String, Double> money = new Hashtable<>();

    public CollectionBox() {
        this.uuid = UUID.randomUUID();
        for (Currencies currency : Currencies.values()) {
            money.put(currency.toString(), 0.0);
        }
    }

    public void putMoney(String currency, double amount) throws ArgumentsException {
        if (amount < 0) {
            throw new InvalidAmountException(amount);
        }
        if (currency == null || !money.containsKey(currency)) {
            throw new InvalidCurrencyException(currency);
        }
        money.put(currency, amount);
    }

    public Double getMoneyByCurrency(String currency) throws ArgumentsException {
        if (currency == null || !money.containsKey(currency)) {
            throw new InvalidCurrencyException(currency);
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

    public void assignFundraisingEvent(FundraisingEvent newFundraisingEvent)
            throws FundraisingEventException, CollectionBoxException {
        if (newFundraisingEvent == null) {
            throw new InvalidFundraisingEventException("Fundraising event cannot be null");
        }
        if (this.fundraisingEvent != null) {
            throw new InvalidEventAssignmentException("Fundraising event is already assigned");
        }
        if(!this.isEmpty())
        {
            throw new InvalidCollectionBoxException("Collection box is not empty");
        }
        this.fundraisingEvent = newFundraisingEvent;
    }

    public void unregisterFundraisingEvent() throws FundraisingEventException {
        if (this.fundraisingEvent == null) {
            throw new InvalidFundraisingEventException("Fundraising event is not assigned");
        }
        this.fundraisingEvent = null;
    }

    public FundraisingEvent getFundraisingEvent() {
        return fundraisingEvent;
    }

    public Boolean isAssignedToFundraisingEvent() {
        return fundraisingEvent != null;
    }
}
