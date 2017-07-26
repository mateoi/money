package com.mateoi.money.model;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * Created by mateo on 26/07/2017.
 */
public class PercentageStringConverter extends StringConverter<Number> {
    private Supplier<Number> numberSupplier;

    public PercentageStringConverter(Supplier<Number> numberSupplier) {
        this.numberSupplier = numberSupplier;
    }

    public static String formatNumber(Number number) {
        if (number == null) {
            return "";
        }
        return new DecimalFormat("#.##").format(number.floatValue()) + "%";
    }

    @Override
    public String toString(Number number) {
        return formatNumber(number);
    }

    @Override
    public Number fromString(String string) {
        String numberPart = string.replaceAll("[^0-9.]", "");
        try {
            return Float.parseFloat(numberPart);
        } catch (NumberFormatException e) {
            return numberSupplier.get();
        }
    }
}
