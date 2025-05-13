package app.models;

import app.exceptions.*;
import app.services.CurrencyConverter;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "fundraising_events")
public class FundraisingEvent {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double accountBalance;

    @OneToOne
    @JoinColumn(name = "collection_box_id", referencedColumnName = "uuid")
    private CollectionBox collectionBox;



    public FundraisingEvent() {}

    public FundraisingEvent(String name, String currency) {
        this.name = name;
        this.currency = currency;
        this.accountBalance = 0.0;
    }

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
            throws InvalidEventAssignmentException, InvalidCollectionBoxException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        if (collectionBox == null) {
            throw new InvalidCollectionBoxException("Collection box cannot be null");
        }
        if(collectionBox.isAssignedToFundraisingEvent())
            throw new CollectionBoxAlreadyAssignedException("Collection box is already assigned to another event");
        if (this.collectionBox != null) {
            throw new CollectionBoxAlreadyAssignedException("This event already has a collection box assigned");
        }
        this.collectionBox = collectionBox;
        this.collectionBox.assignFundraisingEvent(this);
    }

    public void unregisterCollectionBox() throws InvalidCollectionBoxException, InvalidFundraisingEventUUIDException {
        if (this.collectionBox == null) {
            throw new InvalidCollectionBoxException("Collection box is not assigned to this event");
        }
        this.collectionBox.emptyBoxFully();
        this.collectionBox.unregisterFundraisingEvent();
        this.collectionBox = null;
    }

    public void transferMoney()
            throws InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException {
        if(this.collectionBox == null) {
            throw new CollectionBoxDoesntExistException();
        }
        if(this.collectionBox.isEmpty()){
            return;
        }
        CurrencyConverter currencyConverter = new CurrencyConverter();
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
