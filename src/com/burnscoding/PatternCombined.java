package com.burnscoding;

import java.sql.Connection;
import java.util.Date;

public class PatternCombined {
    private static Connection conn;
    public static void main(String[] args) {
        Date runStart=new Date();
        conn=Config.getConnection();
        DB.setConnection(conn);

        if(conn!=null) {
            System.out.println("Database connection initialized");

            DB.executeSQL("create_pattern_table.sql");

            DB.executeSQL("insert_pattern_table.sql", new String[]{"2.5, 3.5, 4.5", "2.25, 3.25, 4.25", "0.1, 0.4, 0.7723", "3", "\'ZNGA\'"});
        }
    }
}