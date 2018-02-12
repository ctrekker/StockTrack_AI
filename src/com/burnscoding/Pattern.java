package com.burnscoding;

import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Pattern {
    private static Connection conn;
    public static void main(String[] args) {
        Date runStart=new Date();
        conn=Config.getConnection();
        DB.setConnection(conn);

        if(conn!=null) {
            System.out.println("Database connection initialized");

            DB.executeSQL("create_pattern_table.sql");

//            DB.executeSQL("insert_pattern_table.sql", new String[]{"2.5, 3.5, 4.5", "2.25, 3.25, 4.25", "0.1, 0.4, 0.7723", "3", "\'ZNGA\'"});
            ResultSet randomStockEntry=DB.executeQuerySQL("select_stock_random.sql", new String[]{Config.PATTERN_NUMBER+""});
            try {
                int x=1;
                while (randomStockEntry.next()) {
                    String randomSymbol = null;
                    try {
                        randomSymbol = randomStockEntry.getString("symbol");
                    } catch (SQLException e) {
                        System.out.println("Error retrieving symbol from random column.");
                        e.printStackTrace();
                    }

                    System.out.println(x+"/"+Config.PATTERN_NUMBER+": "+randomSymbol);

                    if (randomSymbol != null) {
                        ResultSet stockHighestClose=DB.executeQuerySQL("select_stock_highest_close.sql", new String[]{"close", randomSymbol});
                        stockHighestClose.next();
                        float highest;
                        try {
                            highest=stockHighestClose.getFloat("close");
                        }
                        catch(PSQLException e) {
                            System.out.println("Error calling next(). Skipping...");
                            x++;
                            continue;
                        }

                        ResultSet stockData=DB.executeQuerySQL("select_stock_recent.sql", new String[]{"*", randomSymbol, "entry_time", Config.PATTERN_LENGTH+""});
                        float[] inputList=new float[Config.PATTERN_LENGTH];
                        float[] outputList=new float[Config.PATTERN_LENGTH];
                        float[] weightList=new float[Config.PATTERN_LENGTH];
                        int i=0;
                        while(stockData.next()) {
                            inputList[i]=stockData.getFloat("close")/highest;
                            outputList[i]=0;
                            weightList[i]=(float)Math.random();
                            i++;
                        }
                        DB.executeSQL("insert_pattern_table.sql", new String[]{joinFloat(inputList), joinFloat(outputList), joinFloat(weightList), inputList.length+"", randomSymbol});
                    }
                    x++;
                }
            }
            catch(SQLException e) {
                System.out.println("Error executing next() on resultSet");
                e.printStackTrace();
            }
        }
    }
    public static String joinFloat(float[] f) {
        StringBuilder out=new StringBuilder();
        for(int i=0; i<f.length; i++) {
            if(i!=f.length-1) {
                out.append(f[i]).append(",");
            }
            else {
                out.append(f[i]);
            }
        }
        return out.toString();
    }
}