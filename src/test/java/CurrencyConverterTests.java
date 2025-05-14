import app.exceptions.arguments.InvalidCurrencyException;
import app.services.CurrencyConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyConverterTests {

    private static Stream<Arguments> convertCurrencyArguments() {
        return Stream.of(
                Arguments.of("EUR", "PLN", 100.0, 450.0),
                Arguments.of("PLN", "EUR", 100.0, 22.0),
                Arguments.of("PLN", "PLN",  50.0,  50.0)
        );
    }

    @ParameterizedTest
    @MethodSource("convertCurrencyArguments")
    public void convertCurrency_ShouldConvertCorrectly(
            String from, String to, double amount, double expected) throws InvalidCurrencyException {
        assertEquals(expected, CurrencyConverter.convertCurrency(from, to, amount));
    }

    @Test
    public void convertCurrency_ShouldReturnSameAmount_WhenSameCurrency() throws InvalidCurrencyException {
        double amount = 100.0;
        double convertedAmount = CurrencyConverter.convertCurrency("PLN", "PLN", amount);
        assertEquals(100.0, convertedAmount);
    }

    @Test
    public void convertCurrency_ShouldThrowException_WhenExchangeRateNotAvailable() {
        double amount = 100.0;
        String wrongCurrency = "USD";
        String toCurrency = "EUR";

        assertThrows(InvalidCurrencyException.class, () -> {
            CurrencyConverter.convertCurrency(wrongCurrency, toCurrency, amount);
        });
    }
}
