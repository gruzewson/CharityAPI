package collection_box_tests;

import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.exceptions.fundraising_event.*;
import app.factories.CollectionBoxFactory;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.Currencies;
import app.models.FundraisingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionBoxTests {
    private static final String CORRECT_CURRENCY = "PLN";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    public void putMoneyValidData_ShouldPut() throws ArgumentsException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertEquals(CORRECT_AMOUNT, box.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    static Stream<Double> invalidAmountArguments() {
        return Stream.of(-1.0, -100.5);
    }

    @ParameterizedTest
    @MethodSource("invalidAmountArguments")
    public void putMoney_NegativeAmount_ShouldThrowInvalidAmount(Double amount) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidAmountException.class,
                () -> box.putMoney(CORRECT_CURRENCY, amount));
    }

    static Stream<String> invalidCurrencyArguments() {
        return Stream.of(null, "UNKNOWN");
    }

    @ParameterizedTest
    @MethodSource("invalidCurrencyArguments")
    public void putMoney_InvalidCurrency_ShouldThrowInvalidCurrency(String currency) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidCurrencyException.class,
                () -> box.putMoney(currency, CORRECT_AMOUNT));
    }

    @Test
    public void getMoneyByCurrency_ShouldReturnCorrectAmount() throws ArgumentsException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertEquals(CORRECT_AMOUNT, box.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    private static Stream<Arguments> getMoneyByCurrencyInvalidArguments() {
        return Stream.of(
                Arguments.of("ERR"),
                Arguments.of((String) null)
        );
    }


    @ParameterizedTest
    @MethodSource("getMoneyByCurrencyInvalidArguments")
    public void getMoneyByCurrencyInvalidData_ShouldThrowException(String currency) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidCurrencyException.class, () -> box.getMoneyByCurrency(currency));
    }

    @Test
    public void getUuid_ShouldReturnNotNull() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertNotNull(box.getUuid());
    }

    @Test
    public void emptyBoxFully_ShouldSetAllAmountsToZero() throws ArgumentsException {
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
    public void isEmpty_ShouldReturnTrueWhenBoxIsEmpty() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertTrue(box.isEmpty());
    }

    @Test
    public void isEmpty_ShouldReturnFalseWhenBoxIsNotEmpty() throws ArgumentsException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertFalse(box.isEmpty());
    }

    @Test
    public void assignFundraisingEvent_ShouldAssignCorrectEvent() throws FundraisingEventException, CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        box.assignFundraisingEvent(event);
        assertEquals(event, box.getFundraisingEvent());
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenBoxIsNotEmpty() throws ArgumentsException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertThrows(InvalidCollectionBoxException.class, () -> box.assignFundraisingEvent(event));
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenAlreadyAssigned()
            throws FundraisingEventException, CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        box.assignFundraisingEvent(event);
        assertThrows(InvalidEventAssignmentException.class, () -> box.assignFundraisingEvent(event));
    }

    @Test
    public void assignFundraisingEvent_ShouldNotAssignWhenEventIsNull(){
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidFundraisingEventException.class, () -> box.assignFundraisingEvent(null));
    }

    @Test
    public void isAssigned_ShouldReturnTrueWhenAssigned() throws FundraisingEventException, CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        box.assignFundraisingEvent(event);
        assertTrue(box.isAssignedToFundraisingEvent());
    }

    @Test
    public void isAssigned_ShouldReturnFalseWhenNotAssigned() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertFalse(box.isAssignedToFundraisingEvent());
    }

    @Test
    public void unregisterFundraisingEvent_ShouldUnassign() throws FundraisingEventException, CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        box.assignFundraisingEvent(event);
        box.unregisterFundraisingEvent();
        assertNull(box.getFundraisingEvent());
    }

    @Test
    public void unregisterFundraisingEvent_ShouldThrowExceptionWhenNotAssigned() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(InvalidFundraisingEventException.class, () -> box.unregisterFundraisingEvent());
    }
}