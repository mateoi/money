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
    private TableColumn<Transaction, BudgetItem> typeColumn;

    @FXML
    private TableView<SubTransaction> stTable;

    @FXML
    private TableColumn<SubTransaction, Account> stAccountColumn;

    @FXML
    private TableColumn<SubTransaction, Money> stAmountColumn;

    @FXML
    private TableColumn<SubTransaction, Money> stBalanceColumn;

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
    private Label budgetLabel;

    private ObjectProperty<Transaction> selectedTransaction = new SimpleObjectProperty<>();

    private ObjectProperty<BudgetItem> selectedTransactionType = new SimpleObjectProperty<>();


    @FXML
    private void initialize() {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTransaction.set(newValue);
                stTable.setItems(newValue.getSubTransactions());
                selectedTransactionType.bind(newValue.budgetTypeProperty());
            }
        });

        initializeMainTable();
        initializeSubTransactionTable();
        initializeBudgetTable();
        budgetLabel.textProperty().bind(Bindings.createStringBinding(this::createBudgetString, selectedTransactionType));
        Settings.getInstance().colorCodeProperty().addListener((a, b, c) -> {
            table.refresh();
            stTable.refresh();
            budgetTable.refresh();
        });

    }


    @FXML
    void onEditItem() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            Transaction result = super.editItem(transaction, "/TransactionEditDialog.fxml", true);
            if (result != null) {
                MainState.getInstance().setModified(true);
            }
        }
    }

    @FXML
    void onRemoveItem() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            MainState.getInstance().getTransactions().remove(transaction);
            transaction.getBudgetType().getTransactions().remove(transaction);
            MainState.getInstance().getSubTransactions().removeAll(transaction.getSubTransactions());
            MainState.getInstance().setModified(true);
        }
    }

    @FXML
    void onAddItem() {
        int newId = MainState.getInstance().getLastTransaction() + 1;
        Transaction transaction = new Transaction(newId, LocalDate.now(), "", null, true);
        int newID_st = MainState.getInstance().getLastSubTransaction() + 1;
        MainState.getInstance().setLastSubTransaction(newID_st);
        SubTransaction subTransaction = new SubTransaction(newID_st, null, Money.zero(transaction.getCurrency()), transaction);
        transaction.getSubTransactions().add(subTransaction);
        Transaction result = super.editItem(transaction, "/TransactionEditDialog.fxml", false);
        if (result != null) {
            MainState.getInstance().setLastTransaction(newId);
            MainState.getInstance().getTransactions().add(result);
            MainState.getInstance().getSubTransactions().addAll(result.getSubTransactions());
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
        amountColumn.setCellValueFactory(param -> param.getValue().totalAmountProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().budgetTypeProperty());

        amountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> table.getSelectionModel().getSelectedItem().getTotalAmount(), Transaction::colorTransaction));
        dateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
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

        budgetAmountColumn.setCellValueFactory(param -> param.getValue().totalAmountProperty());
        budgetDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        budgetDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        budgetIncludedColumn.setCellValueFactory(param -> param.getValue().includedProperty());

        budgetAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> budgetTable.getSelectionModel().getSelectedItem().getTotalAmount(), Transaction::colorTransaction));
        budgetDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        budgetDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        budgetIncludedColumn.setCellFactory(c -> new CheckBoxTableCell<>());
    }

    private void initializeSubTransactionTable() {
        stAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        stAccountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        stBalanceColumn.setCellValueFactory(param -> param.getValue().accountBalanceProperty());

        stAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> stTable.getSelectionModel().getSelectedItem().getAmount(), SubTransaction::colorSubTransaction));
        stAccountColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<SubTransaction, Account> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getAccounts());
            cell.setConverter(new StringConverter<Account>() {
                @Override
                public String toString(Account account) {
                    return account.getName();
                }

                @Override
                public Account fromString(String string) {
                    return MainState.getInstance().getAccounts().stream().
                            filter(b -> b.getName().equals(string)).
                            findFirst().
                            orElse(MainState.UNKNOWN_ACCOUNT);
                }
            });
            return cell;
        });
        stBalanceColumn.setCellFactory(c -> new TableCell<SubTransaction, Money>() {
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

    private String createBudgetString() {
        BudgetItem budgetItem = selectedTransactionType.get();
        if (budgetItem == null) {
            return "";
        } else {
            return budgetItem.getName() + " budget details";
        }
    }
}
