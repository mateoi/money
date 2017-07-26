package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.util.Optional;

/**
 * Created by mateo on 30/06/2017.
 */
public class SavingsController extends TabController<SavingsItem> {

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
        table.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                onEditSavingsItem();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().select(null);
            } else if (event.getCode() == KeyCode.DELETE) {
                onRemoveSavingsItem();
            }
        });
        
        descriptionColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        goalColumn.setCellValueFactory(param -> param.getValue().goalProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().currentAmountProperty());
        progressColumn.setCellValueFactory(param -> param.getValue().progressProperty());
        accountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        allocationColumn.setCellValueFactory(param -> param.getValue().allocationProperty());

        goalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getGoal())));
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getCurrentAmount())));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        progressColumn.setCellFactory(TextFieldTableCell.forTableColumn(new PercentageStringConverter(() -> table.getSelectionModel().getSelectedItem().getProgress())));
        allocationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new PercentageStringConverter(() -> table.getSelectionModel().getSelectedItem().getAllocation())));
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

    @FXML
    private void onEditSavingsItem() {
        SavingsItem savingsItem = table.getSelectionModel().getSelectedItem();
        if (savingsItem != null) {
            super.editItem(savingsItem, "/SavingsEditDialog.fxml", true);
        }
    }

    @FXML
    private void onAddSavingsItem() {
        int newId = MainState.getInstance().getLastSavings() + 1;
        SavingsItem savingsItem = new SavingsItem(newId, "", Money.zero(Monetary.getCurrency("USD")), Money.zero(Monetary.getCurrency("USD")), null, 0);
        SavingsItem result = super.editItem(savingsItem, "/SavingsEditDialog.fxml", false);
        if (result != null) {
            MainState.getInstance().getSavingsItems().add(result);
            MainState.getInstance().setLastSavings(newId);
        }
    }

    @FXML
    private void onRemoveSavingsItem() {
        SavingsItem savingsItem = table.getSelectionModel().getSelectedItem();
        if (savingsItem != null) {
            String text = "Are you sure you want to delete the savings goal \"" + savingsItem.getName() + "\"?";
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.orElse(ButtonType.CANCEL).equals(ButtonType.OK)) {
                MainState.getInstance().getSavingsItems().remove(savingsItem);
            }
        }
    }


}
