package com.mateoi.money.io;

import com.mateoi.money.model.Account;
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
     * @param s The toString to parse
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

}
