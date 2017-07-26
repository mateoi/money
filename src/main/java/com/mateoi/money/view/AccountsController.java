package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;

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

    private Stage primaryStage;


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
    }

    @FXML
    private void onEditAccount() {
        Account account = mainTable.getSelectionModel().getSelectedItem();
        if (account != null) {
            editAccount(account, true);
        }
    }

    @FXML
    private void onAddAccount() {
        int newId = MainState.getInstance().getLastAccount() + 1;
        Account account = new Account(newId, "", Money.zero(Monetary.getCurrency("USD")), 0);
        Account result = editAccount(account, false);
        if (result != null) {
            MainState.getInstance().getAccounts().add(result);
            MainState.getInstance().setLastAccount(newId);
        }
    }

    @FXML
    private void onRemoveAccount() {
        Account account = mainTable.getSelectionModel().getSelectedItem();
        if (account != null) {
            String text = "Are you sure you want to delete the account \"" + account.getName() + "\"?";
            int transactions = account.getTransactions().size();
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
            }
        }

    }

    private Account editAccount(Account account, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/AccountEditDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle((edit ? "Edit" : "Create New") + " Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            AccountEditController controller = loader.getController();
            controller.setAccount(account);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isOkPressed()) {
                return controller.getAccount();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initializeMainTable() {
        mainTable.setItems(MainState.getInstance().getAccounts());
        mainTable.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                onEditAccount();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                mainTable.getSelectionModel().select(null);
            }
        });

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
        txBalanceColumn.setCellFactory(c -> new TableCell<Transaction, Money>() {
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
