package com.mateoi.money.io;

import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Singleton class that handles writing and reading files.
 */
public class FileIO {
    private final static FileIO instance = new FileIO();

    private FileIO() {
    }

    /**
     * Get the singleton instance of this class
     *
     * @return
     */
    public static FileIO getInstance() {
        return instance;
    }


    /**
     * Saves the current program state to the specified path
     *
     * @param path The path to save the current state to
     * @throws IOException If there is a problem writing to file
     */
    public void save(Path path) throws IOException {
        String lines = MainState.getInstance().toString();
        Files.write(path, lines.getBytes());
    }

    /**
     * Saves the current program settings to the specified path
     *
     * @param path The path to save the current settings to
     * @throws IOException If there is a problem writing to file
     */
    public void saveSettings(Path path) throws IOException {
        String lines = Settings.getInstance().toString();
        Files.write(path, lines.getBytes());
    }


    /**
     * Reads the specified path and parses a program state, then loads it into {@link MainState}.
     *
     * @param path The path to load
     * @throws IOException If there is a problem reading the file
     */
    public void load(Path path) throws IOException {
        if (path != null) {
            List<String> lines = Files.readAllLines(path);
            MainStateParser.getInstance().parseIntoMainState(lines);
        }
    }


    /**
     * Reads the specified path and parses program settings, then applies them.
     *
     * @param path The path to load
     * @throws IOException If there is a problem reading the file
     */
    public void loadSettings(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        SettingsParser.getInstance().parseIntoSettings(lines);
    }

}
