package com.mateoi.money.view;

import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.MoneyStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.javamoney.moneta.Money;

/**
 * Created by mateo on 26/07/2017.
 */
public class BudgetEditController extends EditDialogController<BudgetItem> {
    private ToggleGroup inOutGroup = new ToggleGroup();

    @FXML
    private RadioButton inRadioButton;

    @FXML
    private RadioButton outRadioButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField amountField;

    @FXML
    private CheckBox essentialCheckBox;

    private Money amount;


    @FXML
    private void initialize() {
        inRadioButton.setToggleGroup(inOutGroup);
        outRadioButton.setToggleGroup(inOutGroup);
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
    private void onOK() {
        if (validateFields()) {
            super.setOkPressed(true);
            commitBudgetItem();
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

    private void commitBudgetItem() {
        item.setIn(inOutGroup.getSelectedToggle().equals(inRadioButton));
        item.setName(nameField.getText());
        item.setAmount(amount);
        item.setEssential(essentialCheckBox.isSelected());
    }

    private void validateAmount() {
        Money newAmount = new MoneyStringConverter(() -> amount).fromString(amountField.getText());
        amountField.setText(MoneyStringConverter.formatMoney(newAmount));
        amount = newAmount;
    }

    private boolean validateFields() {
        boolean nameOK = !nameField.getText().equals("");
        boolean amountOK = amount != null;
        return nameOK && amountOK;
    }


    public void setItem(BudgetItem budgetItem) {
        this.item = budgetItem;
        this.amount = budgetItem.getAmount();

        inOutGroup.selectToggle(budgetItem.isIn() ? inRadioButton : outRadioButton);
        nameField.setText(budgetItem.getName());
        amountField.setText(MoneyStringConverter.formatMoney(amount));
        this.essentialCheckBox.setSelected(budgetItem.isEssential());
    }
}
