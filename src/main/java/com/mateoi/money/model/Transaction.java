package com.mateoi.money.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.javamoney.moneta.Money;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by mateo on 30/06/2017.
 */
public class Transaction {
    private final int transactionId;

    private ObjectProperty<Date> date = new SimpleObjectProperty<>();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Money> amount = new SimpleObjectProperty<>();

    private ObjectProperty<BudgetItem> budgetType = new SimpleObjectProperty<>();

    private ObjectProperty<Account> account = new SimpleObjectProperty<>();

    public Transaction(int id, Date date, String description, Money amount, BudgetItem budgetType, Account account) {
        this.transactionId = id;
        this.date.setValue(date);
        this.description.setValue(description);
        this.amount.setValue(amount);
        this.budgetType.setValue(budgetType);
        this.account.setValue(account);

        this.account.addListener((observable, oldValue, newValue) -> {
            oldValue.getTransactions().remove(Transaction.this);
            newValue.getTransactions().add(Transaction.this);
        });
        this.budgetType.addListener(((observable, oldValue, newValue) -> {
            oldValue.getTransactions().remove(Transaction.this);
            newValue.getTransactions().add(Transaction.this);
        }));
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Transaction) {
            Transaction t = (Transaction) o;
            boolean ids = transactionId == t.getTransactionId();
            boolean dates = this.date.get().equals(t.getDate());
            boolean descriptions = this.description.get().equals(t.getDescription());
            boolean budgets = this.budgetType.get().equals(t.getBudgetType());
            boolean amounts = this.amount.get().equals(t.getAmount());
            boolean accounts = this.account.get().equals(t.getAccount());
            return ids && dates && descriptions && budgets && amounts && accounts;
        } else {
            return false;
        }
    }

    public String toString() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        return String.valueOf(transactionId) +
                ";" +
                dateFormat.format(date.get()) +
                ";" +
                description.get() +
                ";" +
                amount.get().toString() +
                ";" +
                budgetType.get().getItemId() +
                ";" +
                account.get().getAccountId();
    }

    public Date getDate() {
        return date.get();
    }

    public String getDescription() {
        return description.get();
    }


    public BudgetItem getBudgetType() {
        return budgetType.get();
    }

    public Account getAccount() {
        return account.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public StringProperty descriptionProperty() {
        return description;
    }


    public ObjectProperty<BudgetItem> budgetTypeProperty() {
        return budgetType;
    }

    public ObjectProperty<Account> accountProperty() {
        return account;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }


    public void setBudgetType(BudgetItem budgetType) {
        this.budgetType.set(budgetType);
    }

    public void setAccount(Account account) {
        this.account.set(account);
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

    public int getTransactionId() {
        return transactionId;
    }
}
