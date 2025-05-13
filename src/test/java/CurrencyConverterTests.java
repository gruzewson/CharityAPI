import app.services.CurrencyConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyConverterTests {
    @Test
    public void convertCurrency_ShouldConvertCorrectly() {
        double amount = 100.0;
        double convertedAmount = CurrencyConverter.convertCurrency("EUR", "PLN", amount);
        assertEquals(450.0, convertedAmount);
    }

    @Test
    public void convertCurrency_ShouldReturnSameAmount_WhenSameCurrency() {
        double amount = 100.0;
        double convertedAmount = CurrencyConverter.convertCurrency("PLN", "PLN", amount);
        assertEquals(100.0, convertedAmount);
    }

    @Test
    public void convertCurrency_ShouldThrowException_WhenExchangeRateNotAvailable() {
        double amount = 100.0;
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyConverter.convertCurrency(fromCurrency, toCurrency, amount);
        });
    }
}
