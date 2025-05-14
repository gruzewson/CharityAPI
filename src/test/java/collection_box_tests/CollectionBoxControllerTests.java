package collection_box_tests;

import app.controllers.CollectionBoxController;
import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
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
        sampleBox = new CollectionBox();
    }

    @Test
    void create_ShouldReturnNewBox() {
        when(service.registerBox()).thenReturn(sampleBox);

        CollectionBox result = controller.createBox();

        assertSame(sampleBox, result);
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
    void delete_ShouldCallServiceToDeleteBox() throws CollectionBoxDoesntExistException {
        UUID boxId = sampleBox.getUuid();

        controller.delete(boxId);

        verify(service).unregisterBox(boxId);
    }

    @Test
    void delete_BoxDoesntExist_ShouldThrowException() throws CollectionBoxDoesntExistException {
        UUID boxId = UUID.randomUUID();

        doThrow(CollectionBoxDoesntExistException.class).when(service).unregisterBox(boxId);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.delete(boxId));

        verify(service).unregisterBox(boxId);
    }

    @Test
    void putMoney_ShouldReturnUpdatedBox() throws CollectionBoxDoesntExistException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();
        String currency = "PLN";
        double amount = 100.0;

        when(service.putMoney(boxId, currency, amount)).thenReturn(sampleBox);

        CollectionBox result = controller.putMoney(boxId, currency, amount);

        assertSame(sampleBox, result);
        verify(service).putMoney(boxId, currency, amount);
    }

    @Test
    void putMoney_BoxDoesntExist_ShouldThrowException() throws CollectionBoxDoesntExistException, ArgumentsException {
        UUID boxId = UUID.randomUUID();
        String currency = "PLN";
        double amount = 100.0;

        doThrow(CollectionBoxDoesntExistException.class).when(service).putMoney(boxId, currency, amount);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.putMoney(boxId, currency, amount));

        verify(service).putMoney(boxId, currency, amount);
    }

    private static Stream<Arguments> putMoneyInvalidArguments() {
        String CORRECT_CURRENCY = "PLN";
        double CORRECT_AMOUNT = 100.0;
        return Stream.of(
                Arguments.of(CORRECT_CURRENCY, -1.0),
                Arguments.of("ERR", CORRECT_AMOUNT),
                Arguments.of(null, CORRECT_AMOUNT)
        );
    }

    @ParameterizedTest
    @MethodSource("putMoneyInvalidArguments")
    void putMoney_ShouldThrowException_WhenInvalidCurrencyOrAmount(String currency, double amount) throws CollectionBoxDoesntExistException, ArgumentsException {
        UUID boxId = sampleBox.getUuid();

        doThrow(ArgumentsException.class).when(service).putMoney(boxId, currency, amount);

        assertThrows(ArgumentsException.class, () -> controller.putMoney(boxId, currency, amount));

        verify(service).putMoney(boxId, currency, amount);
    }

    @Test
    void empty_ShouldReturnEmptiedBox() throws Exception {
        UUID boxId = sampleBox.getUuid();
        sampleBox.putMoney("PLN", 100.0);

        when(service.emptyBox(boxId)).thenAnswer(invocation -> {
            sampleBox.emptyBoxFully();
            return sampleBox;
        });

        CollectionBox result = controller.empty(boxId);

        assertSame(sampleBox, result);
        assertEquals(0.0, result.getMoneyByCurrency("PLN"));
    }

    @Test
    void empty_BoxDoesntExist_ShouldThrowException() throws Exception {
        UUID boxId = UUID.randomUUID();

        doThrow(CollectionBoxDoesntExistException.class).when(service).emptyBox(boxId);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.empty(boxId));

        verify(service).emptyBox(boxId);
    }
}