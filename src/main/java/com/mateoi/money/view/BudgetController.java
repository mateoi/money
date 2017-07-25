package com.mateoi.money.view;

import com.mateoi.money.model.BudgetItem;
import com.mateoi.money.model.Budgets;
import com.mateoi.money.model.MainState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.javamoney.moneta.Money;


/**
 * Created by mateo on 30/06/2017.
 */
public class BudgetController {
    @FXML
    private TableView<BudgetItem> table;

    @FXML
    private TableColumn<BudgetItem, Boolean> inColumn;

    @FXML
    private TableColumn<BudgetItem, String> nameColumn;

    @FXML
    private TableColumn<BudgetItem, Money> amountColumn;

    @FXML
    private TableColumn<BudgetItem, Money> remainingColumn;

    @FXML
    private TableColumn<BudgetItem, Boolean> essentialColumn;

    @FXML
    private Label totalInLabel;

    @FXML
    private Label totalOutLabel;

    @FXML
    private Label totalSavingsLabel;

    @FXML
    private Label totalEssentialsLabel;

    @FXML
    private Label totalNonEssentialsLabel;


    @FXML

    private void initialize() {
        table.setItems(MainState.getInstance().getBudgetItems());
        inColumn.setCellValueFactory(param -> param.getValue().inProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        remainingColumn.setCellValueFactory(param -> param.getValue().remainingProperty());
        essentialColumn.setCellValueFactory(param -> param.getValue().essentialProperty());

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        inColumn.setCellFactory(c -> new InOutTableCell<>());
        essentialColumn.setCellFactory(c -> new CheckBoxTableCell<>());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getAmount())));
        remainingColumn.setCellFactory(c -> new TableCell<BudgetItem, Money>() {
            @Override
            protected void updateItem(Money item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(MoneyStringConverter.formatMoney(item));
                }
            }
        });

        totalInLabel.textProperty().bind(Budgets.getInstance().totalInProperty().asString());
        totalOutLabel.textProperty().bind(Budgets.getInstance().totalOutProperty().asString());
        totalSavingsLabel.textProperty().bind(Budgets.getInstance().totalToSavingsProperty().asString());
        totalEssentialsLabel.textProperty().bind(Budgets.getInstance().totalEssentialsProperty().asString());
        totalNonEssentialsLabel.textProperty().bind(Budgets.getInstance().totalExtrasProperty().asString());

    }

}
