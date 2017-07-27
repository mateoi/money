package com.mateoi.money.view.controllers;

import com.mateoi.money.model.*;
import com.mateoi.money.view.DatePickerTableCell;
import com.mateoi.money.view.InOutTableCell;
import com.mateoi.money.view.MoneyTableCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.util.Optional;


/**
 * Created by mateo on 30/06/2017.
 */
public class BudgetController extends TabController<BudgetItem> {
    @FXML
    private TableView<BudgetItem> table;

    @FXML
    private TableColumn<BudgetItem, Boolean> inColumn;

    @FXML
    private TableColumn<BudgetItem, String> nameColumn;

    @FXML
    private TableColumn<BudgetItem, Money> amountColumn;

    @FXML
    private TableColumn<BudgetItem, Money> remainingColumn;

    @FXML
    private TableColumn<BudgetItem, Boolean> essentialColumn;

    @FXML
    private Label totalInLabel;

    @FXML
    private Label totalOutLabel;

    @FXML
    private Label totalSavingsLabel;

    @FXML
    private Label totalEssentialsLabel;

    @FXML
    private Label totalNonEssentialsLabel;

    @FXML
    private TableView<Transaction> txTable;

    @FXML
    private TableColumn<Transaction, LocalDate> txDateColumn;

    @FXML
    private TableColumn<Transaction, String> txDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Money> txAmountColumn;

    @FXML
    private TableColumn<Transaction, Account> txAccountColumn;

    @FXML
    private TableColumn<Transaction, Boolean> txIncludedColumn;


    @FXML
    private void initialize() {
        initializeMainTable();
        initializeLabels();
        initializeTxTable();
        Settings.getInstance().colorCodeProperty().addListener((a, b, c) -> {
            table.refresh();
            txTable.refresh();
        });
    }

    @FXML
    void onEditItem() {
        BudgetItem budgetItem = table.getSelectionModel().getSelectedItem();
        if (budgetItem != null) {
            super.editItem(budgetItem, "/BudgetEditDialog.fxml", true);
        }
    }

    @FXML
    void onAddItem() {
        int newId = MainState.getInstance().getLastBudget() + 1;
        BudgetItem budgetItem = new BudgetItem(newId, false, "", Money.zero(Settings.getInstance().getDefaultCurrency()), false);
        BudgetItem result = super.editItem(budgetItem, "/BudgetEditDialog.fxml", false);
        if (result != null) {
            MainState.getInstance().getBudgetItems().add(result);
            MainState.getInstance().setLastBudget(newId);
            MainState.getInstance().setModified(true);
        }
    }

    @FXML
    void onRemoveItem() {
        BudgetItem budgetItem = table.getSelectionModel().getSelectedItem();
        if (budgetItem != null) {
            String text = "Are you sure you want to delete the budget type \"" + budgetItem.getName() + "\"?";
            int transactions = budgetItem.getTransactions().size();
            if (transactions > 0) {
                String addendum = "\n" + transactions + " Transaction";
                addendum += transactions > 1 ? "s" : "";
                addendum += " will be orphaned.";
                text += addendum;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.OK, ButtonType.CANCEL);
            confirmation.setTitle("Delete budget type?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.orElse(ButtonType.CANCEL).equals(ButtonType.OK)) {
                MainState.getInstance().getBudgetItems().remove(budgetItem);
                MainState.getInstance().setModified(true);
            }
        }
    }

    @FXML
    private void onFlush() {
        BudgetItem budgetItem = table.getSelectionModel().getSelectedItem();
        if (budgetItem == null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to flush all budgets?", ButtonType.YES, ButtonType.NO);
            confirmation.setTitle("Flush all budgets?");
            ButtonType result = confirmation.showAndWait().orElse(ButtonType.NO);
            if (result.equals(ButtonType.YES)) {
                MainState.getInstance().getTransactions().forEach(Transaction::silentExclude);
                MainState.getInstance().getBudgetItems().forEach(BudgetItem::processTransactions);
            }
        } else {
            budgetItem.getTransactions().forEach(Transaction::silentExclude);
            budgetItem.processTransactions();
        }
    }

    private void initializeTxTable() {
        table.getSelectionModel().selectedItemProperty().addListener((a, b, newValue) -> {
            if (newValue != null) {
                txTable.setItems(newValue.getTransactions());
            } else {
                txTable.setItems(FXCollections.emptyObservableList());
            }
        });
        txDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        txDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        txAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        txAccountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        txIncludedColumn.setCellValueFactory(param -> param.getValue().includedProperty());

        txDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        txDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        txAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> txTable.getSelectionModel().getSelectedItem().getAmount(), Transaction::colorTransaction));
        txAccountColumn.setCellFactory(c -> {
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
        txIncludedColumn.setCellFactory(c -> new CheckBoxTableCell<>());
    }

    private void initializeLabels() {
        totalInLabel.setText(MoneyStringConverter.formatMoney(Budgets.getInstance().getTotalIn()));
        totalOutLabel.setText(MoneyStringConverter.formatMoney(Budgets.getInstance().getTotalOut()));
        totalSavingsLabel.setText(MoneyStringConverter.formatMoney(Budgets.getInstance().getTotalToSavings()));
        totalEssentialsLabel.setText(MoneyStringConverter.formatMoney(Budgets.getInstance().getTotalEssentials()));
        totalNonEssentialsLabel.setText(MoneyStringConverter.formatMoney(Budgets.getInstance().getTotalExtras()));


        Budgets.getInstance().totalInProperty().addListener((a, b, amount) -> totalInLabel.setText(MoneyStringConverter.formatMoney(amount)));
        Budgets.getInstance().totalOutProperty().addListener((a, b, amount) -> totalOutLabel.setText(MoneyStringConverter.formatMoney(amount)));
        Budgets.getInstance().totalToSavingsProperty().addListener((a, b, amount) -> totalSavingsLabel.setText(MoneyStringConverter.formatMoney(amount)));
        Budgets.getInstance().totalEssentialsProperty().addListener((a, b, amount) -> totalEssentialsLabel.setText(MoneyStringConverter.formatMoney(amount)));
        Budgets.getInstance().totalExtrasProperty().addListener((a, b, amount) -> totalNonEssentialsLabel.setText(MoneyStringConverter.formatMoney(amount)));
    }

    private void initializeMainTable() {
        table.setItems(MainState.getInstance().getBudgetItems());
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().select(null);
            } else if (event.getCode() == KeyCode.DELETE) {
                onRemoveItem();
            }
        });
        inColumn.setCellValueFactory(param -> param.getValue().inProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        remainingColumn.setCellValueFactory(param -> param.getValue().remainingProperty());
        essentialColumn.setCellValueFactory(param -> param.getValue().essentialProperty());

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        inColumn.setCellFactory(c -> new InOutTableCell<>());
        essentialColumn.setCellFactory(c -> new CheckBoxTableCell<>());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getAmount())));
        remainingColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> table.getSelectionModel().getSelectedItem().getRemaining(), BudgetItem::colorBudget));
        remainingColumn.setEditable(false);
    }

}
