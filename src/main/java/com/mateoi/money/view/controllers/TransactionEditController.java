package com.mateoi.money.view.controllers;

import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.SubTransaction;
import com.mateoi.money.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @FXML
    private VBox subTransactionBox;

    private ObservableList<SubTransactionController> subTransactionControllers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
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
        subTransactionControllers.addListener((ListChangeListener<? super SubTransactionController>) c -> {
            while (c.next()) {
                if (subTransactionControllers.size() == 1) {
                    subTransactionControllers.get(0).setButtonVisible(false);
                } else {
                    for (SubTransactionController subTransactionController : subTransactionControllers) {
                        subTransactionController.setButtonVisible(true);
                    }
                }
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

    @FXML
    private void onAdd() {
        int newID = MainState.getInstance().getLastSubTransaction() + 1;
        MainState.getInstance().setLastSubTransaction(newID);
        SubTransaction subTransaction = new SubTransaction(newID, null, Money.zero(this.item.getCurrency()), this.item);
        addSubTransaction(subTransaction);
        getDialogStage().getScene().getWindow().sizeToScene();
    }

    private void commitTransaction() {
        item.setDate(datePicker.getValue());
        item.setDescription(descriptionField.getText());
        item.setBudgetType(typeChoiceBox.getValue());
        List<SubTransaction> sts = subTransactionControllers.stream().map(SubTransactionController::getSubTransaction).collect(Collectors.toList());
        for (SubTransaction subTransaction : item.getSubTransactions()) {
            if (!sts.contains(subTransaction)) {
                subTransaction.getAccount().getSubTransactions().remove(subTransaction);
            }
        }
        MainState.getInstance().getSubTransactions().removeIf(st -> st.getTransaction().equals(item));
        MainState.getInstance().getSubTransactions().addAll(sts);
        item.getSubTransactions().setAll(sts);
    }

    private boolean validateFields() {
        boolean dateOK = datePicker.getValue() != null && !datePicker.getValue().isAfter(LocalDate.now());
        boolean descriptionOK = descriptionField.getText() != null && !descriptionField.getText().equals("");
        boolean typeOK = typeChoiceBox.getValue() != null;
        boolean subTxs = validateSubTransactions();
        return dateOK && descriptionOK && typeOK && subTxs;
    }

    private boolean validateSubTransactions() {
        return subTransactionControllers.stream().
                map(SubTransactionController::getSubTransaction).
                allMatch(this::validateSubTransaction);
    }

    private boolean validateSubTransaction(SubTransaction st) {
        boolean accountOK = st.getAccount() != null;
        boolean amountOK = st.getAmount() != null;
        return accountOK && amountOK;
    }

    private void addSubTransaction(SubTransaction subTransaction) {
        SubTransactionController controller = SubTransactionController.createNodeController();
        if (controller != null) {
            controller.setSubTransaction(subTransaction);
            controller.setEditor(this);
            subTransactionBox.getChildren().add(controller.getNode());
            subTransactionControllers.add(controller);
        }
    }

    void removeSubTransactionNode(SubTransactionController controller) {
        subTransactionControllers.remove(controller);
        subTransactionBox.getChildren().remove(controller.getNode());
        getDialogStage().getScene().getWindow().sizeToScene();
    }

    public void setItem(Transaction transaction) {
        this.item = transaction;
        datePicker.setValue(transaction.getDate());
        descriptionField.setText(transaction.getDescription());
        typeChoiceBox.getSelectionModel().select(transaction.getBudgetType());
        for (SubTransaction subTransaction : transaction.getSubTransactions()) {
            addSubTransaction(subTransaction);
        }
    }
}
