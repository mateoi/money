package com.mateoi.money.io;

import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by mateo on 04/07/2017.
 */
public class FileIO {
    private final static FileIO instance = new FileIO();

    private FileIO() {
    }

    public static FileIO getInstance() {
        return instance;
    }

    public void save(String location) throws IOException {
        Path path = Paths.get(location);
        save(path);
    }

    public void save(Path path) throws IOException {
        String lines = MainState.getInstance().toString();
        Files.write(path, lines.getBytes());
    }

    public void saveSettings(String location) throws IOException {
        Path path = Paths.get(location);
        saveSettings(path);
    }

    public void saveSettings(Path path) throws IOException {
        String lines = Settings.getInstance().toString();
        Files.write(path, lines.getBytes());
    }

    public void load(String location) throws IOException {
        Path path = Paths.get(location);
        load(path);
    }

    public void load(Path path) throws IOException {
        if (path != null) {
            List<String> lines = Files.readAllLines(path);
            MainStateParser.getInstance().parseIntoMainState(lines);
        }
    }

    public void loadSettings(String location) throws IOException {
        Path path = Paths.get(location);
        loadSettings(path);
    }

    public void loadSettings(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        SettingsParser.getInstance().parseIntoSettings(lines);
    }

}
