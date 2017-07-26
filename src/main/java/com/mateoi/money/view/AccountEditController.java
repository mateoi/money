package com.mateoi.money.view;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MoneyStringConverter;
import com.mateoi.money.model.PercentageStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 26/07/2017.
 */
public class AccountEditController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField startingAmountField;

    @FXML
    private TextField interestField;

    private Stage dialogStage;

    private Account account;

    private Money startingAmount;

    private float interest;

    private boolean okPressed = false;

    @FXML
    private void initialize() {
        startingAmountField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                validateAmount();
            }
        });
        startingAmountField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                validateAmount();
                startingAmountField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                startingAmountField.setText(MoneyStringConverter.formatMoney(startingAmount));
                startingAmountField.cancelEdit();
            }
        });

        interestField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                validateInterest();
            }
        });
        interestField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                validateInterest();
                interestField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                interestField.setText(PercentageStringConverter.formatNumber(interest));
                interestField.cancelEdit();
            }
        });
    }

    @FXML
    private void onOK() {
        if (validateFields()) {
            okPressed = true;
            commitAccount();
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

    private void validateAmount() {
        Money newAmount = new MoneyStringConverter(() -> startingAmount).fromString(startingAmountField.getText());
        startingAmountField.setText(MoneyStringConverter.formatMoney(newAmount));
        startingAmount = newAmount;
    }

    private void validateInterest() {
        String string = interestField.getText();
        PercentageStringConverter converter = new PercentageStringConverter(() -> interest);
        float newInterest = converter.fromString(string).floatValue();
        interestField.setText(converter.toString(newInterest));
        this.interest = newInterest;
    }

    private boolean validateFields() {
        boolean nameOK = !nameField.getText().equals("");
        boolean amountOK = startingAmount != null;
        return nameOK && amountOK;
    }

    private void commitAccount() {
        account.setName(nameField.getText());
        account.setStartingAmount(startingAmount);
        account.setAnnualInterest(interest);
    }

    public Account getAccount() {
        return account;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAccount(Account account) {
        this.account = account;
        this.startingAmount = account.getStartingAmount();
        this.interest = account.getAnnualInterest();
        nameField.setText(account.getName());
        startingAmountField.setText(MoneyStringConverter.formatMoney(account.getStartingAmount()));
        interestField.setText(PercentageStringConverter.formatNumber(account.getAnnualInterest()));
    }
}
