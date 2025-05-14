package app.models;

import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.exceptions.fundraising_event.*;
import app.services.CurrencyConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "fundraising_events")
public class FundraisingEvent {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double accountBalance;

    @OneToOne
    @JoinColumn(name = "collection_box_id", referencedColumnName = "uuid")
    @JsonManagedReference
    private CollectionBox collectionBox;

    public FundraisingEvent(String name, String currency) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.currency = currency;
        this.accountBalance = 0.0;
    }

    public FundraisingEvent() {}

    public UUID getUuid() {
        return uuid;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public CollectionBox getCollectionBox() {
        return collectionBox;
    }

    public void assignCollectionBox(CollectionBox collectionBox)
            throws CollectionBoxException, FundraisingEventException {
        if (collectionBox == null) {
            throw new CollectionBoxDoesntExistException();
        }
        if(collectionBox.isAssignedToFundraisingEvent())
            throw new CollectionBoxAlreadyAssignedException("Collection box is already assigned to another event");
        if (this.collectionBox != null) {
            throw new CollectionBoxAlreadyAssignedException("This event already has a collection box assigned");
        }
        this.collectionBox = collectionBox;
        this.collectionBox.assignFundraisingEvent(this);
    }

    public void unregisterCollectionBox() throws CollectionBoxException, FundraisingEventException {
        if (this.collectionBox == null) {
            throw new InvalidCollectionBoxException("Collection box is not assigned to this event");
        }
        this.collectionBox.emptyBoxFully();
        this.collectionBox.unregisterFundraisingEvent();
        this.collectionBox = null;
    }

    public void transferMoney()
            throws ArgumentsException, CollectionBoxException {
        if(this.collectionBox == null) {
            throw new InvalidCollectionBoxException("Collection box is not assigned to this event");
        }
        if(this.collectionBox.isEmpty()){
            return;
        }
        for (Currencies currency : Currencies.values()) {
            Double amount = collectionBox.getMoneyByCurrency(String.valueOf(currency));
            if (amount != null) {
                if (!currency.toString().equals(this.currency)) {
                    amount = CurrencyConverter.convertCurrency(currency.toString(), this.currency, amount);
                }
                accountBalance += amount;
            }
        }
        collectionBox.emptyBoxFully();
    }
}
