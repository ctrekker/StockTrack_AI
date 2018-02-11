package com.burnscoding;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class DB {
    public static Connection conn=null;
    public static void setConnection(Connection conn) {
        DB.conn=conn;
    }
    public static Connection getConnection() {
        return conn;
    }

    public static void executeSQL(String file) {
        executeSQL(file, new String[0]);
    }
    public static void executeSQL(File file) {
        executeSQL(file, new String[0]);
    }
    public static void executeSQL(String file, String[] params) {
        executeSQL(new File(Config.SQL_ROOT+file), params);
    }
    public static void executeSQL(File file, String[] params) {
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            StringBuilder sqlBuilder=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null) {
                sqlBuilder.append(line+"\n");
            }

            String sql=sqlBuilder.toString();
            // Replace parts of sql with parameters
            for(int i=0; i<params.length; i++) {
                sql=sql.replace("$"+(i+1), params[i]);
            }

            try {
                conn.prepareStatement(sql).execute();
            }
            catch(SQLException e) {
                System.out.println("Unable to prepare the following sql statement: ");
                System.out.println(sql);
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("No sql command located at "+file.getPath());
        }
        catch(IOException e) {
            System.out.println("Unable to read file located at "+file.getPath());
        }
    }
}
