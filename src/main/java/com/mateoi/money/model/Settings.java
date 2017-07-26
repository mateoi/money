package com.mateoi.money.model;

import com.mateoi.money.io.FilePrefixes;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.nio.file.Path;

/**
 * Created by mateo on 26/07/2017.
 */
public class Settings {
    private final static Settings ourInstance = new Settings();

    private final ObjectProperty<Path> currentFile = new SimpleObjectProperty<>();

    private final ObservableList<Path> recentFiles = FXCollections.observableArrayList();

    private final ObjectProperty<CurrencyUnit> defaultCurrency = new SimpleObjectProperty<>(Monetary.getCurrency("USD"));

    private final ObservableList<CurrencyUnit> allCurrencies = FXCollections.observableArrayList(Monetary.getCurrencies());

    private final BooleanProperty colorCode = new SimpleBooleanProperty(false);

    private final IntegerProperty maxRecentFiles = new SimpleIntegerProperty(10);

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FilePrefixes.SETTINGS_FILE_PREFIX);
        sb.append(currentFile.get());
        sb.append("\n");

        for (Path path : recentFiles.subList(0, maxRecentFiles.get())) {
            sb.append(FilePrefixes.SETTINGS_RECENT_FILE_PREFIX);
            sb.append(path);
            sb.append("\n");
        }

        sb.append(FilePrefixes.SETTINGS_DEFAULT_CURRENCY_PREFIX);
        sb.append(defaultCurrency.get().getCurrencyCode());
        sb.append("\n");

        sb.append(FilePrefixes.SETTINGS_COLOR_PREFIX);
        sb.append(colorCode.get());
        sb.append("\n");

        sb.append(FilePrefixes.SETTINGS_MAX_RECENT_FILES);
        sb.append(maxRecentFiles.get());
        sb.append("\n");

        return sb.toString();
    }

    public Path getCurrentFile() {
        return currentFile.get();
    }

    public ObjectProperty<Path> currentFileProperty() {
        return currentFile;
    }

    public void setCurrentFile(Path currentFile) {
        this.currentFile.set(currentFile);
    }

    public ObservableList<Path> getRecentFiles() {
        return recentFiles;
    }

    public CurrencyUnit getDefaultCurrency() {
        return defaultCurrency.get();
    }

    public ObjectProperty<CurrencyUnit> defaultCurrencyProperty() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(CurrencyUnit defaultCurrency) {
        this.defaultCurrency.set(defaultCurrency);
    }

    public boolean isColorCode() {
        return colorCode.get();
    }

    public BooleanProperty colorCodeProperty() {
        return colorCode;
    }

    public void setColorCode(boolean colorCode) {
        this.colorCode.set(colorCode);
    }

    public ObservableList<CurrencyUnit> getAllCurrencies() {
        return allCurrencies;
    }

    public int getMaxRecentFiles() {
        return maxRecentFiles.get();
    }

    public IntegerProperty maxRecentFilesProperty() {
        return maxRecentFiles;
    }

    public void setMaxRecentFiles(int maxRecentFiles) {
        this.maxRecentFiles.set(maxRecentFiles);
    }
}
