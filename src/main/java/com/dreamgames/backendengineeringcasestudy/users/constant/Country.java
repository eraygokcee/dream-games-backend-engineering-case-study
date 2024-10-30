package com.dreamgames.backendengineeringcasestudy.users.constant;
public enum Country {
    Turkey(1),
    UnitedStates(2),
    UnitedKingdom(3),
    France(4),
    Germany(5);

    private final int code;

    Country(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
