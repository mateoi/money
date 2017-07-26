package com.mateoi.money.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

/**
 * Created by mateo on 26/07/2017.
 */
public class Accounts {
    private static Accounts ourInstance = new Accounts();

    private final StringProperty overview = new SimpleStringProperty();

    private final ObjectProperty<Money> totalMoney = new SimpleObjectProperty<>();

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
        overview.bind(Bindings.createStringBinding(this::createOverview, totalMoney));
    }

    private String createOverview() {
        return "Total money in all accounts: " + MoneyStringConverter.formatMoney(totalMoney.get());
    }

    private Money calculateTotalMoney() {
        Money zero = Money.zero(Monetary.getCurrency("USD"));
        CurrencyConversion conversion = MonetaryConversions.getConversion(Monetary.getCurrency("USD"));
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
