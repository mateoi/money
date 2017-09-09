package com.mateoi.money.view.controllers;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MoneyStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 09/09/2017.
 */
public class SubTransactionNode {

    @FXML
    private ChoiceBox<Account> accountChoiceBox;

    @FXML
    private TextField amountField;

    @FXML
    private Button removeButton;

    private TransactionEditController editor;

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

    }

    @FXML
    private void onRemove() {
        editor.removeSubTransactionNode(this);
    }

    private void validateAmount() {
        Money newAmount = new MoneyStringConverter(() -> amount).fromString(amountField.getText());
        amountField.setText(MoneyStringConverter.formatMoney(newAmount));
        amount = newAmount;
    }

    public void setButtonVisible(boolean visible) {
        this.removeButton.setVisible(visible);
    }

    public void setEditor(TransactionEditController editor) {
        this.editor = editor;
    }

    public Account getAccount() {
        return accountChoiceBox.getValue();
    }

    public void setAccount(Account account) {
        this.accountChoiceBox.getSelectionModel().select(account);
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
        amountField.setText(MoneyStringConverter.formatMoney(amount));
    }
}
