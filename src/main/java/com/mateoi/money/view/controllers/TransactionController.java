package com.mateoi.money.view.controllers;

import com.mateoi.money.model.*;
import com.mateoi.money.view.DatePickerTableCell;
import com.mateoi.money.view.MoneyTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.time.LocalDate;

/**
 * Created by mateo on 30/06/2017.
 */
public class TransactionController extends TabController<Transaction> {
    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, Money> amountColumn;

    @FXML
    private TableColumn<Transaction, Account> accountColumn;

    @FXML
    private TableColumn<Transaction, BudgetItem> typeColumn;

    @FXML
    private TableView<Transaction> accountTable;

    @FXML
    private TableColumn<Transaction, LocalDate> accountDateColumn;

    @FXML
    private TableColumn<Transaction, String> accountDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Money> accountAmountColumn;

    @FXML
    private TableColumn<Transaction, Money> accountBalanceColumn;

    @FXML
    private TableView<Transaction> budgetTable;

    @FXML
    private TableColumn<Transaction, LocalDate> budgetDateColumn;

    @FXML
    private TableColumn<Transaction, String> budgetDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Money> budgetAmountColumn;

    @FXML
    private TableColumn<Transaction, Boolean> budgetIncludedColumn;

    @FXML
    private Label accountLabel;

    @FXML
    private Label budgetLabel;

    private ObjectProperty<Transaction> selectedTransaction = new SimpleObjectProperty<>();

    private ObjectProperty<Account> selectedTransactionAccount = new SimpleObjectProperty<>();

    private ObjectProperty<BudgetItem> selectedTransactionType = new SimpleObjectProperty<>();


    @FXML
    private void initialize() {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTransaction.set(newValue);
                selectedTransactionAccount.bind(newValue.accountProperty());
                selectedTransactionType.bind(newValue.budgetTypeProperty());
            }
        });

        initializeMainTable();
        initializeAccountTable();
        initializeBudgetTable();
        initializeLabels();
        Settings.getInstance().colorCodeProperty().addListener((a, b, c) -> {
            table.refresh();
            accountTable.refresh();
            budgetTable.refresh();
        });

    }


    @FXML
    void onEditItem() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            super.editItem(transaction, "/TransactionEditDialog.fxml", true);
        }
    }

    @FXML
    void onRemoveItem() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            MainState.getInstance().getTransactions().remove(transaction);
            transaction.getAccount().getTransactions().remove(transaction);
            transaction.getBudgetType().getTransactions().remove(transaction);
            MainState.getInstance().setModified(true);
        }

    }

    @FXML
    void onAddItem() {
        int newId = MainState.getInstance().getLastTransaction() + 1;
        Transaction transaction = new Transaction(newId, LocalDate.now(), "", Money.zero(Settings.getInstance().getDefaultCurrency()), null, null, true);
        Transaction result = super.editItem(transaction, "/TransactionEditDialog.fxml", true);
        if (result != null) {
            MainState.getInstance().setLastTransaction(newId);
            MainState.getInstance().getTransactions().add(result);
            MainState.getInstance().setModified(true);
        }
    }

    private void initializeMainTable() {
        table.setItems(MainState.getInstance().getTransactions());
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().select(null);
            } else if (event.getCode() == KeyCode.DELETE) {
                onRemoveItem();
            }
        });
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (table.getSelectionModel().getSelectedItem() == null) {
                    onAddItem();
                }
            }
        });

        dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        accountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().budgetTypeProperty());

        amountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> table.getSelectionModel().getSelectedItem().getAmount(), Transaction::colorTransaction));
        dateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        accountColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<Transaction, Account> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getAccounts());
            cell.setConverter(new StringConverter<Account>() {
                @Override
                public String toString(Account account) {
                    return account.getName();
                }

                @Override
                public Account fromString(String string) {
                    return MainState.getInstance().getAccounts().stream().
                            filter(a -> a.getName().equals(string)).
                            findFirst().
                            orElse(MainState.UNKNOWN_ACCOUNT);
                }
            });
            return cell;
        });
        typeColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<Transaction, BudgetItem> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getBudgetItems());
            cell.setConverter(new StringConverter<BudgetItem>() {
                @Override
                public String toString(BudgetItem budget) {
                    return budget.getName();
                }

                @Override
                public BudgetItem fromString(String string) {
                    return MainState.getInstance().getBudgetItems().stream().
                            filter(b -> b.getName().equals(string)).
                            findFirst().
                            orElse(MainState.UNKNOWN_BUDGET);
                }
            });
            return cell;
        });
    }

    private void initializeBudgetTable() {
        selectedTransactionType.addListener((a, b, newValue) -> {
            if (newValue != null) {
                budgetTable.setItems(newValue.getTransactions());
            }
        });

        budgetAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        budgetDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        budgetDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        budgetIncludedColumn.setCellValueFactory(param -> param.getValue().includedProperty());

        budgetAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> budgetTable.getSelectionModel().getSelectedItem().getAmount(), Transaction::colorTransaction));
        budgetDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        budgetDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        budgetIncludedColumn.setCellFactory(c -> new CheckBoxTableCell<>());
    }

    private void initializeAccountTable() {
        selectedTransactionAccount.addListener((a, b, newValue) -> {
            if (newValue != null) {
                accountTable.setItems(newValue.getTransactions());
                accountBalanceColumn.setCellValueFactory(param -> newValue.balanceAtTransactionProperty(param.getValue()));
                accountTable.refresh();
            }
        });

        accountAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        accountDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        accountDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        accountBalanceColumn.setCellValueFactory(param -> selectedTransactionAccount.get().balanceAtTransactionProperty(param.getValue()));

        accountAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> budgetTable.getSelectionModel().getSelectedItem().getAmount(), Transaction::colorTransaction));
        accountDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        accountDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        accountBalanceColumn.setCellFactory(c -> new TableCell<Transaction, Money>() {
            @Override
            protected void updateItem(Money item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(MoneyStringConverter.formatMoney(item));
                }
            }
        });
    }

    private void initializeLabels() {
        accountLabel.textProperty().bind(Bindings.createStringBinding(this::createAccountString, selectedTransactionAccount));
        budgetLabel.textProperty().bind(Bindings.createStringBinding(this::createBudgetString, selectedTransactionType));
    }

    private String createAccountString() {
        Account account = selectedTransactionAccount.get();
        if (account == null) {
            return "";
        } else {
            return account.getName() + " transaction details";
        }
    }

    private String createBudgetString() {
        BudgetItem budgetItem = selectedTransactionType.get();
        if (budgetItem == null) {
            return "";
        } else {
            return budgetItem.getName() + " transaction details";
        }
    }
}
