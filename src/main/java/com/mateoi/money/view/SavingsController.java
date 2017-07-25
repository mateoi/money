package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.SavingsItem;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
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

        goalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getGoal())));
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getCurrentAmount())));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        accountColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<SavingsItem, Account> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getAccounts());
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

}
