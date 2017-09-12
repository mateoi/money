package com.mateoi.money.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents an account. It has a starting budget and a list of transactions that detail the account history
 */
public class Account {
    /**
     * ID of this account
     */
    private final int accountId;

    /**
     * Name of this account
     */
    private StringProperty name = new SimpleStringProperty();

    /**
     * Initial balance
     */
    private ObjectProperty<Money> startingAmount = new SimpleObjectProperty<>();

    /**
     * Annual interest rate, as a percentage (ie. a value of 0.5 represents 0.5%, not 50%)
     */
    private FloatProperty annualInterest = new SimpleFloatProperty();

    /**
     * List of transactions in this account
     */
    private ObservableList<SubTransaction> subTransactions = FXCollections.observableArrayList();

    /**
     * Account balance after taking into account all the transactions
     */
    private ObjectProperty<Money> currentBalance = new SimpleObjectProperty<>();

    /**
     * Maximum historical balance
     */
    private ObjectProperty<Money> maximumBalance = new SimpleObjectProperty<>();

    /**
     * Minimum historical balance
     */
    private ObjectProperty<Money> minimumBalance = new SimpleObjectProperty<>();

    /**
     * Average historical balance
     */
    private ObjectProperty<Money> averageBalance = new SimpleObjectProperty<>();

    /**
     * Average deposit to this account
     */
    private ObjectProperty<Money> averageDeposit = new SimpleObjectProperty<>();

    /**
     * Average withdrawal from this account
     */
    private ObjectProperty<Money> averageWithdrawal = new SimpleObjectProperty<>();

    /**
     * Map of each transaction in the account to the balance in the account at the time
     */
    private ObservableMap<SubTransaction, ObjectProperty<Money>> transactionsMap = FXCollections.observableHashMap();

    /**
     * Binding of the number of transactions in this account
     */
    private IntegerBinding txNumber = Bindings.size(subTransactions);

    /**
     * Balance below which the user should be warned
     */
    private ObjectProperty<Money> warningAmount = new SimpleObjectProperty<>();

    /**
     * Create a new account
     *
     * @param id             The unique id
     * @param name           The name or description of the account
     * @param startingAmount The starting balance of the account
     * @param warningAmount  The warning balance of the account
     * @param interest       Annual interest
     */
    public Account(int id, String name, Money startingAmount, Money warningAmount, float interest) {
        accountId = id;
        this.name.set(name);
        this.startingAmount.set(startingAmount);
        this.annualInterest.set(interest);
        this.warningAmount.set(warningAmount);

        this.currentBalance.setValue(Money.of(startingAmount.getNumber(), startingAmount.getCurrency()));
        currentBalance.set(startingAmount);
        minimumBalance.set(startingAmount);
        maximumBalance.set(startingAmount);
        this.subTransactions.addListener((ListChangeListener<? super SubTransaction>) ch -> {
            while (ch.next()) {
                if (ch.getAddedSize() == 1 && ch.getRemovedSize() == 0) {
                    processTransaction(ch.getAddedSubList().get(0));
                } else {
                    processTransactions();
                }
            }
        });

        this.name.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.startingAmount.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.annualInterest.addListener((a, b, c) -> MainState.getInstance().setModified(true));
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Account) {
            Account a = (Account) o;
            boolean ids = accountId == a.getId();
            boolean names = name.get().equals(a.getName());
            boolean starts = startingAmount.get().equals(a.getStartingAmount());
            boolean interests = annualInterest.get() == a.getAnnualInterest();
            return ids && names && starts && interests;
        } else {
            return false;
        }
    }

    public String toString() {
        return String.valueOf(accountId) +
                ";" +
                name.get() +
                ";" +
                startingAmount.get().toString() +
                ";" +
                annualInterest.get() +
                ";" +
                warningAmount.get();
    }

    /**
     * Processes all the transactions in the list
     */
    void processTransactions() {
        currentBalance.set(startingAmount.getValue());
        minimumBalance.set(startingAmount.getValue());
        maximumBalance.set(startingAmount.getValue());
        List<SubTransaction> sorted = new ArrayList<>(subTransactions);
        sorted.sort(Comparator.comparing(subTransaction -> subTransaction.getTransaction().getDate()));

        for (SubTransaction subTransaction : sorted) {
            processTransaction(subTransaction);
        }
    }

    /**
     * Processes a single transaction: updates current and historical balances and averages
     *
     * @param subTransaction The transaction to process
     */
    private void processTransaction(SubTransaction subTransaction) {
        CurrencyConversion conversion = MonetaryConversions.getConversion(startingAmount.get().getCurrency());
        Money amount = subTransaction.getAmount().with(conversion);

        Money balance = currentBalance.get();
        balance = balance.add(amount);
        currentBalance.set(balance);
        updateMaxMin();
        updateAvg();
        if (transactionsMap.containsKey(subTransaction)) {
            transactionsMap.get(subTransaction).set(balance);
        } else {
            transactionsMap.put(subTransaction, new SimpleObjectProperty<>(balance));
        }
    }

    /**
     * Updates historical balance and deposit and withdrawal data
     */
    private void updateAvg() {
        Money total = startingAmount.get();
        Money balance = startingAmount.get();
        CurrencyUnit currency = startingAmount.get().getCurrency();
        Money totalDeposits = Money.of(0, currency);
        Money totalWithdrawals = Money.of(0, currency);
        int numberOfDeposits = 0;
        int numberOfWithdrawals = 0;

        for (SubTransaction subTransaction : subTransactions) {
            CurrencyConversion conversion = MonetaryConversions.getConversion(currency);
            Money amount = subTransaction.getAmount().with(conversion);
            balance = balance.add(amount);
            total = total.add(balance);
            if (amount.isPositive()) {
                totalDeposits = totalDeposits.add(amount);
                numberOfDeposits++;
            } else if (amount.isNegative()) {
                totalWithdrawals = totalWithdrawals.add(amount);
                numberOfWithdrawals++;
            }
        }
        averageBalance.set(total.divide(subTransactions.size() + 1));
        Money averageDeposit = numberOfDeposits == 0 ? totalDeposits : totalDeposits.divide(numberOfDeposits);
        Money averageWithdrawal = numberOfWithdrawals == 0 ? totalWithdrawals : totalWithdrawals.divide(numberOfWithdrawals);
        this.averageDeposit.set(averageDeposit);
        this.averageWithdrawal.set(averageWithdrawal);
    }

    /**
     * Updates historical max and min balance
     */
    private void updateMaxMin() {
        CurrencyUnit currency = startingAmount.get().getCurrency();
        if (currentBalance.get().isGreaterThan(maximumBalance.get())) {
            maximumBalance.set(Money.of(currentBalance.get().getNumber(), currency));
        }
        if (currentBalance.get().isLessThan(minimumBalance.get())) {
            minimumBalance.set(Money.of(currentBalance.get().getNumber(), currency));
        }
    }

    /**
     * Returns a color for this account depending on whether the current balance is above or below the warning amount
     *
     * @return A color for this account
     */
    public Color colorAccount() {
        CurrencyConversion conversion = MonetaryConversions.getConversion(currentBalance.get().getCurrency());
        if (currentBalance.get().isGreaterThan(warningAmount.get().with(conversion))) {
            return Color.GREEN;
        } else if (currentBalance.get().isLessThan(warningAmount.get().with(conversion))) {
            return Color.RED;
        } else {
            return Color.BLACK;
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

    public Money getStartingAmount() {
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

    public ObservableList<SubTransaction> getSubTransactions() {
        return subTransactions;
    }

    public Money getMaximumBalance() {
        return maximumBalance.get();
    }

    public ObjectProperty<Money> maximumBalanceProperty() {
        return maximumBalance;
    }

    public Money getMinimumBalance() {
        return minimumBalance.get();
    }

    public ObjectProperty<Money> minimumBalanceProperty() {
        return minimumBalance;
    }

    public Money getAverageBalance() {
        return averageBalance.get();
    }

    public ObjectProperty<Money> averageBalanceProperty() {
        return averageBalance;
    }

    public Money getAverageDeposit() {
        return averageDeposit.get();
    }

    public ObjectProperty<Money> averageDepositProperty() {
        return averageDeposit;
    }

    public Money getAverageWithdrawal() {
        return averageWithdrawal.get();
    }

    public ObjectProperty<Money> averageWithdrawalProperty() {
        return averageWithdrawal;
    }

    public Money getCurrentBalance() {
        return currentBalance.get();
    }

    public ObjectProperty<Money> currentBalanceProperty() {
        return currentBalance;
    }

    public ObjectProperty<Money> balanceAtTransactionProperty(SubTransaction subTransaction) {
        return transactionsMap.get(subTransaction);
    }

    public int getId() {
        return accountId;
    }

    public Number getTxNumber() {
        return txNumber.get();
    }

    public IntegerBinding txNumberProperty() {
        return txNumber;
    }

    public Money getWarningAmount() {
        return warningAmount.get();
    }

    public ObjectProperty<Money> warningAmountProperty() {
        return warningAmount;
    }

    public void setWarningAmount(Money warningAmount) {
        this.warningAmount.set(warningAmount);
    }
}
