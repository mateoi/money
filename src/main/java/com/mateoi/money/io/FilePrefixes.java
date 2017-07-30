package com.mateoi.money.io;

/**
 * File prefixes used for save files
 */
public class FilePrefixes {
    public static final String ACCOUNT_PREFIX = "A: ";

    public static final String BUDGET_PREFIX = "B: ";

    public static final String SAVINGS_PREFIX = "S: ";

    public static final String TRANSACTION_PREFIX = "T: ";

    public static final String TX_COUNT_PREFIX = "TX_COUNT: ";

    public static final String ACCOUNT_COUNT_PREFIX = "ACC_COUNT: ";

    public static final String BUDGET_COUNT_PREFIX = "BUDGET_COUNT: ";

    public static final String SAVINGS_COUNT_PREFIX = "SAVINGS_COUNT: ";

    public static final String SETTINGS_FILE_PREFIX = "F: ";

    public static final String SETTINGS_RECENT_FILE_PREFIX = "RF: ";

    public static final String SETTINGS_DEFAULT_CURRENCY_PREFIX = "C: ";

    public static final String SETTINGS_COLOR_PREFIX = "COLOR: ";

    public static final String SETTINGS_MAX_RECENT_FILES = "MRF: ";

    private FilePrefixes() {
        // private to prevent instantiation
    }
}
