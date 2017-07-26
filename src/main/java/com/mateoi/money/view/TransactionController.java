package com.mateoi.money.view;

import com.mateoi.money.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by mateo on 30/06/2017.
 */
public class TransactionController {
    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, Money> amountColumn;

    @FXML
    private TableColumn<Transaction, Account> accountColumn;

    @FXML
    private TableColumn<Transaction, BudgetItem> typeColumn;

    private Stage primaryStage;

    @FXML
    private void initialize() {
        table.setItems(MainState.getInstance().getTransactions());
        table.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                onEditTransaction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().select(null);
            }
        });
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (table.getSelectionModel().getSelectedItem() == null) {
                    onAddTransaction();
                } else {
                    table.getSelectionModel().select(null);
                }
            }
        });

        dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        accountColumn.setCellValueFactory(param -> param.getValue().accountProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().budgetTypeProperty());

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MoneyStringConverter(() -> table.getSelectionModel().getSelectedItem().getAmount())));
        dateColumn.setCellFactory(c -> new DatePickerTableCell<>());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        accountColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<Transaction, Account> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getAccounts());
            cell.setConverter(new StringConverter<Account>() {
                @Override
                public String toString(Account account) {
                    return account.getName();
                }

                @Override
                public Account fromString(String string) {
                    return MainState.getInstance().getAccounts().stream().
                            filter(a -> a.getName().equals(string)).
                            findFirst().
                            orElse(MainState.UNKNOWN_ACCOUNT);
                }
            });
            return cell;
        });
        typeColumn.setCellFactory(c -> {
            ChoiceBoxTableCell<Transaction, BudgetItem> cell = new ChoiceBoxTableCell<>(MainState.getInstance().getBudgetItems());
            cell.setConverter(new StringConverter<BudgetItem>() {
                @Override
                public String toString(BudgetItem budget) {
                    return budget.getName();
                }

                @Override
                public BudgetItem fromString(String string) {
                    return MainState.getInstance().getBudgetItems().stream().
                            filter(b -> b.getName().equals(string)).
                            findFirst().
                            orElse(MainState.UNKNOWN_BUDGET);
                }
            });
            return cell;
        });
    }

    @FXML
    private void onEditTransaction() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            editTransaction(transaction, true);
        }
    }

    @FXML
    private void onRemoveTransaction() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            MainState.getInstance().getTransactions().remove(transaction);
            transaction.getAccount().getTransactions().remove(transaction);
            transaction.getBudgetType().getTransactions().remove(transaction);
        }

    }

    @FXML
    private void onAddTransaction() {
        int newId = MainState.getInstance().getLastTransaction() + 1;
        Transaction newTransaction = new Transaction(newId, LocalDate.now(), "", Money.zero(Monetary.getCurrency("USD")), null, null);
        Transaction result = editTransaction(newTransaction, false);
        if (result != null) {
            MainState.getInstance().setLastTransaction(newId);
            MainState.getInstance().getTransactions().add(result);
        }
    }

    private Transaction editTransaction(Transaction transaction, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/TransactionEditDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle((edit ? "Edit" : "Create New") + " Transaction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            TransactionEditController controller = loader.getController();
            controller.setTransaction(transaction);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkPressed()) {
                return controller.getTransaction();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
