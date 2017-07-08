package com.mateoi.money.io;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.BudgetItem;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 06/07/2017.
 */
public class Parser {
    public static final Parser instance = new Parser();

    // Private constructor to prevent instantiation
    private Parser() {
        // Empty
    }

    public static Parser getInstance() {
        return instance;
    }

    /**
     * Parses an account from a format that should match Account's toString() function.
     *
     * @param s The string to parse
     * @return An Account object encoded by this string, without Transaction information, or null if the string isn't
     * formatted properly
     */
    public Account parseAccount(String s) {
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
     * Psrses a {@link BudgetItem} from a format that should match BudgetItem's toString() function.
     *
     * @param s The string to parse
     * @return A BudgetItem object encoded by this string, without Transaction information, or null if the string isn't
     * formatted properly
     */
    public BudgetItem parseBudgetItem(String s) {
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
}
