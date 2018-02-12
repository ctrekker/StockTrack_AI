package com.burnscoding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Query {
    private static Connection conn;
    public static void main(String[] args) {
        Date runStart=new Date();
        conn=Config.getConnection();

        if(conn!=null) {
            System.out.println("Database connection initialized");

            try {
//                PreparedStatement statement=conn.prepareStatement("SELECT * FROM stock_data.a");
//                ResultSet set = statement.executeQuery();
//                while(set.next()) {
//                    System.out.print(set.getTimestamp("entry_time"));
//                }
                String createTableQuery =
                        "CREATE TABLE IF NOT EXISTS public.stock_data_combined\n" +
                                "(\n" +
                                "    entry_time TIMESTAMP NOT NULL,\n" +
                                "    symbol VARCHAR(6),\n" +
                                "    open FLOAT,\n" +
                                "    high FLOAT,\n" +
                                "    low FLOAT,\n" +
                                "    close FLOAT,\n" +
                                "    volume FLOAT\n" +
                                ");";

                conn.prepareStatement(createTableQuery).execute();

                conn
                        .prepareStatement("DROP INDEX IF EXISTS stock_data_index;")
                        .execute();

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

                // Re-index the database for faster accessing
                conn
                        .prepareStatement("CREATE INDEX IF NOT EXISTS stock_data_index ON public.stock_data_combined (symbol, entry_time);")
                        .execute();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch(SQLException e) {
                System.out.println("Unable to initialize database indicies/tables");
            }
        }
        System.out.println("Execution took "+((new Date().getTime()-runStart.getTime())/1000.0/60.0/60.0)+" hours");
    }

    private static void saveQuery(String symbol, String response) throws SQLException {
        String[] responseLines=response.split("\n");

        InsertStatement s=new InsertStatement("public.stock_data_combined");
        s.addColumn("entry_time");
        s.addColumn("symbol");
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
            s.addValue(new InsertValue<>(symbol));
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
