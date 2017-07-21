package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Transaction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
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

    @FXML
    private void initialize() {
        initializeMainTable();
        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, o, newValue) -> {
            if (newValue != null) {
                selectedAccount.set(newValue);
            }
        });
        initializeTxTable();
        initializeLabels();
    }

    private void initializeMainTable() {
        mainTable.setItems(MainState.getInstance().getAccounts());
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().currentBalanceProperty());
        transactionsColumn.setCellValueFactory(param -> Bindings.size(param.getValue().getTransactions()));
        interestColumn.setCellValueFactory(param -> param.getValue().annualInterestProperty());

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

        txDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        txTypeColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<Transaction, BudgetItem> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getBudgetItems());
            cell.setConverter(new StringConverter<BudgetItem>() {
                @Override
                public String toString(BudgetItem budget) {
                    return budget.toString();
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

    private void initializeLabels() {
        nameLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getName(), selectedAccount));
        balanceLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getCurrentBalance().toString(), selectedAccount));
        minBalanceLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getMinimumBalance().toString(), selectedAccount));
        maxBalanceLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getMaximumBalance().toString(), selectedAccount));
        avgBalanceLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getAverageBalance().toString(), selectedAccount));
        avgDepositLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getAverageDeposit().toString(), selectedAccount));
        avgWithdrawalLabel.textProperty().bind(Bindings.createStringBinding(() -> selectedAccount.getValue().getAverageWithdrawal().toString(), selectedAccount));
        txNumberLabel.textProperty().bind(Bindings.createIntegerBinding(() -> selectedAccount.getValue().getTxNumber().intValue(), selectedAccount, selectedAccount.getValue().txNumberProperty()).asString());
        interestLabel.textProperty().bind(Bindings.createFloatBinding(() -> selectedAccount.getValue().getAnnualInterest(), selectedAccount).asString());
    }

}
