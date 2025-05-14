package fundraising_event_tests;

import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.exceptions.fundraising_event.*;
import app.factories.CollectionBoxFactory;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.FundraisingEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FundraisingEventTests {
    @Test
    public void assignCollectionBox_ShouldAssign()
            throws CollectionBoxException, FundraisingEventException   {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);

        assertEquals(collectionBox, event.getCollectionBox());
        assertEquals(event, collectionBox.getFundraisingEvent());
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenEventAlreadyHasCollectionBox()
            throws CollectionBoxException, FundraisingEventException   {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox1 = CollectionBoxFactory.createCollectionBox();
        CollectionBox collectionBox2 = CollectionBoxFactory.createCollectionBox();

        event.assignCollectionBox(collectionBox1);

        assertThrows(CollectionBoxAlreadyAssignedException.class, () -> {
            event.assignCollectionBox(collectionBox2);
        });
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenCollectionBoxIsNull() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            event.assignCollectionBox(null);
        });
    }

    @Test
    public void assignCollectionBox_ShouldThrowException_WhenCollectionBoxIsAlreadyAssigned()
            throws FundraisingEventException, CollectionBoxException {
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
            throws CollectionBoxException, FundraisingEventException  {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);
        event.unregisterCollectionBox();

        assertNull(event.getCollectionBox());
        assertNull(collectionBox.getFundraisingEvent());
    }

    @Test
    public void unregisterCollectionBox_ShouldThrowException_WhenNoCollectionBoxAssigned() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();

        assertThrows(InvalidCollectionBoxException.class, () -> {
            event.unregisterCollectionBox();
        });
    }

    @Test
    public void transferMoney_ShouldTransferMoney()
            throws FundraisingEventException, CollectionBoxException, ArgumentsException {
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

        assertThrows(InvalidCollectionBoxException.class, () -> {
            event.transferMoney();
        });
    }

    @Test
    public void transferMoney_ShouldNotTransferMoney_WhenCollectionBoxIsEmpty()
            throws FundraisingEventException, CollectionBoxException, ArgumentsException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");
        CollectionBox collectionBox = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(collectionBox);
        event.transferMoney();

        // Assert that the account balance is still 0.0
        assertEquals(0.0, event.getAccountBalance());
    }
}
