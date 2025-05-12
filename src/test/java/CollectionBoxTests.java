import app.exceptions.InvalidCurrencyOrAmountException;
import app.exceptions.InvalidEventAssignmentException;
import app.exceptions.InvalidFundraisingEventUUIDException;
import app.factories.CollectionBoxFactory;
import app.models.CollectionBox;
import app.models.Currencies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionBoxTests {
    private final CollectionBoxFactory collectionBoxFactory = new CollectionBoxFactory();
    private static final String CORRECT_CURRENCY = "PLN";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    public void putMoneyValidData_ShouldPut() throws InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertEquals(CORRECT_AMOUNT, box.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    private static Stream<Arguments> putMoneyInvalidArgumentsPutMoney() {
        return Stream.of(
                Arguments.of(CORRECT_CURRENCY, -1.0),
                Arguments.of("ERR", CORRECT_AMOUNT),
                Arguments.of(null, CORRECT_AMOUNT)
        );
    }

    @ParameterizedTest
    @MethodSource("putMoneyInvalidArgumentsPutMoney")
    public void putMoneyInvalidData_ShouldThrowException(String currency, double amount) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidCurrencyOrAmountException.class, () -> box.putMoney(currency, amount));
    }

    @Test
    public void getMoneyByCurrency_ShouldReturnCorrectAmount() throws InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertEquals(CORRECT_AMOUNT, box.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    private static Object[][] getMoneyByCurrencyInvalidArguments() {
        return new Object[][]{
                {"ERR"},
                {null}
        };
    }

    @ParameterizedTest
    @MethodSource("getMoneyByCurrencyInvalidArguments")
    public void getMoneyByCurrencyInvalidData_ShouldThrowException(String currency) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidCurrencyOrAmountException.class, () -> box.getMoneyByCurrency(currency));
    }

    @Test
    public void getUuid_ShouldReturnNotNull() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertNotNull(box.getUuid());
    }

    @Test
    public void emptyBoxFully_ShouldSetAllAmountsToZero() throws InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();

        for (Currencies currency : Currencies.values()) {
            box.putMoney(currency.toString(), CORRECT_AMOUNT);
        }

        box.emptyBoxFully();

        for (Currencies currency : Currencies.values()) {
            assertEquals(0.0, box.getMoneyByCurrency(currency.toString()), 0.0);
        }
    }

    @Test
    public void isEmpty_ShouldReturnTrueWhenBoxIsEmpty() throws InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertTrue(box.isEmpty());
    }

    @Test
    public void isEmpty_ShouldReturnFalseWhenBoxIsNotEmpty() throws InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertFalse(box.isEmpty());
    }

    @Test
    public void assignFundraisingEvent_ShouldAssignCorrectEvent() throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        UUID eventId = UUID.randomUUID();
        box.assignFundraisingEvent(eventId);
        assertEquals(eventId, box.getFundraisingEvent());
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenBoxIsNotEmpty()
            throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException, InvalidCurrencyOrAmountException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        UUID eventId = UUID.randomUUID();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertThrows(InvalidEventAssignmentException.class, () -> box.assignFundraisingEvent(eventId));
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenAlreadyAssigned()
            throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        UUID eventId = UUID.randomUUID();
        box.assignFundraisingEvent(eventId);
        assertThrows(InvalidEventAssignmentException.class, () -> box.assignFundraisingEvent(eventId));
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenEventIsNull()
            throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidFundraisingEventUUIDException.class, () -> box.assignFundraisingEvent(null));
    }

    @Test
    public void isAssigned_ShouldReturnTrueWhenAssigned() throws InvalidFundraisingEventUUIDException, InvalidEventAssignmentException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        UUID eventId = UUID.randomUUID();
        box.assignFundraisingEvent(eventId);
        assertTrue(box.isAssignedToFundraisingEvent());
    }

    @Test
    public void isAssigned_ShouldReturnFalseWhenNotAssigned() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertFalse(box.isAssignedToFundraisingEvent());
    }
}