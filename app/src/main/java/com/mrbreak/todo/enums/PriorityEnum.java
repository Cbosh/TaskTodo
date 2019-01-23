package com.mrbreak.todo.enums;

public enum PriorityEnum {
    HIGH(0, "High"),
    MEDIUM(1, "Medium"),
    LOW(2, "Low");

    private String stringValue;
    private int intValue;

    private PriorityEnum(int value, String toString) {
        stringValue = toString;
        intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
