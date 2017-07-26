package com.mateoi.money.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.javamoney.moneta.Money;

import javax.money.Monetary;

/**
 * Created by mateo on 20/07/2017.
 */
public class Budgets {

    private static final Money ZERO = Money.zero(Monetary.getCurrency("USD"));

    private final static Budgets instance = new Budgets();

    private ObjectProperty<Money> totalIn = new SimpleObjectProperty<>();

    private ObjectProperty<Money> totalOut = new SimpleObjectProperty<>();

    private ObjectProperty<Money> totalToSavings = new SimpleObjectProperty<>();

    private ObjectProperty<Money> totalEssentials = new SimpleObjectProperty<>();

    private ObjectProperty<Money> totalExtras = new SimpleObjectProperty<>();

    public Budgets() {
        ObservableList<BudgetItem> budgetItems = MainState.getInstance().getBudgetItems();
        ChangeListener<Object> listener = (a, b, c) -> setValues();
        setValues();
        for (BudgetItem budgetItem : budgetItems) {
            budgetItem.inProperty().addListener(listener);
            budgetItem.essentialProperty().addListener(listener);
            budgetItem.amountProperty().addListener(listener);
        }

        budgetItems.addListener((ListChangeListener<BudgetItem>) c -> {
            while (c.next()) {
                for (BudgetItem budgetItem : c.getAddedSubList()) {
                    budgetItem.inProperty().addListener(listener);
                    budgetItem.essentialProperty().addListener(listener);
                    budgetItem.amountProperty().addListener(listener);
                }
                setValues();
            }
        });
    }

    private void setValues() {
        totalIn.set(totalMoneyIn());
        totalOut.set(totalMoneyOut());
        totalToSavings.set(totalMoneyIn().subtract(totalMoneyOut()));
        totalEssentials.set(calculateEssentials());
        totalExtras.set(calculateExtras());
    }

    private Money totalMoneyIn() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(BudgetItem::isIn).
                map(BudgetItem::getAmount).
                reduce(ZERO, Money::add);
    }

    private Money totalMoneyOut() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(b -> !b.isIn()).
                map(BudgetItem::getAmount).
                reduce(ZERO, Money::add);
    }

    private Money calculateEssentials() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(BudgetItem::isEssential).
                filter(b -> !b.isIn()).
                map(BudgetItem::getAmount).
                reduce(ZERO, Money::add);
    }

    private Money calculateExtras() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(b -> !b.isEssential()).
                filter(b -> !b.isIn()).
                map(BudgetItem::getAmount).
                reduce(ZERO, Money::add);
    }

    public Money getTotalIn() {
        return totalIn.get();
    }

    public ObjectProperty<Money> totalInProperty() {
        return totalIn;
    }

    public Money getTotalOut() {
        return totalOut.get();
    }

    public ObjectProperty<Money> totalOutProperty() {
        return totalOut;
    }

    public Money getTotalToSavings() {
        return totalToSavings.get();
    }

    public ObjectProperty<Money> totalToSavingsProperty() {
        return totalToSavings;
    }

    public Money getTotalEssentials() {
        return totalEssentials.get();
    }

    public ObjectProperty<Money> totalEssentialsProperty() {
        return totalEssentials;
    }

    public Money getTotalExtras() {
        return totalExtras.get();
    }

    public ObjectProperty<Money> totalExtrasProperty() {
        return totalExtras;
    }

    public static Budgets getInstance() {
        return instance;
    }

}
