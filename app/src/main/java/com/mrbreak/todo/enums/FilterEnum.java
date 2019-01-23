package com.mrbreak.todo.enums;

public enum FilterEnum {
    PRIORITY(0),
    CATEGORY(1),
    DATE(2);

    private int intValue;

    private FilterEnum(int value) {
        intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }
}
