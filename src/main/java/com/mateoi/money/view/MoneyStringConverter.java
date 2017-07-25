package com.mateoi.money.view;

import javafx.util.StringConverter;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.UnknownCurrencyException;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import javax.money.format.MonetaryParseException;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created by mateo on 25/07/2017.
 */
public class MoneyStringConverter extends StringConverter<Money> {
    private Supplier<Money> amountSupplier;

    public MoneyStringConverter(Supplier<Money> amountSupplier) {
        this.amountSupplier = amountSupplier;
    }

    public static String formatMoney(Money amount) {
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.US).set(CurrencyStyle.SYMBOL).set("pattern", "¤#,##0.00;¤#,##0.00").build());
        return format.format(amount);
    }

    @Override
    public String toString(Money amount) {
        return formatMoney(amount);
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
