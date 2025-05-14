import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
import app.models.CollectionBox;
import app.repositories.CollectionBoxRepository;
import app.services.CollectionBoxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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

    @Test
    void registerBox_ShouldReturnNewCollectionBox() {
        CollectionBox newBox = new CollectionBox();
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(newBox);

        CollectionBox result = collectionBoxService.registerBox();

        assertNotNull(result);
    }

    @Test
    void listAll_ShouldReturnListOfCollectionBoxes() {
        CollectionBox box1 = new CollectionBox();
        CollectionBox box2 = new CollectionBox();
        when(collectionBoxRepository.findAll()).thenReturn(List.of(box1, box2));

        List<CollectionBox> result = collectionBoxService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void putMoney_ShouldReturnUpdatedCollectionBox() throws CollectionBoxDoesntExistException, ArgumentsException {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.of(box));
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(box);

        CollectionBox result = collectionBoxService.putMoney(box.getUuid(), "PLN", 100.0);

        assertNotNull(result);
        assertEquals(100.0, result.getMoneyByCurrency("PLN"));
    }

    @Test
    void putMoney_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), "PLN", 100.0);
        });
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
    void putMoney_ShouldThrowException_WhenInvalidCurrencyOrAmount() {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.of(box));

        assertThrows(ArgumentsException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), "INVALID", -100.0);
        });
    }

    @Test
    void unregisterBox_ShouldDeleteBox() throws CollectionBoxDoesntExistException {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.existsById(any())).thenReturn(true);

        collectionBoxService.unregisterBox(box.getUuid());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.putMoney(box.getUuid(), "PLN", 100.0);
        });
    }

    @Test
    void unregisterBox_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.unregisterBox(box.getUuid());
        });
    }

    @Test
    void emptyBox_ShouldReturnUpdatedCollectionBox() throws CollectionBoxDoesntExistException {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.of(box));
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(box);

        CollectionBox result = collectionBoxService.emptyBox(box.getUuid());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyBox_ShouldThrowException_WhenBoxDoesntExist() {
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            collectionBoxService.emptyBox(box.getUuid());
        });
    }
}
