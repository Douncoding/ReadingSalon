package com.douncoding.readingsalon;

public enum ContentsType {
    TODAY(1), BOOK(2), NOTICE(3), FAVOR(4);
    int value;
    ContentsType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
