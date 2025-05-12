import app.factories.CollectionBoxFactory;
import app.models.CollectionBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionBoxTests {
    private final CollectionBoxFactory collectionBoxFactory = new CollectionBoxFactory();
    private static final String CORRECT_CURRENCY = "PLN";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    public void putMoneyValidData_ShouldPut() {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        assertEquals(CORRECT_AMOUNT, box.getMoney(CORRECT_CURRENCY), 100.0);
    }

    private static Stream<Arguments> wrongInputs() {
        return Stream.of(
                Arguments.of(CORRECT_CURRENCY, -1.0),
                Arguments.of("ERR", CORRECT_AMOUNT),
                Arguments.of(null, CORRECT_AMOUNT)
        );
    }

    @ParameterizedTest
    @MethodSource("wrongInputs")
    public void putMoneyInvalidData_ShouldThrowException(String currency, double amount) {
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        assertThrows(IllegalArgumentException.class, () -> box.putMoney(currency, amount));
    }
}