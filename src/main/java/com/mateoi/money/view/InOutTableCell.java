package com.mateoi.money.view;

import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;

/**
 * Created by mateo on 20/07/2017.
 */
public class InOutTableCell<S> extends TableCell<S, Boolean> {
    public InOutTableCell() {
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!isEmpty() && event.getClickCount() == 2) {
                if (getItem() == null) {
                    commitEdit(true);
                } else {
                    commitEdit(!getItem());
                }
            }
        });
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else if (item) {
            setText("In");
        } else {
            setText("Out");
        }
    }
}
