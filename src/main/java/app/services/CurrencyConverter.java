package app.services;

import app.exceptions.arguments.InvalidCurrencyException;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        exchangeRates.put("PLN_EUR", 0.22);
        exchangeRates.put("PLN_GBP", 0.20);
        exchangeRates.put("EUR_PLN", 4.50);
        exchangeRates.put("EUR_GBP", 0.84);
        exchangeRates.put("GBP_PLN", 5.04);
        exchangeRates.put("GBP_EUR", 1.19);
    }

    public static double convertCurrency(String fromCurrency, String toCurrency, double amount) throws InvalidCurrencyException {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        String key = fromCurrency + "_" + toCurrency;
        if (!exchangeRates.containsKey(key)) {
            throw new InvalidCurrencyException("Exchange rate not available for: " + fromCurrency + " to " + toCurrency);
        }

        double rate = exchangeRates.get(key);
        return amount * rate;
    }
}

