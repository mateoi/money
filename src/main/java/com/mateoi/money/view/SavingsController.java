package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by mateo on 30/06/2017.
 */
public class SavingsController extends SubNode {

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
            editSavingsItem(savingsItem, true);
        }
    }

    @FXML
    private void onAddSavingsItem() {
        int newId = MainState.getInstance().getLastSavings() + 1;
        SavingsItem savingsItem = new SavingsItem(newId, "", Money.zero(Monetary.getCurrency("USD")), Money.zero(Monetary.getCurrency("USD")), null, 0);
        SavingsItem result = editSavingsItem(savingsItem, false);
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

    private SavingsItem editSavingsItem(SavingsItem savingsItem, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SavingsEditDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle((edit ? "Edit" : "Create New") + " Savings goal");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(super.getPrimaryStage());
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            SavingsEditController controller = loader.getController();
            controller.setSavingsItem(savingsItem);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isOkPressed()) {
                return controller.getSavingsItem();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
