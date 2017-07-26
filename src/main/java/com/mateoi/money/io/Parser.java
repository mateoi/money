package com.mateoi.money.io;

import com.mateoi.money.model.*;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mateo on 06/07/2017.
 */
public class Parser {
    private static final Parser instance = new Parser();

    // Private constructor to prevent instantiation
    private Parser() {
        // Empty
    }

    public static Parser getInstance() {
        return instance;
    }

    public void parseIntoMainState(List<String> lines) {
        List<Account> accounts = cleanLines(lines, FilePrefixes.ACCOUNT_PREFIX).map(this::parseAccount).collect(Collectors.toList());
        List<BudgetItem> budgets = cleanLines(lines, FilePrefixes.BUDGET_PREFIX).map(this::parseBudgetItem).collect(Collectors.toList());
        List<SavingsItem> savings = cleanLines(lines, FilePrefixes.SAVINGS_PREFIX).map(s -> parseSavingsItem(s, accounts)).collect(Collectors.toList());
        List<Transaction> transactions = cleanLines(lines, FilePrefixes.TRANSACTION_PREFIX).map(s -> parseTransaction(s, accounts, budgets)).collect(Collectors.toList());
        int txCount = getCount(lines, FilePrefixes.TX_COUNT_PREFIX);
        int accountCount = getCount(lines, FilePrefixes.ACCOUNT_COUNT_PREFIX);
        int savingsCount = getCount(lines, FilePrefixes.SAVINGS_COUNT_PREFIX);
        int budgetCount = getCount(lines, FilePrefixes.BUDGET_COUNT_PREFIX);
        MainState.getInstance().initialize(transactions, accounts, budgets, savings, txCount, accountCount, savingsCount, budgetCount);
    }

    private int getCount(List<String> lines, String prefix) {
        List<String> countLine = cleanLines(lines, prefix).collect(Collectors.toList());
        int count = 0;
        if (countLine.size() > 0) {
            try {
                count = Integer.parseInt(countLine.get(0));
            } catch (NumberFormatException n) {
                count = 0;
            }
        }
        return count;
    }

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
        if (parts.length == 4) {
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
            return new Account(id, name, starting, interest);
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

    private Transaction parseTransaction(String s, List<Account> accounts, List<BudgetItem> budgets) {
        String[] parts = s.trim().split(";");
        if (parts.length == 6) {
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException n) {
                return null;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(parts[1]);
            } catch (DateTimeParseException e) {
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
            return new Transaction(id, date, description, amount, budgetItem, account);
        }
        return null;
    }

    private Account getAccount(int account_id, List<Account> accounts) {
        List<Account> correctId = accounts.stream().filter(a -> a.getAccountId() == account_id).collect(Collectors.toList());
        return correctId.size() > 0 ? correctId.get(0) : MainState.UNKNOWN_ACCOUNT;
    }

    private BudgetItem getBudgetItem(int budget_id, List<BudgetItem> budgets) {
        List<BudgetItem> correctId = budgets.stream().filter(b -> b.getItemId() == budget_id).collect(Collectors.toList());
        return correctId.size() > 0 ? correctId.get(0) : MainState.UNKNOWN_BUDGET;
    }
}
