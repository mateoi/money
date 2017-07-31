package com.mateoi.money.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.javamoney.moneta.Money;

import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

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

    private ObjectProperty<Money> monthlyIncrease = new SimpleObjectProperty<>();

    private ObjectProperty<Number> monthsToTarget = new SimpleObjectProperty<>();


    public SavingsItem(int id, String name, Money goal, Money currentAmount, Account account, float allocation) {
        savingsId = id;
        this.name.set(name);
        this.goal.set(goal);
        this.currentAmount.set(currentAmount);
        this.account.set(account);
        this.allocation.set(allocation);

        progress.bind(Bindings.createFloatBinding(this::calculateProgress, this.goal, this.currentAmount));

        this.name.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.goal.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.currentAmount.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.account.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        monthlyIncrease.bind(Bindings.createObjectBinding(this::calculateMonthlyIncrease, Budgets.getInstance().totalToSavingsProperty(), this.allocation));
        monthsToTarget.bind(Bindings.createIntegerBinding(this::calculateMonthsToTarget, monthlyIncrease, this.goal, this.currentAmount));
    }

    private Money calculateMonthlyIncrease() {
        Money toSavings = Budgets.getInstance().getTotalToSavings();
        if (toSavings.isNegativeOrZero()) {
            return Money.zero(toSavings.getCurrency());
        } else {
            return toSavings.multiply(getAllocation() / 100);
        }
    }

    private int calculateMonthsToTarget() {
        CurrencyConversion conversion = MonetaryConversions.getConversion(goal.get().getCurrency());
        Money remaining = goal.get().subtract(currentAmount.get().with(conversion));
        if (remaining.isNegativeOrZero() || monthlyIncrease.get().isZero()) {
            return 0;
        } else {
            return (int) Math.ceil(remaining.divide(monthlyIncrease.get().with(conversion).getNumber()).getNumber().doubleValue());
        }
    }

    private float calculateProgress() {
        CurrencyConversion conversion = MonetaryConversions.getConversion(goal.get().getCurrency());
        Money currentAmount = getCurrentAmount().with(conversion);
        float absoluteProgress = (float) (currentAmount.getNumber().doubleValue() / goal.get().getNumber().doubleValue());
        return 100 * absoluteProgress;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof SavingsItem) {
            SavingsItem s = (SavingsItem) o;
            boolean ids = savingsId == s.getId();
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
        return String.valueOf(savingsId) +
                ";" +
                name.get() +
                ";" +
                goal.get().toString() +
                ";" +
                currentAmount.get().toString() +
                ";" +
                account.get().getId() +
                ";" +
                allocation.get();
    }

    public Color colorSavings() {
        CurrencyConversion conversion = MonetaryConversions.getConversion(currentAmount.get().getCurrency());
        if (currentAmount.get().isGreaterThanOrEqualTo(goal.get().with(conversion))) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
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

    public int getId() {
        return savingsId;
    }

    public Money getMonthlyIncrease() {
        return monthlyIncrease.get();
    }

    public ObjectProperty<Money> monthlyIncreaseProperty() {
        return monthlyIncrease;
    }

    public Number getMonthsToTarget() {
        return monthsToTarget.get();
    }

    public ObjectProperty<Number> monthsToTargetProperty() {
        return monthsToTarget;
    }
}
