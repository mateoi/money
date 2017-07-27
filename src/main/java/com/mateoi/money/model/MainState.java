package com.mateoi.money.model;

import com.mateoi.money.io.FilePrefixes;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.javamoney.moneta.Money;

import java.util.List;

/**
 * Created by mateo on 02/07/2017.
 */
public class MainState {
    private final static MainState instance = new MainState();

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private ObservableList<Account> accounts = FXCollections.observableArrayList();

    private ObservableList<BudgetItem> budgetItems = FXCollections.observableArrayList();

    private ObservableList<SavingsItem> savingsItems = FXCollections.observableArrayList();

    public static final Account UNKNOWN_ACCOUNT = new Account(-1, "Unknown", Money.zero(Settings.getInstance().getDefaultCurrency()), Money.zero(Settings.getInstance().getDefaultCurrency()), 0f);

    public static final BudgetItem UNKNOWN_BUDGET = new BudgetItem(-1, false, "Unknown", Money.zero(Settings.getInstance().getDefaultCurrency()), false);

    private IntegerProperty lastTransaction = new SimpleIntegerProperty(0);

    private IntegerProperty lastAccount = new SimpleIntegerProperty(0);

    private IntegerProperty lastSavings = new SimpleIntegerProperty(0);

    private IntegerProperty lastBudget = new SimpleIntegerProperty(0);

    private BooleanProperty modified = new SimpleBooleanProperty(false);

    private MainState() {
        transactions.addListener((ListChangeListener<? super Transaction>) ch -> processTransactions());
        accounts.addListener((ListChangeListener<? super Account>) ch -> {
            while (ch.next()) {
                for (Account account : ch.getRemoved()) {
                    removeAccount(account);
                }
            }
        });
        budgetItems.addListener((ListChangeListener<? super BudgetItem>) ch -> {
            while (ch.next()) {
                for (BudgetItem budgetItem : ch.getRemoved()) {
                    removeBudgetItem(budgetItem);
                }
            }
        });
    }

    public static MainState getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Accounts");
        sb.append("\n");
        for (Account account : accounts) {
            sb.append(FilePrefixes.ACCOUNT_PREFIX);
            sb.append(account.toString());
            sb.append("\n");
        }
        sb.append("# Savings");
        sb.append("\n");
        for (SavingsItem savingsItem : savingsItems) {
            sb.append(FilePrefixes.SAVINGS_PREFIX);
            sb.append(savingsItem.toString());
            sb.append("\n");

        }
        sb.append("# Budgets");
        sb.append("\n");
        for (BudgetItem budgetItem : budgetItems) {
            sb.append(FilePrefixes.BUDGET_PREFIX);
            sb.append(budgetItem.toString());
            sb.append("\n");

        }
        sb.append("# Transactions");
        sb.append("\n");
        for (Transaction transaction : transactions) {
            sb.append(FilePrefixes.TRANSACTION_PREFIX);
            sb.append(transaction.toString());
            sb.append("\n");
        }
        sb.append(FilePrefixes.TX_COUNT_PREFIX);
        sb.append(lastTransaction.get());
        sb.append("\n");

        sb.append(FilePrefixes.ACCOUNT_COUNT_PREFIX);
        sb.append(lastAccount.get());
        sb.append("\n");

        sb.append(FilePrefixes.BUDGET_COUNT_PREFIX);
        sb.append(lastBudget.get());
        sb.append("\n");

        sb.append(FilePrefixes.SAVINGS_COUNT_PREFIX);
        sb.append(lastSavings.get());
        sb.append("\n");

        return sb.toString();
    }

    public void initialize(List<Transaction> transactions, List<Account> accounts, List<BudgetItem> budgetItems,
                           List<SavingsItem> savings, int txCount, int accountCount, int savingsCount, int budgetCount) {
        clearAll();
        this.transactions.addAll(transactions);
        this.accounts.addAll(accounts);
        this.budgetItems.addAll(budgetItems);
        this.savingsItems.addAll(savings);
        this.lastTransaction.set(txCount);
        this.lastAccount.set(accountCount);
        this.lastSavings.set(savingsCount);
        this.lastBudget.set(budgetCount);
        modified.set(false);
    }

    public void clearAll() {
        this.transactions.clear();
        this.accounts.clear();
        this.budgetItems.clear();
        this.savingsItems.clear();
        this.lastTransaction.set(0);
        this.lastAccount.set(0);
        this.lastSavings.set(0);
        this.lastBudget.set(0);
        modified.set(false);
    }

    private void removeBudgetItem(BudgetItem budgetItem) {
        for (Transaction transaction : transactions) {
            if (transaction.getBudgetType().equals(budgetItem)) {
                transaction.setBudgetType(UNKNOWN_BUDGET);
            }
        }
    }

    private void removeAccount(Account account) {
        for (Transaction transaction : transactions) {
            if (transaction.getAccount().equals(account)) {
                transaction.setAccount(UNKNOWN_ACCOUNT);
            }
        }
        for (SavingsItem savingsItem : savingsItems) {
            if (savingsItem.getAccount().equals(account)) {
                savingsItem.setAccount(UNKNOWN_ACCOUNT);
            }
        }
    }

    private void processTransactions() {
        for (Transaction transaction : transactions) {
            if (transaction != null) {
                if (transaction.getAccount() != null && !transaction.getAccount().getTransactions().contains(transaction)) {
                    transaction.getAccount().getTransactions().add(transaction);
                }
                if (transaction.getBudgetType() != null && !transaction.getBudgetType().getTransactions().contains(transaction)) {
                    transaction.getBudgetType().getTransactions().add(transaction);
                }
            }
        }
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ObservableList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ObservableList<Account> accounts) {
        this.accounts = accounts;
    }

    public ObservableList<BudgetItem> getBudgetItems() {
        return budgetItems;
    }

    public void setBudgetItems(ObservableList<BudgetItem> budgetItems) {
        this.budgetItems = budgetItems;
    }

    public ObservableList<SavingsItem> getSavingsItems() {
        return savingsItems;
    }

    public void setSavingsItems(ObservableList<SavingsItem> savingsItems) {
        this.savingsItems = savingsItems;
    }

    public int getLastTransaction() {
        return lastTransaction.get();
    }

    public IntegerProperty lastTransactionProperty() {
        return lastTransaction;
    }

    public void setLastTransaction(int lastTransaction) {
        this.lastTransaction.set(lastTransaction);
    }

    public int getLastAccount() {
        return lastAccount.get();
    }

    public IntegerProperty lastAccountProperty() {
        return lastAccount;
    }

    public void setLastAccount(int lastAccount) {
        this.lastAccount.set(lastAccount);
    }

    public int getLastSavings() {
        return lastSavings.get();
    }

    public IntegerProperty lastSavingsProperty() {
        return lastSavings;
    }

    public void setLastSavings(int lastSavings) {
        this.lastSavings.set(lastSavings);
    }

    public int getLastBudget() {
        return lastBudget.get();
    }

    public IntegerProperty lastBudgetProperty() {
        return lastBudget;
    }

    public void setLastBudget(int lastBudget) {
        this.lastBudget.set(lastBudget);
    }

    public boolean isModified() {
        return modified.get();
    }

    public BooleanProperty modifiedProperty() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified.set(modified);
    }
}
