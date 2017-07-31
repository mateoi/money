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

import java.util.Comparator;
import java.util.List;

/**
 * Singleton class that encodes the state of the program at a given time. It includes all current transactions,
 * accounts, and budget and savings items. It also keeps track of the current max id for each of these types.
 */
public class MainState {
    /**
     * Singleton instance of the class
     */
    private final static MainState instance = new MainState();

    /**
     * Default account
     */
    public static final Account UNKNOWN_ACCOUNT = new Account(-1, "Unknown", Money.zero(Settings.getInstance().getDefaultCurrency()), Money.zero(Settings.getInstance().getDefaultCurrency()), 0f);

    /**
     * Default budget
     */
    public static final BudgetItem UNKNOWN_BUDGET = new BudgetItem(-1, false, "Unknown", Money.zero(Settings.getInstance().getDefaultCurrency()), false);

    /**
     * All transactions in history
     */
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    /**
     * All accounts in history
     */
    private ObservableList<Account> accounts = FXCollections.observableArrayList();

    /**
     * All budget items in history
     */
    private ObservableList<BudgetItem> budgetItems = FXCollections.observableArrayList();

    /**
     * All savings items in history
     */
    private ObservableList<SavingsItem> savingsItems = FXCollections.observableArrayList();

    /**
     * ID of the last transaction added
     */
    private IntegerProperty lastTransaction = new SimpleIntegerProperty(0);

    /**
     * ID of the last account added
     */
    private IntegerProperty lastAccount = new SimpleIntegerProperty(0);

    /**
     * ID of the last savings item added
     */
    private IntegerProperty lastSavings = new SimpleIntegerProperty(0);

    /**
     * ID of the last budget added
     */
    private IntegerProperty lastBudget = new SimpleIntegerProperty(0);

    /**
     * Whether the state has been modified since the last save
     */
    private BooleanProperty modified = new SimpleBooleanProperty(false);

    private MainState() {
        transactions.addListener((ListChangeListener<? super Transaction>) ch -> processTransactions());
        accounts.addListener((ListChangeListener<? super Account>) ch -> {
            while (ch.next()) {
                for (Account account : ch.getRemoved()) {
                    removeAccountFromTransactions(account);
                }
            }
        });
        budgetItems.addListener((ListChangeListener<? super BudgetItem>) ch -> {
            while (ch.next()) {
                for (BudgetItem budgetItem : ch.getRemoved()) {
                    removeBudgetItemFromTransactions(budgetItem);
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
        return sb.toString();
    }

    /**
     * Set the program state to the given parameters
     *
     * @param transactions List of all transactions
     * @param accounts     List of all accounts
     * @param budgetItems  List of all budget items
     * @param savings      List of all savings
     */
    public void initialize(List<Transaction> transactions, List<Account> accounts, List<BudgetItem> budgetItems,
                           List<SavingsItem> savings) {
        clearAll();
        this.transactions.addAll(transactions);
        this.accounts.addAll(accounts);
        this.budgetItems.addAll(budgetItems);
        this.savingsItems.addAll(savings);
        this.lastTransaction.set(transactions.stream().map(Transaction::getId).max(Comparator.naturalOrder()).orElse(0));
        this.lastAccount.set(accounts.stream().map(Account::getId).max(Comparator.naturalOrder()).orElse(0));
        this.lastSavings.set(savings.stream().map(SavingsItem::getId).max(Comparator.naturalOrder()).orElse(0));
        this.lastBudget.set(budgetItems.stream().map(BudgetItem::getId).max(Comparator.naturalOrder()).orElse(0));
        modified.set(false);
    }

    /**
     * Reset program state to all clear
     */
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


    /**
     * Update all transactions to remove references to the given budgetitem and replace them with UNKNOWN_BUDGET.
     *
     * @param budgetItem The item to remove
     */
    private void removeBudgetItemFromTransactions(BudgetItem budgetItem) {
        for (Transaction transaction : transactions) {
            if (transaction.getBudgetType().equals(budgetItem)) {
                transaction.setBudgetType(UNKNOWN_BUDGET);
            }
        }
    }

    /**
     * Update all transactions to remove references to the given account and replace them with UNKNOWN_ACCOUNT.
     *
     * @param account The account to remove
     */
    private void removeAccountFromTransactions(Account account) {
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

    /**
     * Process all transactions: reconcile all transactions, accounts and budgets by making sure that all data
     * structures are consistent
     */
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

    public ObservableList<Account> getAccounts() {
        return accounts;
    }

    public ObservableList<BudgetItem> getBudgetItems() {
        return budgetItems;
    }

    public ObservableList<SavingsItem> getSavingsItems() {
        return savingsItems;
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
