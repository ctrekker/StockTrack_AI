package com.burnscoding;

public class Config {
    public static String DB_URL="jdbc:postgresql://localhost:5432/stocktrack_db";
    public static String DB_USERNAME="stocktrack";
    public static String DB_PASSWORD="st_password";
    public static String SYMBOL_LIST_PATH="symbols.txt";
    public static int QUERY_DELAY=5000;
    public static int SYMBOL_LIST_LENGTH=3286;
}
