package collection_box_tests;

import app.exceptions.arguments.ArgumentsException;
import app.exceptions.arguments.InvalidAmountException;
import app.exceptions.arguments.InvalidCurrencyException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
import app.exceptions.collection_box.CollectionBoxException;
import app.factories.CollectionBoxFactory;
import app.models.CollectionBox;
import app.repositories.CollectionBoxRepository;
import app.services.CollectionBoxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollectionBoxServiceTests {

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    @InjectMocks
    private CollectionBoxService collectionBoxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String CORRECT_CURRENCY = "PLN";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    void registerBox_ShouldReturnNewCollectionBox() {
        CollectionBox newBox = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(newBox);

        CollectionBox result = collectionBoxService.registerBox();

        assertNotNull(result);
        assertEquals(newBox, result);
    }

    @Test
    void listAll_ShouldReturnListOfCollectionBoxes() {
        CollectionBox box1 = CollectionBoxFactory.createCollectionBox();
        CollectionBox box2 = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findAll()).thenReturn(List.of(box1, box2));

        List<CollectionBox> result = collectionBoxService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(box1, result.get(0));
        assertEquals(box2, result.get(1));
    }

    @Test
    void putMoney_ShouldReturnUpdatedCollectionBox() throws CollectionBoxException, ArgumentsException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(Optional.of(box));
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(box);

        CollectionBox result = collectionBoxService.putMoney(box.getUuid(), CORRECT_CURRENCY, CORRECT_AMOUNT);

        assertNotNull(result);
        assertEquals(CORRECT_AMOUNT, result.getMoneyByCurrency(CORRECT_CURRENCY));
    }

    @Test
    void putMoney_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), CORRECT_CURRENCY, CORRECT_AMOUNT);
        });
    }

    private static Stream<Double> invalidAmounts() {
        return Stream.of(-1.0, -100.5, 0.0);
    }

    @ParameterizedTest
    @MethodSource("invalidAmounts")
    void putMoney_ShouldThrowInvalidAmountException(double amount) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any()))
                .thenReturn(Optional.of(box));

        assertThrows(InvalidAmountException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), CORRECT_CURRENCY, amount);
        });
    }

    private static Stream<String> invalidCurrencies() {
        return Stream.of("ERR", null, "");
    }

    @ParameterizedTest
    @MethodSource("invalidCurrencies")
    void putMoney_ShouldThrowInvalidCurrencyException(String currency) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any()))
                .thenReturn(Optional.of(box));

        assertThrows(InvalidCurrencyException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), currency, CORRECT_AMOUNT);
        });
    }

    @Test
    void unregisterBox_ShouldDeleteBox() throws CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(box.getUuid()))
                .thenReturn(Optional.of(box));

        collectionBoxService.unregisterBox(box.getUuid());

        verify(collectionBoxRepository).delete(box);
    }

    @Test
    void unregisterBox_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.unregisterBox(box.getUuid());
        });
    }

    @Test
    void emptyBox_ShouldReturnUpdatedCollectionBox() throws CollectionBoxException {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(Optional.of(box));
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(box);

        CollectionBox result = collectionBoxService.emptyBox(box.getUuid());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyBox_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.emptyBox(box.getUuid());
        });
    }
}
