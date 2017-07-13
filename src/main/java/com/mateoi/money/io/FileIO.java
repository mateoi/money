package com.mateoi.money.io;

import com.mateoi.money.model.MainState;

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

    public void save(String location, MainState state) throws IOException {
        Path path = Paths.get(location);
        save(path, state);
    }

    public void save(Path path, MainState state) throws IOException {
        String lines = state.toString();
        Files.write(path, lines.getBytes());
    }

    public void load(String location) throws IOException {
        Path path = Paths.get(location);
        load(path);
    }

    public void load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        Parser.getInstance().parseIntoMainState(lines);
    }

}
