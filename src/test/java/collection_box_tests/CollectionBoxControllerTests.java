package collection_box_tests;

import app.controllers.CollectionBoxController;
import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.factories.CollectionBoxFactory;
import app.models.CollectionBox;
import app.services.CollectionBoxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionBoxControllerTests {

    @Mock
    private CollectionBoxService service;

    @InjectMocks
    private CollectionBoxController controller;

    private CollectionBox sampleBox;

    @BeforeEach
    void setUp() {
        sampleBox = CollectionBoxFactory.createCollectionBox();
    }

    private static final String CORRECT_CURRENCY = "PLN";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    void create_ShouldReturnNewBox() {
        when(service.registerBox()).thenReturn(sampleBox);

        CollectionBox result = controller.createBox();

        assertSame(sampleBox, result);
        assertEquals(sampleBox.getUuid(), result.getUuid());
        verify(service).registerBox();
    }

    @Test
    void getAll_ShouldReturnListOfBoxes() {
        List<CollectionBox> boxes = Collections.singletonList(sampleBox);
        when(service.listAll()).thenReturn(boxes);

        List<CollectionBox> result = controller.getAll();

        assertEquals(boxes, result);
        verify(service).listAll();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoBoxesExist() {
        when(service.listAll()).thenReturn(Collections.emptyList());

        List<CollectionBox> result = controller.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void delete_ShouldCallServiceToDeleteBox() throws CollectionBoxException {
        UUID boxId = sampleBox.getUuid();

        controller.delete(boxId);

        verify(service).unregisterBox(boxId);
    }

    @Test
    void delete_BoxDoesntExist_ShouldThrowException() throws CollectionBoxException {
        UUID boxId = UUID.randomUUID();

        doThrow(CollectionBoxDoesntExistException.class).when(service).unregisterBox(boxId);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.delete(boxId));

        verify(service).unregisterBox(boxId);
    }

    @Test
    void putMoney_ShouldReturnUpdatedBox() throws CollectionBoxException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();

        when(service.putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT)).thenReturn(sampleBox);

        CollectionBox result = controller.putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT);

        assertSame(sampleBox, result);
        verify(service).putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT);
    }

    @Test
    void putMoney_ValidAmount_ShouldUpdateBox() throws CollectionBoxException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();
        when(service.putMoney(boxId, CORRECT_CURRENCY, CORRECT_AMOUNT)).thenReturn(sampleBox);

        CollectionBox result = controller.putMoney(boxId, CORRECT_CURRENCY, CORRECT_AMOUNT);

        assertNotNull(result);
        verify(service).putMoney(boxId, CORRECT_CURRENCY, CORRECT_AMOUNT);
    }


    @Test
    void putMoney_BoxDoesntExist_ShouldThrowException() throws CollectionBoxException, ArgumentsException {
        UUID boxId = UUID.randomUUID();

        doThrow(CollectionBoxDoesntExistException.class).when(service).putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT));

        verify(service).putMoney(boxId,  CORRECT_CURRENCY, CORRECT_AMOUNT);
    }

    private static Stream<Arguments> invalidAmounts() {
        return Stream.of(
                Arguments.of(CORRECT_CURRENCY, -1.0),
                Arguments.of(CORRECT_CURRENCY, -100.0),
                Arguments.of(CORRECT_CURRENCY, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmounts")
    void putMoney_ShouldThrowInvalidAmountException(String currency, double amount) throws CollectionBoxException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();

        doThrow(InvalidAmountException.class).when(service).putMoney(boxId, currency, amount);

        assertThrows(InvalidAmountException.class, () -> controller.putMoney(boxId, currency, amount));

        verify(service).putMoney(boxId, currency, amount);
    }

    private static Stream<Arguments> invalidCurrencies() {
        return Stream.of(
                Arguments.of("ERR", CORRECT_AMOUNT),
                Arguments.of(null, CORRECT_AMOUNT),
                Arguments.of("", CORRECT_AMOUNT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCurrencies")
    void putMoney_ShouldThrowInvalidCurrencyException(String currency, double amount) throws CollectionBoxException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();

        doThrow(InvalidCurrencyException.class).when(service).putMoney(boxId, currency, amount);

        assertThrows(InvalidCurrencyException.class, () -> controller.putMoney(boxId, currency, amount));

        verify(service).putMoney(boxId, currency, amount);
    }

    @Test
    void empty_ShouldReturnEmptiedBox() throws Exception {
        UUID boxId = sampleBox.getUuid();
        sampleBox.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);

        when(service.emptyBox(boxId)).thenAnswer(invocation -> {
            sampleBox.emptyBoxFully();
            return sampleBox;
        });

        CollectionBox result = controller.empty(boxId);

        assertSame(sampleBox, result);
        assertEquals(0.0, result.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    @Test
    void empty_BoxDoesntExist_ShouldThrowException() throws Exception {
        UUID boxId = UUID.randomUUID();

        doThrow(CollectionBoxDoesntExistException.class).when(service).emptyBox(boxId);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.empty(boxId));

        verify(service).emptyBox(boxId);
    }
}