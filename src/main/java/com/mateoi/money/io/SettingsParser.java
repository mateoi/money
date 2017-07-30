package com.mateoi.money.io;

import com.mateoi.money.model.Settings;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton class that parses and loads settings from strings.
 */
public class SettingsParser {
    private static SettingsParser ourInstance = new SettingsParser();

    /**
     * Get the singleton instance of this class
     *
     * @return
     */
    public static SettingsParser getInstance() {
        return ourInstance;
    }

    // Private to prevent instantiation
    private SettingsParser() {
        //Empty
    }

    /**
     * Parses the lines that encode settings and applies them
     *
     * @param lines
     */
    public void parseIntoSettings(List<String> lines) {
        Path currentFile = cleanLines(lines, FilePrefixes.SETTINGS_FILE_PREFIX).filter(s -> !s.equals("null")).map(Paths::get).findFirst().orElse(null);
        List<Path> recentFiles = cleanLines(lines, FilePrefixes.SETTINGS_RECENT_FILE_PREFIX).filter(s -> !s.equals("null")).map(Paths::get).collect(Collectors.toList());
        CurrencyUnit currency = cleanLines(lines, FilePrefixes.SETTINGS_DEFAULT_CURRENCY_PREFIX).map(Monetary::getCurrency).findFirst().orElse(Monetary.getCurrency("USD"));
        boolean color = cleanLines(lines, FilePrefixes.SETTINGS_COLOR_PREFIX).map(Boolean::parseBoolean).findFirst().orElse(true);
        int maxFiles = cleanLines(lines, FilePrefixes.SETTINGS_MAX_RECENT_FILES).map(Integer::parseInt).map(Math::abs).findFirst().orElse(10);

        Settings settings = Settings.getInstance();
        settings.setCurrentFile(currentFile);
        settings.getRecentFiles().addAll(recentFiles);
        settings.setDefaultCurrency(currency);
        settings.setColorCode(color);
        settings.setMaxRecentFiles(maxFiles);

        if (currentFile != null) {
            try {
                FileIO.getInstance().load(currentFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a stream of the given lines consisting only of those lines that start with the given prefix,
     * but with the prefix removed.
     *
     * @param lines
     * @param prefix
     * @return
     */
    private Stream<String> cleanLines(List<String> lines, String prefix) {
        return lines.stream().filter(s -> s.startsWith(prefix)).map(s -> s.substring(prefix.length()));
    }

}
