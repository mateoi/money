package com.mateoi.money.io;

/**
 * Created by mateo on 06/07/2017.
 */
public class Parser {
    public static final Parser instance = new Parser();

    // Private constructor to prevent instantiation
    private Parser() {
        // Empty
    }

    public static Parser getInstance() {
        return instance;
    }

}
