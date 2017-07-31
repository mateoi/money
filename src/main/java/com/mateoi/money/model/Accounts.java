package com.mateoi.money.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

/**
 * Useful bindings related to accounts. Singleton class.
 */
public class Accounts {
    private static Accounts ourInstance = new Accounts();

    /**
     * Overview of all accounts
     */
    private final StringProperty overview = new SimpleStringProperty();

    /**
     * Total money in all accounts
     */
    private final ObjectProperty<Money> totalMoney = new SimpleObjectProperty<>();

    /**
     * Get the singleton instance of this class
     *
     * @return The instance of Accounts.
     */
    public static Accounts getInstance() {
        return ourInstance;
    }

    private Accounts() {
        ObservableList<Account> accounts = MainState.getInstance().getAccounts();
        ChangeListener<Object> listener = (a, b, c) -> totalMoney.set(calculateTotalMoney());
        totalMoney.set(calculateTotalMoney());
        for (Account account : accounts) {
            account.currentBalanceProperty().addListener(listener);
        }
        accounts.addListener((ListChangeListener<? super Account>) c -> {
            while (c.next()) {
                for (Account account : c.getAddedSubList()) {
                    account.currentBalanceProperty().addListener(listener);
                }
                totalMoney.set(calculateTotalMoney());
            }
        });
        Settings.getInstance().defaultCurrencyProperty().addListener((a, b, c) -> {
            totalMoney.set(calculateTotalMoney());
            overview.set(createOverview());
        });
        totalMoney.addListener((a, b, c) -> overview.set(createOverview()));
        overview.set(createOverview());
    }

    /**
     * Creates the string shown in the account overview
     *
     * @return A String detailing the total money in all accounts
     */
    private String createOverview() {
        return "Total money in all accounts: " + MoneyStringConverter.formatMoney(totalMoney.get());
    }

    /**
     * Calculates the total amount in all accounts
     *
     * @return The total amount of money in all accounts, in the default currency
     */
    private Money calculateTotalMoney() {
        CurrencyUnit currencyUnit = Settings.getInstance().getDefaultCurrency();
        Money zero = Money.zero(currencyUnit);
        CurrencyConversion conversion = MonetaryConversions.getConversion(currencyUnit);
        return MainState.getInstance().getAccounts().stream().
                map(Account::getCurrentBalance).
                map(m -> m.with(conversion)).
                reduce(zero, Money::add);
    }

    public String getOverview() {
        return overview.get();
    }

    public StringProperty overviewProperty() {
        return overview;
    }
}
