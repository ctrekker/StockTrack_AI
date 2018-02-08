package com.burnscoding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Query {
    private static Connection conn;
    public static void main(String[] args) {
        Date runStart=new Date();
        try {
            System.out.println("Loading resources...");
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/stocktrack_db", "stocktrack", "st_password");
        }
        catch(ClassNotFoundException e) {
            System.out.println("Class org.postgresql.Driver not found!");
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        if(conn!=null) {
            System.out.println("Database connection initialized");

            try {
//                PreparedStatement statement=conn.prepareStatement("SELECT * FROM stock_data.a");
//                ResultSet set = statement.executeQuery();
//                while(set.next()) {
//                    System.out.print(set.getTimestamp("entry_time"));
//                }
                BufferedReader symbolReader=new BufferedReader(new FileReader(Config.SYMBOL_LIST_PATH));
                int count=0;
                String symbol;
                while((symbol = symbolReader.readLine()) != null) {
                    Date startTime=new Date();

                    System.out.println("---- "+(Math.round(((((double)count)/Config.SYMBOL_LIST_LENGTH)*100)*100)/100.0)+"% ----");
                    System.out.println("Querying "+symbol+" data...");
                    String response=HTTPUtils.sendGet(getRequestUrl(symbol));
                    // General failure
                    if(response.contains("Error Message")) {
                        System.out.println("Could not get data");
                    }
                    // Server-side error
                    else if(response.contains("<!DOCTYPE html>")) {
                        System.out.println("Server error!");
                    }
                    // Successful query!
                    else {
                        System.out.println("Received "+response.getBytes().length+" bytes");
                        try {
                            saveQuery(symbol, response);
                            System.out.println("Completed "+symbol);
                        }
                        catch(SQLException e) {
                            System.out.println("ERROR: unable to save data to database");
                        }
                    }

                    count++;
                    System.out.println();
                    while(startTime.getTime()+Config.QUERY_DELAY>(new Date().getTime())) {
                        // Wait for Config.QUERY_DELAY milliseconds before continuing
                    }
                }
                symbolReader.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Execution took "+((new Date().getTime()-runStart.getTime())/1000.0/60.0/60.0)+" hours");
    }

    private static void saveQuery(String symbol, String response) throws SQLException {
        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS stock_data."+symbol+"\n" +
                        "(\n" +
                        "    entry_time TIMESTAMP PRIMARY KEY NOT NULL,\n" +
                        "    open FLOAT,\n" +
                        "    high FLOAT,\n" +
                        "    low FLOAT,\n" +
                        "    close FLOAT,\n" +
                        "    volume FLOAT\n" +
                        ");";

        String[] responseLines=response.split("\n");

        conn.prepareStatement(createTableQuery).execute();

        InsertStatement s=new InsertStatement("stock_data."+symbol);
        s.addColumn("entry_time");
        s.addColumn("open");
        s.addColumn("high");
        s.addColumn("low");
        s.addColumn("close");
        s.addColumn("volume");

        for(String line : responseLines) {
            // If its the first line, skip it (no actual data)
            if(line.equals("timestamp,open,high,low,close,volume")) continue;

            String[] splitLine=line.split(",");
            s.addValue(new InsertValue<>(splitLine[0]));
            s.addValue(new InsertValue<>(Double.parseDouble(splitLine[1])));
            s.addValue(new InsertValue<>(Double.parseDouble(splitLine[2])));
            s.addValue(new InsertValue<>(Double.parseDouble(splitLine[3])));
            s.addValue(new InsertValue<>(Double.parseDouble(splitLine[4])));
            s.addValue(new InsertValue<>(Integer.parseInt(splitLine[5])));

            conn.prepareStatement(s.getQuery()).execute();

            s.setValues(new ArrayList<>());
        }
    }

    public static String getRequestUrl(String symbol) {
        return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+symbol+"&interval=5min&datatype=csv&apikey=KL04FKXI8NH3L70I";
    }
}
