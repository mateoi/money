package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.time.LocalDate;

/**
 * Created by mateo on 25/07/2017.
 */
public class TransactionEditController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;

    @FXML
    private ChoiceBox<BudgetItem> typeChoiceBox;

    @FXML
    private ChoiceBox<Account> accountChoiceBox;

    private Stage dialogStage;

    private Transaction transaction;

    private Money amount;

    private boolean okPressed = false;

    @FXML
    private void initialize() {
        amountField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                validateAmount();
            }
        });
        amountField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                validateAmount();
                amountField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                amountField.setText(MoneyStringConverter.formatMoney(amount));
                amountField.cancelEdit();
            }
        });

        typeChoiceBox.setItems(MainState.getInstance().getBudgetItems());
        typeChoiceBox.setConverter(new StringConverter<BudgetItem>() {
            @Override
            public String toString(BudgetItem object) {
                return object.getName();
            }

            @Override
            public BudgetItem fromString(String string) {
                return null;
            }
        });

        accountChoiceBox.setItems(MainState.getInstance().getAccounts());
        accountChoiceBox.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                return object.getName();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });

    }

    @FXML
    private void onOK() {
        if (validateFields()) {
            okPressed = true;
            commitTransaction();
            dialogStage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    private void commitTransaction() {
        transaction.setDate(datePicker.getValue());
        transaction.setDescription(descriptionField.getText());
        transaction.setAmount(amount);
        transaction.setBudgetType(typeChoiceBox.getValue());
        transaction.setAccount(accountChoiceBox.getValue());
    }

    private void validateAmount() {
        Money newAmount = new MoneyStringConverter(() -> amount).fromString(amountField.getText());
        amountField.setText(MoneyStringConverter.formatMoney(newAmount));
        amount = newAmount;
    }

    private boolean validateFields() {
        boolean dateOK = datePicker.getValue() != null && !datePicker.getValue().isAfter(LocalDate.now());
        boolean descriptionOK = descriptionField.getText() != null && !descriptionField.getText().equals("");
        boolean amountOK = amount != null;
        boolean typeOK = typeChoiceBox.getValue() != null;
        boolean accountOK = accountChoiceBox.getValue() != null;
        return dateOK && descriptionOK && amountOK && typeOK && accountOK;
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.amount = transaction.getAmount();
        datePicker.setValue(transaction.getDate());
        descriptionField.setText(transaction.getDescription());
        amountField.setText(MoneyStringConverter.formatMoney(transaction.getAmount()));
        typeChoiceBox.getSelectionModel().select(transaction.getBudgetType());
        accountChoiceBox.getSelectionModel().select(transaction.getAccount());
    }

    public boolean isOkPressed() {
        return okPressed;
    }
}
