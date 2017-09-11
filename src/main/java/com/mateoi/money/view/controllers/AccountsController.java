package com.mateoi.money.view.controllers;

import com.mateoi.money.model.*;
import com.mateoi.money.view.DatePickerTableCell;
import com.mateoi.money.view.MoneyTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
public class AccountsController extends TabController<Account> {
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
    private TableView<SubTransaction> stTable;

    @FXML
    private TableColumn<SubTransaction, String> stDescriptionColumn;

    @FXML
    private TableColumn<SubTransaction, LocalDate> stDateColumn;

    @FXML
    private TableColumn<SubTransaction, Money> stAmountColumn;

    @FXML
    private TableColumn<SubTransaction, BudgetItem> stTypeColumn;

    @FXML
    private TableColumn<SubTransaction, Money> stBalanceColumn;

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
        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {
                removeListeners(oldValue);
            }
            if (newValue != null) {
                selectedAccount.set(newValue);
                initializeLabels(newValue);
            }
        });
        initializeTxTable();
        Settings.getInstance().colorCodeProperty().addListener((a, b, c) -> {
            mainTable.refresh();
            stTable.refresh();
        });
    }

    @FXML
    void onEditItem() {
        Account account = mainTable.getSelectionModel().getSelectedItem();
        if (account != null) {
            super.editItem(account, "/AccountEditDialog.fxml", true);
        }
    }

    @FXML
    void onAddItem() {
        int newId = MainState.getInstance().getLastAccount() + 1;
        Account account = new Account(newId, "", Money.zero(Settings.getInstance().getDefaultCurrency()), Money.zero(Settings.getInstance().getDefaultCurrency()), 0);
        Account result = super.editItem(account, "/AccountEditDialog.fxml", false);
        if (result != null) {
            MainState.getInstance().getAccounts().add(result);
            MainState.getInstance().setLastAccount(newId);
            MainState.getInstance().setModified(true);
        }
    }

    @FXML
    void onRemoveItem() {
        Account account = mainTable.getSelectionModel().getSelectedItem();
        if (account != null) {
            String text = "Are you sure you want to delete the account \"" + account.getName() + "\"?";
            int transactions = account.getSubTransactions().size();
            if (transactions > 0) {
                String addendum = "\n" + transactions + " Transaction";
                addendum += transactions > 1 ? "s" : "";
                addendum += " will be orphaned.";
                text += addendum;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.OK, ButtonType.CANCEL);
            confirmation.setTitle("Delete Account?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.orElse(ButtonType.CANCEL).equals(ButtonType.OK)) {
                MainState.getInstance().getAccounts().remove(account);
                MainState.getInstance().setModified(true);
            }
        }
    }

    private void initializeMainTable() {
        mainTable.setItems(MainState.getInstance().getAccounts());
        mainTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                mainTable.getSelectionModel().select(null);
            } else if (event.getCode() == KeyCode.DELETE) {
                onRemoveItem();
            }
        });

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().currentBalanceProperty());
        transactionsColumn.setCellValueFactory(param -> Bindings.size(param.getValue().getSubTransactions()));
        interestColumn.setCellValueFactory(param -> param.getValue().annualInterestProperty());

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> mainTable.getSelectionModel().getSelectedItem().getCurrentBalance(),
                Account::colorAccount));
        interestColumn.setCellFactory(TextFieldTableCell.forTableColumn(new PercentageStringConverter(() -> mainTable.getSelectionModel().getSelectedItem().getAnnualInterest())));
    }

    private void initializeTxTable() {
        selectedAccount.addListener((obs, o, newValue) -> stTable.setItems(newValue.getSubTransactions()));
        stDescriptionColumn.setCellValueFactory(param -> param.getValue().txDescriptionProperty());
        stDateColumn.setCellValueFactory(param -> param.getValue().txDateProperty());
        stAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        stTypeColumn.setCellValueFactory(param -> param.getValue().txTypeProperty());
        stBalanceColumn.setCellValueFactory(param -> selectedAccount.get().balanceAtTransactionProperty(param.getValue()));

        stDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        stDateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        stAmountColumn.setCellFactory(MoneyTableCell.forTableColumn(() -> stTable.getSelectionModel().getSelectedItem().getAmount(), SubTransaction::colorSubTransaction));
        stTypeColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<SubTransaction, BudgetItem> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getBudgetItems());
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

    private void initializeLabels(Account account) {
        addLabelListeners(account);
        nameLabel.setText(account.getName());
        balanceLabel.setText(MoneyStringConverter.formatMoney(account.getCurrentBalance()));
        minBalanceLabel.setText(MoneyStringConverter.formatMoney(account.getMinimumBalance()));
        maxBalanceLabel.setText(MoneyStringConverter.formatMoney(account.getMaximumBalance()));
        avgBalanceLabel.setText(MoneyStringConverter.formatMoney(account.getAverageBalance()));
        avgDepositLabel.setText(MoneyStringConverter.formatMoney(account.getAverageDeposit()));
        avgWithdrawalLabel.setText(MoneyStringConverter.formatMoney(account.getAverageWithdrawal()));
        txNumberLabel.setText(Integer.toString(account.getTxNumber().intValue()));
        interestLabel.setText(Float.toString(account.getAnnualInterest()));


    }

    private ChangeListener<String> nameListener = (observable, oldValue, newValue) -> nameLabel.setText(newValue);

    private ChangeListener<Money> balanceListener = (observable, oldValue, newValue) -> balanceLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Money> minBalanceListener = (observable, oldValue, newValue) -> minBalanceLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Money> maxBalanceListener = (observable, oldValue, newValue) -> maxBalanceLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Money> avgBalanceListener = (observable, oldValue, newValue) -> avgBalanceLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Money> avgDepositListener = (observable, oldValue, newValue) -> avgDepositLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Money> avgWithdrawalListener = (observable, oldValue, newValue) -> avgWithdrawalLabel.setText(MoneyStringConverter.formatMoney(newValue));

    private ChangeListener<Number> transactionNumberListener = (observable, oldValue, newValue) ->
            txNumberLabel.setText(Integer.toString(newValue.intValue()));


    private ChangeListener<Number> interestListener = (observable, oldValue, newValue) -> interestLabel.setText(newValue.toString());


    private void addLabelListeners(Account account) {
        account.nameProperty().addListener(nameListener);
        account.currentBalanceProperty().addListener(balanceListener);
        account.minimumBalanceProperty().addListener(minBalanceListener);
        account.maximumBalanceProperty().addListener(maxBalanceListener);
        account.averageBalanceProperty().addListener(avgBalanceListener);
        account.averageDepositProperty().addListener(avgDepositListener);
        account.averageWithdrawalProperty().addListener(avgWithdrawalListener);
        account.txNumberProperty().addListener(transactionNumberListener);
        account.annualInterestProperty().addListener(interestListener);
    }

    private void removeListeners(Account account) {
        account.nameProperty().removeListener(nameListener);
        account.currentBalanceProperty().removeListener(balanceListener);
        account.minimumBalanceProperty().removeListener(minBalanceListener);
        account.maximumBalanceProperty().removeListener(maxBalanceListener);
        account.averageBalanceProperty().removeListener(avgBalanceListener);
        account.averageDepositProperty().removeListener(avgDepositListener);
        account.averageWithdrawalProperty().removeListener(avgWithdrawalListener);
        account.txNumberProperty().removeListener(transactionNumberListener);
        account.annualInterestProperty().removeListener(interestListener);
    }
}
