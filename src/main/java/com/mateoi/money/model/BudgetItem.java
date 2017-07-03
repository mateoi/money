package com.mateoi.money.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;

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
        String sb = String.valueOf(itemId) +
                ";" +
                in.get() +
                ";" +
                name.get() +
                ";" +
                amount.get().toString() +
                ";" +
                essential.get();
        return sb;
    }

    private void processTransactions() {
        for (Transaction transaction : transactions) {
            processTransaction(transaction);
        }
    }

    private void processTransaction(Transaction transaction) {
        if (!currency.equals(transaction.getAmount().getCurrency())) {
            return;
        }
        Money balance = amount.get();
        if (in.get()) {
            balance = balance.subtract(transaction.getAmount());
        } else {
            balance = balance.add(transaction.getAmount());
        }
        amount.set(balance);
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