package com.mateoi.money.view;

import com.mateoi.money.model.MoneyStringConverter;
import com.mateoi.money.model.Settings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.javamoney.moneta.Money;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by mateo on 27/07/2017.
 */
public class MoneyTableCell<S> extends TextFieldTableCell<S, Money> {

    private Function<S, Color> coloringFunction;

    public MoneyTableCell(Supplier<Money> defaultMoneySupplier, Function<S, Color> coloringFunction) {
        super(new MoneyStringConverter(defaultMoneySupplier));
        this.coloringFunction = coloringFunction;
    }


    public static <S> Callback<TableColumn<S, Money>, TableCell<S, Money>> forTableColumn(
            Supplier<Money> defaultMoneySupplier, Function<S, Color> coloringFunction) {
        return c -> new MoneyTableCell<>(defaultMoneySupplier, coloringFunction);
    }

    @Override
    public void updateItem(Money item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText("");
            setStyle("");
        } else if (Settings.getInstance().isColorCode() && getTableRow() != null && getTableRow().getItem() != null) {
            setTextFill(coloringFunction.apply((S) getTableRow().getItem()));
        } else {
            setTextFill(Color.BLACK);
        }
    }
}
