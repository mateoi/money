package com.mateoi.money.view.controllers;

import com.mateoi.money.model.Account;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.MoneyStringConverter;
import com.mateoi.money.model.SubTransaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.io.IOException;

/**
 * Created by mateo on 09/09/2017.
 */
public class SubTransactionController {

    @FXML
    private ChoiceBox<Account> accountChoiceBox;

    @FXML
    private TextField amountField;

    @FXML
    private Button removeButton;

    private SubTransaction subTransaction;

    private TransactionEditController editor;

    private Money amount;

    private Node node;


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
    private void onRemove() {
        editor.removeSubTransactionNode(this);
    }

    private void validateAmount() {
        Money newAmount = new MoneyStringConverter(() -> amount).fromString(amountField.getText());
        amountField.setText(MoneyStringConverter.formatMoney(newAmount));
        amount = newAmount;
    }

    public static SubTransactionController createNodeController() {
        try {
            FXMLLoader loader = new FXMLLoader(SubTransactionController.class.getResource("/SubTransactionNode.fxml"));
            Node node = (Node) loader.load();
            SubTransactionController controller = loader.getController();
            controller.setNode(node);
            return controller;
        } catch (IOException e) {
            return null;
        }
    }

    public void setSubTransaction(SubTransaction subTransaction) {
        this.subTransaction = subTransaction;
        this.accountChoiceBox.getSelectionModel().select(subTransaction.getAccount());
        this.amount = subTransaction.getAmount();
        amountField.setText(MoneyStringConverter.formatMoney(amount));
    }

    public SubTransaction getSubTransaction() {
        subTransaction.setAccount(accountChoiceBox.getValue());
        subTransaction.setAmount(amount);
        return subTransaction;
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

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
