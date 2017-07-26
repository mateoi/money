package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
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
    private void initialize() {
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

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getAmount())));
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
        }

    }

    @FXML
    void onAddItem() {
        int newId = MainState.getInstance().getLastTransaction() + 1;
        Transaction transaction = new Transaction(newId, LocalDate.now(), "", Money.zero(Monetary.getCurrency("USD")), null, null);
        Transaction result = super.editItem(transaction, "/TransactionEditDialog.fxml", true);
        if (result != null) {
            MainState.getInstance().setLastTransaction(newId);
            MainState.getInstance().getTransactions().add(result);
        }
    }
}
