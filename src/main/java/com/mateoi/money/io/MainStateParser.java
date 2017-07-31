package com.mateoi.money.io;

import com.mateoi.money.model.*;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton class used for parsing a main state and loading it.
 */
public class MainStateParser {
    private static final MainStateParser instance = new MainStateParser();

    // Private constructor to prevent instantiation
    private MainStateParser() {
        // Empty
    }

    /**
     * Get the singleton instance of this class
     *
     * @return The instance of this class
     */
    public static MainStateParser getInstance() {
        return instance;
    }

    /**
     * Parses a main state from a list of lines, then initializes the program state.
     *
     * @param lines The lines to parse
     */
    public void parseIntoMainState(List<String> lines) {
        List<Account> accounts = cleanLines(lines, FilePrefixes.ACCOUNT_PREFIX).map(this::parseAccount).collect(Collectors.toList());
        List<BudgetItem> budgets = cleanLines(lines, FilePrefixes.BUDGET_PREFIX).map(this::parseBudgetItem).collect(Collectors.toList());
        List<SavingsItem> savings = cleanLines(lines, FilePrefixes.SAVINGS_PREFIX).map(s -> parseSavingsItem(s, accounts)).collect(Collectors.toList());
        List<Transaction> transactions = cleanLines(lines, FilePrefixes.TRANSACTION_PREFIX).map(s -> parseTransaction(s, accounts, budgets)).collect(Collectors.toList());

        MainState.getInstance().initialize(transactions, accounts, budgets, savings);
    }

    /**
     * Creates a stream of the given lines consisting only of those lines that start with the given prefix,
     * but with the prefix removed.
     *
     * @param lines  The lines to clean
     * @param prefix The prefix to remove
     * @return Those lines which begin with prefix, with that prefix removed
     */
    private Stream<String> cleanLines(List<String> lines, String prefix) {
        return lines.stream().filter(s -> s.startsWith(prefix)).map(s -> s.substring(prefix.length()));
    }

    /**
     * Parses an account from a format that should match Account's toString() function.
     *
     * @param s The string to parse
     * @return An Account object encoded by this string, without Transaction information, or null if the string isn't
     * formatted properly
     */
    private Account parseAccount(String s) {
        String[] parts = s.trim().split(";");
        if (parts.length == 5) {
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException n) {
                return null;
            }
            String name = parts[1];
            Money starting = Money.parse(parts[2]);
            float interest;
            try {
                interest = Float.parseFloat(parts[3]);
            } catch (NumberFormatException n) {
                return null;
            }
            Money warning = Money.parse(parts[4]);
            return new Account(id, name, starting, warning, interest);
        }
        return null;
    }

    /**
     * Parses a {@link BudgetItem} from a format that should match BudgetItem's toString() function.
     *
     * @param s The string to parse
     * @return A BudgetItem object encoded by this string, without Transaction information, or null if the string isn't
     * formatted properly
     */
    private BudgetItem parseBudgetItem(String s) {
        String[] parts = s.trim().split(";");
        if (parts.length == 5) {
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException n) {
                return null;
            }
            boolean in = Boolean.parseBoolean(parts[1]);
            String name = parts[2];
            Money amount = Money.parse(parts[3]);
            boolean essential = Boolean.parseBoolean(parts[4]);
            return new BudgetItem(id, in, name, amount, essential);
        }
        return null;
    }

    /**
     * Parses a {@link SavingsItem} from a format that should match SavingsItem's toString() function.
     *
     * @param s The string to parse
     * @return A SavingsItem object encoded by this string, without Transaction information, or null if the string isn't
     * formatted properly
     */
    private SavingsItem parseSavingsItem(String s, List<Account> accounts) {
        String[] parts = s.trim().split(";");
        if (parts.length == 6) {
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException n) {
                return null;
            }
            String name = parts[1];
            Money goal = Money.parse(parts[2]);
            Money currentAmount = Money.parse(parts[3]);
            int account_id;
            try {
                account_id = Integer.parseInt(parts[4]);
            } catch (NumberFormatException n) {
                return null;
            }
            float allocation;
            try {
                allocation = Float.parseFloat(parts[5]);
            } catch (NumberFormatException n) {
                return null;
            }
            Account account = getAccount(account_id, accounts);
            return new SavingsItem(id, name, goal, currentAmount, account, allocation);

        }
        return null;
    }

    /**
     * Parses a {@link Transaction} from a format that should match Transaction's toString() function.
     *
     * @param s The string to parse
     * @return A Transaction object encoded by this string, or null if the string isn't
     * formatted properly
     */
    private Transaction parseTransaction(String s, List<Account> accounts, List<BudgetItem> budgets) {
        String[] parts = s.trim().split(";");
        if (parts.length == 7) {
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException n) {
                return null;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(parts[1], DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Couldn't parse " + parts[1]);
                return null;
            }
            String description = parts[2];
            Money amount = Money.parse(parts[3]);
            int budget_id;
            try {
                budget_id = Integer.parseInt(parts[4]);
            } catch (NumberFormatException n) {
                return null;
            }
            int account_id;
            try {
                account_id = Integer.parseInt(parts[5]);
            } catch (NumberFormatException n) {
                return null;
            }

            Account account = getAccount(account_id, accounts);
            BudgetItem budgetItem = getBudgetItem(budget_id, budgets);
            boolean included = Boolean.parseBoolean(parts[6]);
            return new Transaction(id, date, description, amount, budgetItem, account, included);
        }
        return null;
    }

    /**
     * Get the account with the given ID from the list of accounts, or {@link MainState#UNKNOWN_ACCOUNT} if not found.
     *
     * @param account_id ID of the account to fetch
     * @param accounts   List of accounts to search in
     * @return The account with the given ID, or UNKNOWN_ACCOUNT if not found
     */
    private Account getAccount(int account_id, List<Account> accounts) {
        return accounts.stream().filter(a -> a.getId() == account_id).findFirst().orElse(MainState.UNKNOWN_ACCOUNT);
    }

    /**
     * Get the budget with the given ID from the list of budgets, or {@link MainState#UNKNOWN_BUDGET} if not found.
     *
     * @param budget_id ID of the budget to fetch
     * @param budgets   List of budgets to search in
     * @return The budget with the given ID, or UNKNOWN_BUDGET if not found
     */
    private BudgetItem getBudgetItem(int budget_id, List<BudgetItem> budgets) {
        return budgets.stream().filter(b -> b.getId() == budget_id).findFirst().orElse(MainState.UNKNOWN_BUDGET);
    }
}
