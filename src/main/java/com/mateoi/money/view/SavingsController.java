package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.SavingsItem;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 30/06/2017.
 */
public class SavingsController {

    @FXML
    private TableView<SavingsItem> table;

    @FXML
    private TableColumn<SavingsItem, String> descriptionColumn;

    @FXML
    private TableColumn<SavingsItem, Money> goalColumn;

    @FXML
    private TableColumn<SavingsItem, Money> amountColumn;

    @FXML
    private TableColumn<SavingsItem, Number> progressColumn;

    @FXML
    private TableColumn<SavingsItem, Account> accountColumn;

    @FXML
    private TableColumn<SavingsItem, Number> allocationColumn;


    @FXML
    private void initialize() {
        table.setItems(MainState.getInstance().getSavingsItems());

        descriptionColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        goalColumn.setCellValueFactory(param -> param.getValue().goalProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().currentAmountProperty());
        progressColumn.setCellValueFactory(param -> param.getValue().progressProperty());
        accountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        allocationColumn.setCellValueFactory(param -> param.getValue().allocationProperty());

        accountColumn.setCellFactory(c -> new TableCell<SavingsItem, Account>() {
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
    }

}
