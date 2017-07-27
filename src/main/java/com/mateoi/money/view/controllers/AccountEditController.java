package com.mateoi.money.view.controllers;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MoneyStringConverter;
import com.mateoi.money.model.PercentageStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 26/07/2017.
 */
public class AccountEditController extends EditDialogController<Account> {
    @FXML
    private TextField nameField;

    @FXML
    private TextField startingAmountField;

    @FXML
    private TextField warningField;

    @FXML
    private TextField interestField;

    private Money startingAmount;

    private Money warning;

    private float interest;

    @FXML
    private void initialize() {
        startingAmountField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                startingAmount = validateAmount(startingAmount, startingAmountField);
            }
        });
        startingAmountField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                startingAmount = validateAmount(startingAmount, startingAmountField);
                startingAmountField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                startingAmountField.setText(MoneyStringConverter.formatMoney(startingAmount));
                startingAmountField.cancelEdit();
            }
        });

        warningField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                warning = validateAmount(warning, warningField);
            }
        });
        warningField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                warning = validateAmount(warning, warningField);
                warningField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                warningField.setText(MoneyStringConverter.formatMoney(warning));
                warningField.cancelEdit();
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
            setOkPressed(true);
            commitAccount();
            super.getDialogStage().close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        super.getDialogStage().close();
    }

    private Money validateAmount(Money oldMoney, TextField field) {
        Money newAmount = new MoneyStringConverter(() -> oldMoney).fromString(field.getText());
        field.setText(MoneyStringConverter.formatMoney(newAmount));
        return newAmount;
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
        boolean warningOK = warning != null;
        return nameOK && amountOK && warningOK;
    }

    private void commitAccount() {
        item.setName(nameField.getText());
        item.setStartingAmount(startingAmount);
        item.setWarningAmount(warning);
        item.setAnnualInterest(interest);
    }

    public void setItem(Account account) {
        this.item = account;
        this.startingAmount = account.getStartingAmount();
        this.interest = account.getAnnualInterest();
        this.warning = account.getWarningAmount();
        nameField.setText(account.getName());
        startingAmountField.setText(MoneyStringConverter.formatMoney(account.getStartingAmount()));
        warningField.setText(MoneyStringConverter.formatMoney(warning));
        interestField.setText(PercentageStringConverter.formatNumber(account.getAnnualInterest()));
    }
}
