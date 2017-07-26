package com.mateoi.money.io;

import com.mateoi.money.model.Settings;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mateo on 26/07/2017.
 */
public class SettingsParser {
    private static SettingsParser ourInstance = new SettingsParser();

    public static SettingsParser getInstance() {
        return ourInstance;
    }

    private SettingsParser() {
    }

    public void parseIntoSettings(List<String> lines) {
        Path currentFile = cleanLines(lines, FilePrefixes.SETTINGS_FILE_PREFIX).map(Paths::get).findFirst().orElse(null);
        List<Path> recentFiles = cleanLines(lines, FilePrefixes.SETTINGS_RECENT_FILE_PREFIX).map(Paths::get).collect(Collectors.toList());
        CurrencyUnit currency = cleanLines(lines, FilePrefixes.SETTINGS_DEFAULT_CURRENCY_PREFIX).map(Monetary::getCurrency).findFirst().orElse(Monetary.getCurrency("USD"));
        boolean color = cleanLines(lines, FilePrefixes.SETTINGS_COLOR_PREFIX).map(Boolean::parseBoolean).findFirst().orElse(false);
        int maxFiles = cleanLines(lines, FilePrefixes.SETTINGS_MAX_RECENT_FILES).map(Integer::parseInt).map(Math::abs).findFirst().orElse(10);

        Settings settings = Settings.getInstance();
        settings.setCurrentFile(currentFile);
        settings.getRecentFiles().addAll(recentFiles);
        settings.setDefaultCurrency(currency);
        settings.setColorCode(color);
        settings.setMaxRecentFiles(maxFiles);
    }

    private Stream<String> cleanLines(List<String> lines, String prefix) {
        return lines.stream().filter(s -> s.startsWith(prefix)).map(s -> s.substring(prefix.length()));
    }

}
