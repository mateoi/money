package com.mateoi.money.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

/**
 * Created by mateo on 30/06/2017.
 */
public class BudgetItem {
    private final int itemId;

    private BooleanProperty in = new SimpleBooleanProperty();

    private StringProperty name = new SimpleStringProperty();

    private ObjectProperty<Money> amount = new SimpleObjectProperty<>();

    private ObjectProperty<Money> remaining = new SimpleObjectProperty<>();

    private BooleanProperty essential = new SimpleBooleanProperty();

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private CurrencyUnit currency;


    public BudgetItem(int id, boolean in, String name, Money amount, boolean essential, Transaction... transactions) {
        itemId = id;
        this.in.set(in);
        this.name.set(name);
        this.amount.set(amount);
        this.essential.set(essential);

        this.currency = amount.getCurrency();
        this.remaining.setValue(Money.of(amount.getNumber(), currency));
        this.amount.addListener((observable, oldValue, newValue) -> {
            processTransactions();
            MainState.getInstance().setModified(true);
        });
        this.transactions.addListener((ListChangeListener<? super Transaction>) ch -> {
            while (ch.next()) {
                if (ch.getAddedSize() == 1 && ch.getRemovedSize() == 0) {
                    processTransaction(ch.getAddedSubList().get(0));
                } else {
                    processTransactions();
                }
            }
        });
        this.transactions.addAll(transactions);
        this.in.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.name.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.essential.addListener((a, b, c) -> MainState.getInstance().setModified(true));

    }

    public boolean equals(Object o) {
        if (o != null && o instanceof BudgetItem) {
            BudgetItem b = (BudgetItem) o;
            boolean ids = itemId == b.getItemId();
            boolean ins = in.get() == b.isIn();
            boolean names = name.get().equals(b.getName());
            boolean amounts = amount.get().equals(b.getAmount());
            boolean essentials = essential.get() == b.isEssential();
            return ids && ins && names && amounts && essentials;
        } else {
            return false;
        }
    }

    public String toString() {
        return String.valueOf(itemId) +
                ";" +
                in.get() +
                ";" +
                name.get() +
                ";" +
                amount.get().toString() +
                ";" +
                essential.get();
    }

    protected void processTransactions() {
        this.remaining.set(amount.get());
        for (Transaction transaction : transactions) {
            processTransaction(transaction);
        }
    }

    private void processTransaction(Transaction transaction) {
        CurrencyConversion conversion = MonetaryConversions.getConversion(currency);
        Money amount = transaction.getAmount().with(conversion);
        Money balance = remaining.get();
        balance = balance.add(amount);
        remaining.set(balance);
    }

    public Color colorBudget() {
        Money zero = Money.zero(Settings.getInstance().getDefaultCurrency());
        if (remaining.get().isGreaterThan(zero)) {
            return isIn() ? Color.RED : Color.GREEN;
        } else if (remaining.get().isLessThan(zero)) {
            return isIn() ? Color.GREEN : Color.RED;
        } else {
            return Color.BLACK;
        }

    }

    public boolean isIn() {
        return in.get();
    }

    public BooleanProperty inProperty() {
        return in;
    }

    public void setIn(boolean in) {
        this.in.set(in);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Money getAmount() {
        return amount.get();
    }

    public ObjectProperty<Money> amountProperty() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount.set(amount);
    }

    public Money getRemaining() {
        return remaining.get();
    }

    public ObjectProperty<Money> remainingProperty() {
        return remaining;
    }

    public void setRemaining(Money remaining) {
        this.remaining.set(remaining);
    }

    public boolean isEssential() {
        return essential.get();
    }

    public BooleanProperty essentialProperty() {
        return essential;
    }

    public void setEssential(boolean essential) {
        this.essential.set(essential);
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public int getItemId() {
        return itemId;
    }
}
