package com.mateoi.money.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by mateo on 30/06/2017.
 */
public class Transaction {
    private final int transactionId;

    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Money> totalAmount = new SimpleObjectProperty<>(Money.zero(Settings.getInstance().getDefaultCurrency()));

    private ObjectProperty<BudgetItem> budgetType = new SimpleObjectProperty<>();

    private BooleanProperty included = new SimpleBooleanProperty();

    private ObservableList<SubTransaction> subTransactions = FXCollections.observableArrayList();

    private ChangeListener<Boolean> includedListener = (a, b, c) -> {
        if (budgetType.get() != null) {
            budgetType.get().processTransactions();
        }
    };

    public Transaction(int id, LocalDate date, String description, BudgetItem budgetType, boolean included) {
        this.transactionId = id;
        this.date.setValue(date);
        this.description.setValue(description);
        this.budgetType.setValue(budgetType);
        this.included.set(included);

        this.totalAmount.addListener((observable, oldValue, newValue) -> {
            if (this.budgetType.get() != null) {
                this.budgetType.get().processTransactions();
            }
            MainState.getInstance().setModified(true);
        });
        this.budgetType.addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getTransactions().remove(Transaction.this);
            }
            if (newValue != null) {
                newValue.getTransactions().add(Transaction.this);
            }
            MainState.getInstance().setModified(true);
        }));
        this.date.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.description.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.included.addListener((a, b, c) -> MainState.getInstance().setModified(true));
        this.included.addListener(includedListener);
        this.subTransactions.addListener((ListChangeListener<? super SubTransaction>) c -> updateTotal());
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Transaction) {
            Transaction t = (Transaction) o;
            boolean ids = transactionId == t.getId();
            boolean dates = this.date.get().equals(t.getDate());
            boolean descriptions = this.description.get().equals(t.getDescription());
            boolean budgets = this.budgetType.get().equals(t.getBudgetType());
            boolean amounts = this.totalAmount.get().equals(t.getTotalAmount());
            boolean flushes = isIncluded() == t.isIncluded();
            return ids && dates && descriptions && budgets && amounts && flushes;
        } else {
            return false;
        }
    }

    public String toString() {
        return String.valueOf(transactionId) +
                ";" +
                DateTimeFormatter.ISO_DATE.format(date.get()) +
                ";" +
                description.get() +
                ";" +
                budgetType.get().getId() +
                ";" +
                included.get();
    }

    public Color colorTransaction() {
        if (totalAmount.get().isPositive()) {
            return Color.GREEN;
        } else if (totalAmount.get().isNegative()) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

    public void updateTotal() {
        CurrencyUnit currency = getCurrency();
        Money total = Money.zero(currency);
        CurrencyConversion conversion = MonetaryConversions.getConversion(currency);
        for (SubTransaction subTransaction : subTransactions) {
            total = total.add(subTransaction.getAmount().with(conversion));
        }
        totalAmount.set(total);
    }

    public CurrencyUnit getCurrency() {
        if (subTransactions.size() == 0) {
            return Settings.getInstance().getDefaultCurrency();
        } else {
            CurrencyUnit currency = subTransactions.get(0).getAmount().getCurrency();
            if (subTransactions.stream().map(st -> st.getAmount().getCurrency()).anyMatch(c -> !c.equals(currency))) {
                return Settings.getInstance().getDefaultCurrency();
            } else {
                return currency;
            }
        }
    }

    public LocalDate getDate() {
        return date.get();
    }

    public String getDescription() {
        return description.get();
    }


    public BudgetItem getBudgetType() {
        return budgetType.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty descriptionProperty() {
        return description;
    }


    public ObjectProperty<BudgetItem> budgetTypeProperty() {
        return budgetType;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setBudgetType(BudgetItem budgetType) {
        this.budgetType.set(budgetType);
    }

    public Money getTotalAmount() {
        return totalAmount.get();
    }

    public ObjectProperty<Money> totalAmountProperty() {
        return totalAmount;
    }

    public int getId() {
        return transactionId;
    }

    public boolean isIncluded() {
        return included.get();
    }

    public BooleanProperty includedProperty() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included.set(included);
    }

    public ObservableList<SubTransaction> getSubTransactions() {
        return subTransactions;
    }

    public void silentExclude() {
        this.included.removeListener(includedListener);
        this.included.set(false);
        this.included.addListener(includedListener);
    }

}
