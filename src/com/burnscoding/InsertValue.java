package com.burnscoding;

public class InsertValue<T> {

    private T value;
    public InsertValue(T val) {
        value=val;
    }

    public T getValue() {
        return value;
    }
    public void setValue(T value) {
        this.value = value;
    }
}
