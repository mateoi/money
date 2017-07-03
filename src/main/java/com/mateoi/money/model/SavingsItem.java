package com.mateoi.money.model;

import javafx.beans.property.*;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 02/07/2017.
 */
public class SavingsItem {
    private final int savingsId;

    private StringProperty name = new SimpleStringProperty();

    private ObjectProperty<Money> goal = new SimpleObjectProperty<>();

    private ObjectProperty<Money> currentAmount = new SimpleObjectProperty<>();

    private FloatProperty progress = new SimpleFloatProperty();

    private ObjectProperty<Account> account = new SimpleObjectProperty<>();


    private FloatProperty allocation = new SimpleFloatProperty();


    public SavingsItem(int id, String name, Money goal, Money currentAmount, Account account, float allocation) {
        savingsId = id;
        this.name.set(name);
        this.goal.set(goal);
        this.currentAmount.set(currentAmount);
        this.account.set(account);
        this.allocation.set(allocation);

        this.goal.addListener((o, old, newMoney) -> this.progress.set((float) (currentAmount.getNumber().doubleValue() / newMoney.getNumber().doubleValue())));
        this.currentAmount.addListener((o, old, newMoney) -> this.progress.set((float) (newMoney.getNumber().doubleValue() / goal.getNumber().doubleValue())));
        this.progress.set((float) (currentAmount.getNumber().doubleValue() / goal.getNumber().doubleValue()));
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof SavingsItem) {
            SavingsItem s = (SavingsItem) o;
            boolean ids = savingsId == s.getSavingsId();
            boolean names = name.get().equals(s.getName());
            boolean goals = goal.get().equals(s.getGoal());
            boolean accounts = account.get().equals(s.getAccount());
            boolean allocations = allocation.get() == s.getAllocation();
            return ids && names && goals && accounts && allocations;
        } else {
            return false;
        }
    }

    public String toString() {
        String sb = String.valueOf(savingsId) +
                ";" +
                name.get() +
                ";" +
                goal.get().toString() +
                ";" +
                account.get().getAccountId() +
                ";" +
                allocation.get();
        return sb;
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

    public Money getGoal() {
        return goal.get();
    }

    public ObjectProperty<Money> goalProperty() {
        return goal;
    }

    public void setGoal(Money goal) {
        this.goal.set(goal);
    }

    public Money getCurrentAmount() {
        return currentAmount.get();
    }

    public ObjectProperty<Money> currentAmountProperty() {
        return currentAmount;
    }

    public void setCurrentAmount(Money currentAmount) {
        this.currentAmount.set(currentAmount);
    }

    public float getProgress() {
        return progress.get();
    }

    public FloatProperty progressProperty() {
        return progress;
    }

    public Account getAccount() {
        return account.get();
    }

    public ObjectProperty<Account> accountProperty() {
        return account;
    }

    public void setAccount(Account account) {
        this.account.set(account);
    }

    public float getAllocation() {
        return allocation.get();
    }

    public FloatProperty allocationProperty() {
        return allocation;
    }

    public void setAllocation(float allocation) {
        this.allocation.set(allocation);
    }

    public int getSavingsId() {
        return savingsId;
    }
}
