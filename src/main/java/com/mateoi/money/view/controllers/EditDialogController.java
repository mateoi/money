package com.mateoi.money.view.controllers;

import javafx.stage.Stage;

/**
 * Created by mateo on 26/07/2017.
 */
public abstract class EditDialogController<T> {
    private Stage dialogStage;

    T item;

    private boolean okPressed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public void setOkPressed(boolean okPressed) {
        this.okPressed = okPressed;
    }

    public T getItem() {
        return item;
    }

    public abstract void setItem(T item);
}
