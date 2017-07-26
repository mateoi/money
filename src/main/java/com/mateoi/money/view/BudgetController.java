package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
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
    private void initialize() {
        initializeMainTable();
        initializeLabels();
        initializeTxTable();
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
        BudgetItem budgetItem = new BudgetItem(newId, false, "", Money.zero(Monetary.getCurrency("USD")), false);
        BudgetItem result = super.editItem(budgetItem, "/BudgetEditDialog.fxml", false);
        if (result != null) {
            MainState.getInstance().getBudgetItems().add(result);
            MainState.getInstance().setLastAccount(newId);
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
            }
        }
    }

    @FXML
    private void onFlush() {

    }

    private void initializeTxTable() {
        table.getSelectionModel().selectedItemProperty().addListener((a, b, newValue) -> txTable.setItems(newValue.getTransactions()));
        txDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        txDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        txAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        txAccountColumn.setCellValueFactory(param -> param.getValue().accountProperty());

        txDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        txDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        txAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> txTable.getSelectionModel().getSelectedItem().getAmount())));
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
        remainingColumn.setCellFactory(c -> new TableCell<BudgetItem, Money>() {
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

}
