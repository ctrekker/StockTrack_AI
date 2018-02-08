package com.burnscoding;

import java.util.ArrayList;

public class InsertStatement {
    private String table;
    private ArrayList<String> columns;
    private ArrayList<InsertValue> values;
    public InsertStatement(String table) {
        this.table=table;
        values=new ArrayList<>();
        columns=new ArrayList<>();
    }

    public void addColumn(String column) {
        columns.add(column);
    }
    public void addValue(InsertValue val) {
        values.add(val);
    }

    public ArrayList<InsertValue> getValues() {
        return values;
    }
    public void setValues(ArrayList<InsertValue> values) {
        this.values = values;
    }

    public String getColumnString() {
        StringBuilder out=new StringBuilder();
        for(int i=0; i<columns.size(); i++) {
            out.append("\"").append(columns.get(i)).append("\"");
            if(i!=columns.size()-1) {
                out.append(", ");
            }
        }
        return out.toString();
    }
    public String getValueString() {
        StringBuilder out=new StringBuilder();
        for(int i=0; i<values.size(); i++) {
            if(values.get(i).getValue() instanceof String) {
                out.append("\'");
            }
            out.append(values.get(i).getValue());
            if(values.get(i).getValue() instanceof String) {
                out.append("\'");
            }
            if(i!=columns.size()-1) {
                out.append(", ");
            }
        }
        return out.toString();
    }
    public String getQuery() {
        String out="INSERT INTO "+table+" ("+getColumnString()+") VALUES ("+getValueString()+")";
        return out;
    }
}
