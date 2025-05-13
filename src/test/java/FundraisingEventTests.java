import app.exceptions.*;
import app.factories.CollectionBoxFactory;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.FundraisingEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FundraisingEventTests {
    @Test
    public void assignCollectionBox_ShouldAssign()
            throws InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);

        assertEquals(collectionBox, event.getCollectionBox());
        assertEquals(event, collectionBox.getFundraisingEvent());
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenEventAlreadyHasCollectionBox()
            throws InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox1 = CollectionBoxFactory.createCollectionBox();
        CollectionBox collectionBox2 = CollectionBoxFactory.createCollectionBox();

        event.assignCollectionBox(collectionBox1);

        assertThrows(CollectionBoxAlreadyAssignedException.class, () -> {
            event.assignCollectionBox(collectionBox2);
        });
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenCollectionBoxIsNull()
            throws InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();

        assertThrows(InvalidCollectionBoxException.class, () -> {
            event.assignCollectionBox(null);
        });
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenCollectionBoxIsAlreadyAssigned()
            throws InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException, InvalidCollectionBoxException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        FundraisingEvent event2 = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event2.assignCollectionBox(collectionBox);

        assertThrows(CollectionBoxAlreadyAssignedException.class, () -> {
            event.assignCollectionBox(collectionBox);
        });
    }

    @Test
    public void unregisterCollectionBox_ShouldUnassign()
            throws InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);
        event.unregisterCollectionBox();

        assertNull(event.getCollectionBox());
        assertNull(collectionBox.getFundraisingEvent());
    }

    @Test
    public void unregisterCollectionBox_ShouldThrowException_WhenNoCollectionBoxAssigned()
            throws InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();

        assertThrows(InvalidCollectionBoxException.class, () -> {
            event.unregisterCollectionBox();
        });
    }

    @Test
    public void transferMoney_ShouldTransferMoney()
            throws InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, InvalidCollectionBoxException, CollectionBoxAlreadyAssignedException, InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);
        collectionBox.putMoney("EUR", 100.0);
        event.transferMoney();

        assertEquals(450.0, event.getAccountBalance());
        assertEquals(0.0, collectionBox.getMoneyByCurrency("EUR"));
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenNoCollectionBoxAssigned()
    {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            event.transferMoney();
        });
    }

    @Test
    public void transferMoney_ShouldNotTransferMoney_WhenCollectionBoxIsEmpty()
            throws InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, InvalidCollectionBoxException, CollectionBoxAlreadyAssignedException, InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);
        event.transferMoney();

        // Assert that the account balance is still 0.0
        assertEquals(0.0, event.getAccountBalance());
    }
}
