package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.javamoney.moneta.Money;

import java.time.LocalDate;

/**
 * Created by mateo on 30/06/2017.
 */
public class TransactionController {
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

        dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        accountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().budgetTypeProperty());

        dateColumn.setCellFactory(c -> new DatePickerCell<>());
        accountColumn.setCellFactory(c -> new TableCell<Transaction, Account>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        typeColumn.setCellFactory(c -> new TableCell<Transaction, BudgetItem>() {
            @Override
            protected void updateItem(BudgetItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }
}
