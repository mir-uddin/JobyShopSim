package com.barbershopsim.simulation.model;

public enum Barber {
    A("Alice"),
    B("Bob"),
    C("Charlie"),
    D("Dave"),
    E("Eve"),
    F("Frank"),
    G("Grace"),
    H("Hector");

    public final String name;

    Barber(String name) {
        this.name = name;
    }
}
