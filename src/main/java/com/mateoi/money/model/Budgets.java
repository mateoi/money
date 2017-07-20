package com.mateoi.money.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
        totalIn.bind(Bindings.createObjectBinding(this::totalMoneyIn, budgetItems));
        totalOut.bind(Bindings.createObjectBinding(this::totalMoneyOut, budgetItems));
        totalToSavings.bind(Bindings.createObjectBinding(() -> totalMoneyIn().subtract(totalMoneyOut()), budgetItems));
        totalEssentials.bind(Bindings.createObjectBinding(this::calculateEssentials, budgetItems));
        totalExtras.bind(Bindings.createObjectBinding(this::calculateExtras, budgetItems));
    }

    private Money totalMoneyIn() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(BudgetItem::isIn).
                map(BudgetItem::getAmount).
                reduce(Money::add).
                orElse(ZERO);
    }

    private Money totalMoneyOut() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(b -> !b.isIn()).
                map(BudgetItem::getAmount).
                reduce(Money::add).
                orElse(ZERO);
    }

    private Money calculateEssentials() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(BudgetItem::isEssential).
                map(BudgetItem::getAmount).
                reduce(Money::add).
                orElse(ZERO);
    }

    private Money calculateExtras() {
        return MainState.getInstance().getBudgetItems().stream().
                filter(b -> !b.isEssential()).
                map(BudgetItem::getAmount).
                reduce(Money::add).
                orElse(ZERO);
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
