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
public class Account {
    private StringProperty name = new SimpleStringProperty();

    private ObjectProperty<Money> startingAmount = new SimpleObjectProperty<>();

    private FloatProperty annualInterest = new SimpleFloatProperty();

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private CurrencyUnit currency;

    private ObjectProperty<Money> currentBalance = new SimpleObjectProperty<>();

    private ObjectProperty<Money> maximumBalance = new SimpleObjectProperty<>();

    private ObjectProperty<Money> minimumBalance = new SimpleObjectProperty<>();

    private ObjectProperty<Money> averageBalance = new SimpleObjectProperty<>();

    private ObjectProperty<Money> averageDeposit = new SimpleObjectProperty<>();

    private ObjectProperty<Money> averageWithdrawal = new SimpleObjectProperty<>();


    public Account(String name, Money startingAmount, float interest, Transaction... transactions) {
        this.name.set(name);
        this.startingAmount.set(startingAmount);
        this.annualInterest.set(interest);

        this.currency = startingAmount.getCurrency();
        this.currentBalance.setValue(Money.of(startingAmount.getNumber(), currency));
        this.transactions.addListener((ListChangeListener<? super Transaction>) ch -> processTransactions());
        this.transactions.addAll(transactions);
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
        Money balance = currentBalance.get();
        balance = balance.add(transaction.getAmount());
        currentBalance.set(balance);
        updateMaxMin();
        updateAvg();
    }

    private void updateAvg() {
        Money total = startingAmount.get();
        Money totalDeposits = Money.of(0, currency);
        Money totalWithdrawals = Money.of(0, currency);
        int numberOfDeposits = 0;
        int numberOfWithdrawals = 0;

        for (Transaction transaction : transactions) {
            Money amount = transaction.getAmount();
            total = total.add(amount);
            if (amount.isPositive()) {
                totalDeposits = totalDeposits.add(amount);
                numberOfDeposits++;
            } else if (amount.isNegative()) {
                totalWithdrawals = totalWithdrawals.add(amount);
                numberOfWithdrawals++;
            }
        }
        averageBalance.set(total.divide(transactions.size() + 1));
        Money averageDeposit = numberOfDeposits == 0 ? totalDeposits : totalDeposits.divide(numberOfDeposits);
        Money averageWithdrawal = numberOfWithdrawals == 0 ? totalWithdrawals : totalWithdrawals.divide(numberOfWithdrawals);
        this.averageDeposit.set(averageDeposit);
        this.averageWithdrawal.set(averageWithdrawal);
    }

    private void updateMaxMin() {
        if (currentBalance.get().isGreaterThan(maximumBalance.get())) {
            maximumBalance.set(Money.of(currentBalance.get().getNumber(), currency));
        }
        if (currentBalance.get().isLessThan(minimumBalance.get())) {
            minimumBalance.set(Money.of(currentBalance.get().getNumber(), currency));
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

    public Object getStartingAmount() {
        return startingAmount.get();
    }

    public ObjectProperty<Money> startingAmountProperty() {
        return startingAmount;
    }

    public void setStartingAmount(Money startingAmount) {
        this.startingAmount.set(startingAmount);
        processTransactions();
    }

    public float getAnnualInterest() {
        return annualInterest.get();
    }

    public FloatProperty annualInterestProperty() {
        return annualInterest;
    }

    public void setAnnualInterest(float annualInterest) {
        this.annualInterest.set(annualInterest);
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public Money getMaximumBalance() {
        return maximumBalance.get();
    }

    public ObjectProperty<Money> maximumBalanceProperty() {
        return maximumBalance;
    }

    public void setMaximumBalance(Money maximumBalance) {
        this.maximumBalance.set(maximumBalance);
    }

    public Money getMinimumBalance() {
        return minimumBalance.get();
    }

    public ObjectProperty<Money> minimumBalanceProperty() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance.set(minimumBalance);
    }

    public Money getAverageBalance() {
        return averageBalance.get();
    }

    public ObjectProperty<Money> averageBalanceProperty() {
        return averageBalance;
    }

    public void setAverageBalance(Money averageBalance) {
        this.averageBalance.set(averageBalance);
    }

    public Money getAverageDeposit() {
        return averageDeposit.get();
    }

    public ObjectProperty<Money> averageDepositProperty() {
        return averageDeposit;
    }

    public void setAverageDeposit(Money averageDeposit) {
        this.averageDeposit.set(averageDeposit);
    }

    public Money getAverageWithdrawal() {
        return averageWithdrawal.get();
    }

    public ObjectProperty<Money> averageWithdrawalProperty() {
        return averageWithdrawal;
    }

    public void setAverageWithdrawal(Money averageWithdrawal) {
        this.averageWithdrawal.set(averageWithdrawal);
    }
}
