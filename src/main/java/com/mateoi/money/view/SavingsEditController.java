package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 26/07/2017.
 */
public class SavingsEditController extends EditDialogController<SavingsItem> {
    @FXML
    private TextField nameField;

    @FXML
    private TextField goalField;

    @FXML
    private TextField currentAmountField;

    @FXML
    private ChoiceBox<Account> accountChoiceBox;

    @FXML
    private TextField allocationField;

    private Money goal;

    private Money currentAmount;

    private float allocation;

    @FXML
    private void initialize() {
        goalField.focusedProperty().addListener((a, b, nowFocused) -> {
            if (!nowFocused) {
                goal = sanitizeAmount(goalField, goal);
            }
        });
        goalField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                goal = sanitizeAmount(goalField, goal);
                goalField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                goalField.setText(MoneyStringConverter.formatMoney(goal));
                goalField.cancelEdit();
            }
        });
        currentAmountField.focusedProperty().addListener((a, b, nowFocused) -> {
            if (!nowFocused) {
                currentAmount = sanitizeAmount(currentAmountField, currentAmount);
            }
        });
        currentAmountField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                currentAmount = sanitizeAmount(currentAmountField, currentAmount);
                currentAmountField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                currentAmountField.setText(MoneyStringConverter.formatMoney(currentAmount));
                currentAmountField.cancelEdit();
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

        allocationField.focusedProperty().addListener((a, b, focusedNow) -> {
            if (!focusedNow) {
                sanitizeAllocation();
            }
        });
        allocationField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                sanitizeAllocation();
                allocationField.cancelEdit();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                allocationField.setText(PercentageStringConverter.formatNumber(allocation));
                allocationField.cancelEdit();
            }
        });


    }

    @FXML
    private void onOK() {
        if (validateFields()) {
            setOkPressed(true);
            commitSavingsItem();
            getDialogStage().close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        getDialogStage().close();
    }

    private void commitSavingsItem() {
        item.setName(nameField.getText());
        item.setGoal(goal);
        item.setCurrentAmount(currentAmount);
        item.setAccount(accountChoiceBox.getValue());
        item.setAllocation(allocation);
    }


    private boolean validateFields() {
        boolean nameOK = !nameField.getText().equals("");
        boolean goalOK = goal != null;
        boolean currentAmountOK = currentAmount != null;
        boolean accountOK = accountChoiceBox.getValue() != null;
        return nameOK && goalOK && currentAmountOK && accountOK;
    }


    private Money sanitizeAmount(TextField field, Money amount) {
        Money newAmount = new MoneyStringConverter(() -> amount).fromString(field.getText());
        field.setText(MoneyStringConverter.formatMoney(newAmount));
        return newAmount;
    }

    private void sanitizeAllocation() {
        String string = allocationField.getText();
        PercentageStringConverter converter = new PercentageStringConverter(() -> allocation);
        float newAllocation = converter.fromString(string).floatValue();
        allocationField.setText(converter.toString(newAllocation));
        this.allocation = newAllocation;
    }

    public void setItem(SavingsItem savingsItem) {
        this.item = savingsItem;
        this.goal = savingsItem.getGoal();
        this.currentAmount = savingsItem.getCurrentAmount();
        this.allocation = savingsItem.getAllocation();
        nameField.setText(savingsItem.getName());
        goalField.setText(MoneyStringConverter.formatMoney(goal));
        currentAmountField.setText(MoneyStringConverter.formatMoney(currentAmount));
        accountChoiceBox.getSelectionModel().select(savingsItem.getAccount());
        allocationField.setText(PercentageStringConverter.formatNumber(savingsItem.getAllocation()));
    }
}
