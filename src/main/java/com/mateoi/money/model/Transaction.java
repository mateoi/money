package com.mateoi.money.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.javamoney.moneta.Money;

import java.util.Date;

/**
 * Created by mateo on 30/06/2017.
 */
public class Transaction {
    private ObjectProperty<Date> date = new SimpleObjectProperty<>();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Money> amount = new SimpleObjectProperty<>();

    private ObjectProperty<BudgetItem> budgetType = new SimpleObjectProperty<>();

    private ObjectProperty<Account> account = new SimpleObjectProperty<>();

    public Transaction(Date date, String description, Money amount, BudgetItem budgetType, Account account) {
        this.date.setValue(date);
        this.description.setValue(description);
        this.amount.setValue(amount);
        this.budgetType.setValue(budgetType);
        this.account.setValue(account);
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
}
