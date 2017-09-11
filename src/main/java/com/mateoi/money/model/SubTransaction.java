package com.mateoi.money.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.javamoney.moneta.Money;

import java.time.LocalDate;

/**
 * Created by mateo on 09/09/2017.
 */
public class SubTransaction {
    private final int id;

    private ObjectProperty<Account> account = new SimpleObjectProperty<>();

    private ObjectProperty<Money> amount = new SimpleObjectProperty<>();

    private final Transaction transaction;

    private ObjectProperty<Money> accountBalance = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDate> txDate = new SimpleObjectProperty<>();

    private ObjectProperty<String> txDescription = new SimpleObjectProperty<>();

    private ObjectProperty<BudgetItem> txType = new SimpleObjectProperty<>();

    public SubTransaction(int id, Account account, Money amount, Transaction transaction) {
        this.id = id;
        this.transaction = transaction;
        this.amount.set(amount);
        txDate.bindBidirectional(transaction.dateProperty());
        txDescription.bindBidirectional(transaction.descriptionProperty());
        txType.bindBidirectional(transaction.budgetTypeProperty());
        this.amount.addListener((observable, oldValue, newValue) -> {
            if (this.account.get() != null) {
                this.account.get().processTransactions();
            }
            this.transaction.updateTotal();
            MainState.getInstance().setModified(true);
        });
        if (account != null && !account.getSubTransactions().contains(this)) {
            account.getSubTransactions().add(this);
        }

        this.account.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getSubTransactions().remove(SubTransaction.this);
            }
            if (newValue != null) {
                newValue.getSubTransactions().add(SubTransaction.this);
                accountBalance.bind(newValue.balanceAtTransactionProperty(this));
            }
            MainState.getInstance().setModified(true);
        });
        this.account.set(account);
    }

    public Color colorSubTransaction() {
        if (amount.get().isPositive()) {
            return Color.GREEN;
        } else if (amount.get().isNegative()) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
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

    public Money getAmount() {
        return amount.get();
    }

    public ObjectProperty<Money> amountProperty() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount.set(amount);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public int getId() {
        return id;
    }

    public Money getAccountBalance() {
        return accountBalance.get();
    }

    public ObjectProperty<Money> accountBalanceProperty() {
        return accountBalance;
    }

    public LocalDate getTxDate() {
        return txDate.get();
    }

    public ObjectProperty<LocalDate> txDateProperty() {
        return txDate;
    }

    public String getTxDescription() {
        return txDescription.get();
    }

    public ObjectProperty<String> txDescriptionProperty() {
        return txDescription;
    }

    public BudgetItem getTxType() {
        return txType.get();
    }

    public ObjectProperty<BudgetItem> txTypeProperty() {
        return txType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubTransaction that = (SubTransaction) o;

        if (id != that.id) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        return transaction != null ? transaction.equals(that.transaction) : that.transaction == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (transaction != null ? transaction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id +
                ";" +
                transaction.getId() +
                ";" +
                account.get().getId() +
                ";" +
                amount.get().toString();
    }
}
