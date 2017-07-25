package com.mateoi.money.view;

import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.UnknownCurrencyException;
import javax.money.format.MonetaryParseException;
import java.util.function.Supplier;

/**
 * Created by mateo on 25/07/2017.
 */
public class MoneyStringConverter extends StringConverter<Money> {
    private Supplier<Money> amountSupplier;

    public MoneyStringConverter(Supplier<Money> amountSupplier) {
        this.amountSupplier = amountSupplier;
    }

    @Override
    public String toString(Money object) {
        return object.toString();
    }

    @Override
    public Money fromString(String string) {
        try {
            return Money.parse(string.trim());
        } catch (MonetaryParseException ignored) {

            float amount = 0;
            try {
                String amountPart = string.replaceAll("[^\\-0-9.]", "");
                amount = Float.parseFloat(amountPart);
                String currencyPart = string.replaceAll("[^a-zA-Z]", "");
                return Money.of(amount, currencyPart);
            } catch (NumberFormatException e) {
                return amountSupplier.get();
            } catch (UnknownCurrencyException e) {
                return Money.of(amount, amountSupplier.get().getCurrency());
            }
        }
    }
}
