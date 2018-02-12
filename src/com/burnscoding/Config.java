package com.burnscoding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Config {
    public static String SQL_ROOT="./sql/";
    public static String DB_URL="jdbc:postgresql://192.168.1.16:5432/stocktrack_db";
    public static String DB_USERNAME="stocktrack";
    public static String DB_PASSWORD="st_password";
    public static int PATTERN_LENGTH=64;
    public static int PATTERN_NUMBER=1000;
    public static String SYMBOL_LIST_PATH="symbols.txt";
    public static int QUERY_DELAY=5000;
    public static int SYMBOL_LIST_LENGTH=3286;

    public static Connection getConnection() {
        Connection conn=null;
        try {
            System.out.println("Loading resources...");
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
        }
        catch(ClassNotFoundException e) {
            System.out.println("Class org.postgresql.Driver not found!");
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
}
