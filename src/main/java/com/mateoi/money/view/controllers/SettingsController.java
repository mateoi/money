package com.mateoi.money.view.controllers;

import com.mateoi.money.model.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.money.CurrencyUnit;

/**
 * Created by mateo on 27/07/2017.
 */
public class SettingsController {

    @FXML
    private AnchorPane spinnerContainer;

    private Spinner<Integer> maxRecentFilesSpinner = new Spinner<>(1, 50, 10, 1);

    @FXML
    private ChoiceBox<CurrencyUnit> currencyChoiceBox;

    @FXML
    private CheckBox colorCheckBox;

    private Stage dialogStage;


    @FXML
    private void initialize() {
        spinnerContainer.getChildren().add(maxRecentFilesSpinner);
        AnchorPane.setTopAnchor(maxRecentFilesSpinner, 0.);
        AnchorPane.setBottomAnchor(maxRecentFilesSpinner, 0.);
        AnchorPane.setLeftAnchor(maxRecentFilesSpinner, 0.);
        AnchorPane.setRightAnchor(maxRecentFilesSpinner, 0.);
        maxRecentFilesSpinner.setEditable(true);

        maxRecentFilesSpinner.getValueFactory().setValue(Settings.getInstance().getMaxRecentFiles());
        currencyChoiceBox.setItems(Settings.getInstance().getAllCurrencies());
        currencyChoiceBox.getSelectionModel().select(Settings.getInstance().getDefaultCurrency());
        colorCheckBox.setSelected(Settings.getInstance().isColorCode());
    }

    @FXML
    private void onOK() {
        onApply();
        dialogStage.close();
    }

    @FXML
    private void onApply() {
        Settings.getInstance().setMaxRecentFiles(maxRecentFilesSpinner.getValue());
        Settings.getInstance().setDefaultCurrency(currencyChoiceBox.getValue());
        Settings.getInstance().setColorCode(colorCheckBox.isSelected());
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
