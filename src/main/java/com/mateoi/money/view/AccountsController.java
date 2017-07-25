package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Transaction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Created by mateo on 30/06/2017.
 */
public class AccountsController {
    @FXML
    private TableView<Account> mainTable;

    @FXML
    private TableColumn<Account, String> nameColumn;

    @FXML
    private TableColumn<Account, Money> amountColumn;

    @FXML
    private TableColumn<Account, Number> transactionsColumn;

    @FXML
    private TableColumn<Account, Number> interestColumn;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> txDescriptionColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> txDateColumn;

    @FXML
    private TableColumn<Transaction, Money> txAmountColumn;

    @FXML
    private TableColumn<Transaction, BudgetItem> txTypeColumn;

    @FXML
    private TableColumn<Transaction, Money> txBalanceColumn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label maxBalanceLabel;

    @FXML
    private Label avgBalanceLabel;

    @FXML
    private Label minBalanceLabel;

    @FXML
    private Label txNumberLabel;

    @FXML
    private Label avgDepositLabel;

    @FXML
    private Label avgWithdrawalLabel;

    @FXML
    private Label interestLabel;

    private ObjectProperty<Account> selectedAccount = new SimpleObjectProperty<>();

    private ChangeListener<Number> transactionNumberListener = (observable, oldValue, newValue) ->
            txNumberLabel.setText(Integer.toString(newValue.intValue()));


    @FXML
    private void initialize() {
        initializeMainTable();
        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.txNumberProperty().removeListener(transactionNumberListener);
            }
            if (newValue != null) {
                selectedAccount.set(newValue);
                initializeLabels(newValue);
            }
        });
        initializeTxTable();
    }

    private void initializeMainTable() {
        mainTable.setItems(MainState.getInstance().getAccounts());

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().currentBalanceProperty());
        transactionsColumn.setCellValueFactory(param -> Bindings.size(param.getValue().getTransactions()));
        interestColumn.setCellValueFactory(param -> param.getValue().annualInterestProperty());

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> mainTable.getSelectionModel().getSelectedItem().getCurrentBalance())));
        interestColumn.setCellFactory(c -> new TableCell<Account, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(new DecimalFormat("#.##").format(item.floatValue()) + "%");
                }
            }

        });
    }

    private void initializeTxTable() {
        selectedAccount.addListener((obs, o, newValue) -> transactionTable.setItems(newValue.getTransactions()));
        txDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        txDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        txAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        txTypeColumn.setCellValueFactory(param -> param.getValue().budgetTypeProperty());
        txBalanceColumn.setCellValueFactory(param -> selectedAccount.getValue().balanceAtTransactionProperty(param.getValue()));

        txDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        txDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        txAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> transactionTable.getSelectionModel().getSelectedItem().getAmount())));
        txTypeColumn.setCellFactory(c -> {
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

    private void initializeLabels(Account account) {
        account.txNumberProperty().addListener(transactionNumberListener);

        nameLabel.setText(account.getName());
        balanceLabel.setText(account.getCurrentBalance().toString());
        minBalanceLabel.setText(account.getMinimumBalance().toString());
        maxBalanceLabel.setText(account.getMaximumBalance().toString());
        avgBalanceLabel.setText(account.getAverageBalance().toString());
        avgDepositLabel.setText(account.getAverageDeposit().toString());
        avgWithdrawalLabel.setText(account.getAverageWithdrawal().toString());
        txNumberLabel.setText(Integer.toString(account.getTxNumber().intValue()));
        interestLabel.setText(Float.toString(account.getAnnualInterest()));
    }

}
