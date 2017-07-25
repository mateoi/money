package com.mateoi.money.view;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;

/**
 * Created by mateo on 20/07/2017.
 */
public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {
    private final LocalDateStringConverter formatter;

    private final DatePicker datePicker;

    public DatePickerTableCell() {
        formatter = new LocalDateStringConverter();
        datePicker = new DatePicker();

        // Commit edit on Enter and cancel on Escape.
        // Note that the default behavior consumes key events, so we must
        // register this as an event filter to capture it.
        // Consequently, with Enter, the datePicker's value won't yet have been updated,
        // so commit will sent the wrong value. So we must update it ourselves from the
        // editor's text value.

        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                commitEdit(datePicker.getValue());
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        // Modify default mouse behavior on date picker:
        // Don't hide popup on single click, just set date
        // On double-click, hide popup and commit edit for editor
        // Must consume event to prevent default hiding behavior, so
        // must update date picker value ourselves.

        // Modify key behavior so that enter on a selected cell commits the edit
        // on that cell's date.

        datePicker.setDayCellFactory(picker ->

        {
            DateCell cell = new DateCell();
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                datePicker.setValue(cell.getItem());
                if (event.getClickCount() == 2) {
                    datePicker.hide();
                    commitEdit(cell.getItem());
                }
                event.consume();
            });
            cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    commitEdit(datePicker.getValue());
                }
            });
            return cell;
        });

        contentDisplayProperty().
                bind(Bindings.when(editingProperty()).
                        then(ContentDisplay.GRAPHIC_ONLY).
                        otherwise(ContentDisplay.TEXT_ONLY));
    }

    @Override
    public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        if (empty || date == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(formatter.toString(date));
            setGraphic(datePicker);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            datePicker.setValue(LocalDate.now());
        }
    }

}
