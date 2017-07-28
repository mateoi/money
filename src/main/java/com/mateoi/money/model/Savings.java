package com.mateoi.money.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;


/**
 * Created by mateo on 27/07/2017.
 */
public class Savings {
    private static final Savings ourInstance = new Savings();

    private StringProperty totalAllocation = new SimpleStringProperty();

    private FloatProperty allocation = new SimpleFloatProperty();

    public static Savings getInstance() {
        return ourInstance;
    }

    private Savings() {
        ObservableList<SavingsItem> savings = MainState.getInstance().getSavingsItems();
        ChangeListener<Number> listener = (a, b, c) -> allocation.set(calculateAllocation());
        allocation.set(calculateAllocation());
        savings.forEach(s -> s.allocationProperty().addListener(listener));
        savings.addListener((ListChangeListener<? super SavingsItem>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(s -> s.allocationProperty().addListener(listener));
                allocation.set(calculateAllocation());
            }
        });

        allocation.addListener((a, b, c) -> totalAllocation.set(setTotalAllocation()));
        totalAllocation.set(setTotalAllocation());
    }

    private String setTotalAllocation() {
        return "Total Allocation: " + PercentageStringConverter.formatNumber(allocation.floatValue());
    }

    private float calculateAllocation() {
        return MainState.getInstance().getSavingsItems().stream().
                map(SavingsItem::getAllocation).
                reduce(0f, (a, b) -> a + b);
    }

    public String getTotalAllocation() {
        return totalAllocation.get();
    }

    public StringProperty totalAllocationProperty() {
        return totalAllocation;
    }
}
