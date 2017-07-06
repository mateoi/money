package com.mateoi.money.model;

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

    private Account unknownAccount = new Account(-1, "Unknown", Money.of(0, "USD"), 0f);

    private BudgetItem unknownBudget = new BudgetItem(-1, false, "Unknown", Money.of(0, "USD"), false);

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
            sb.append("A: ");
            sb.append(account.toString());
            sb.append("\n");
        }
        sb.append("# Savings");
        sb.append("\n");
        for (SavingsItem savingsItem : savingsItems) {
            sb.append("S: ");
            sb.append(savingsItem.toString());
            sb.append("\n");

        }
        sb.append("# Budgets");
        sb.append("\n");
        for (BudgetItem budgetItem : budgetItems) {
            sb.append("B: ");
            sb.append(budgetItem.toString());
            sb.append("\n");

        }
        sb.append("# Transactions");
        sb.append("\n");
        for (Transaction transaction : transactions) {
            sb.append("T: ");
            sb.append(transaction.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public void initialize(List<Transaction> transactions, List<Account> accounts, List<BudgetItem> budgetItems, List<SavingsItem> savings) {
        this.transactions.addAll(transactions);
        this.accounts.addAll(accounts);
        this.budgetItems.addAll(budgetItems);
        this.savingsItems.addAll(savings);
    }

    private void removeBudgetItem(BudgetItem budgetItem) {
        for (Transaction transaction : transactions) {
            if (transaction.getBudgetType().equals(budgetItem)) {
                transaction.setBudgetType(unknownBudget);
            }
        }
    }

    private void removeAccount(Account account) {
        for (Transaction transaction : transactions) {
            if (transaction.getAccount().equals(account)) {
                transaction.setAccount(unknownAccount);
            }
        }
        for (SavingsItem savingsItem : savingsItems) {
            if (savingsItem.getAccount().equals(account)) {
                savingsItem.setAccount(unknownAccount);
            }
        }
    }

    private void processTransactions() {
        for (Transaction transaction : transactions) {
            transaction.getAccount().getTransactions().add(transaction);
            transaction.getBudgetType().getTransactions().add(transaction);
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
}
