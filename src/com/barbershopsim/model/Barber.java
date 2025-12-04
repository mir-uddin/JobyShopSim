package com.barbershopsim.model;

public enum Barber {
    A("Alice"),
    B("Bob"),
    C("Charlie"),
    D("Dave"),
    E("Eve"),
    M("Mallory"),
    T("Trent");

    public final String name;

    Barber(String name) {
        this.name = name;
    }
}
