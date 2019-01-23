package com.mrbreak.todo.enums;

public enum CategoryEnum {
    GENERAL(3, "General"),
    PERSONAL(4, "Personal"),
    WORK(5, "Work"),
    BUSINESS(6, "Business"),
    STUDIES(7, "Studies");

    private String stringValue;
    private int intValue;

    private CategoryEnum(int value, String toString) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
