package com.mrbreak.todo.enums;

public enum ReminderEnum {
    FIVE_MINUTES(5, "5 minutes before"),
    TEN_MINUTES(10, "10 minutes before"),
    FIFTEEN_MINUTES(15, "15 minutes before"),
    THIRTY_MINUTES(30, "30 minutes before");

    private String stringValue;
    private int intValue;

    private ReminderEnum(int value, String toString) {
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
