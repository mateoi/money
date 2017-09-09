package com.mateoi.money.view.controllers;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.time.LocalDate;

/**
 * Created by mateo on 25/07/2017.
 */
public class TransactionEditController extends EditDialogController<Transaction> {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private ChoiceBox<BudgetItem> typeChoiceBox;

    private Money amount;

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
            setOkPressed(true);
            commitTransaction();
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

    @FXML
    private void onPlusOne() {
        LocalDate date = datePicker.getValue();
        date = date == null ? LocalDate.now() : date;
        datePicker.setValue(date.plusDays(1));
    }

    @FXML
    private void onMinusOne() {
        LocalDate date = datePicker.getValue();
        date = date == null ? LocalDate.now() : date;
        datePicker.setValue(date.minusDays(1));
    }

    private void commitTransaction() {
        item.setDate(datePicker.getValue());
        item.setDescription(descriptionField.getText());
        item.setAmount(amount);
        item.setBudgetType(typeChoiceBox.getValue());
        item.setAccount(accountChoiceBox.getValue());
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

    public void setItem(Transaction transaction) {
        this.item = transaction;
        this.amount = transaction.getAmount();
        datePicker.setValue(transaction.getDate());
        descriptionField.setText(transaction.getDescription());
        amountField.setText(MoneyStringConverter.formatMoney(transaction.getAmount()));
        typeChoiceBox.getSelectionModel().select(transaction.getBudgetType());
        accountChoiceBox.getSelectionModel().select(transaction.getAccount());
    }
}
