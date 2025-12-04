package com.barbershopsim.model.events;

public enum CustomerExitType {
    SATISFIED("satisfied"),
    UNFULFILLED("unfulfilled"),
    DISAPPOINTED("disappointed"),
    CURSING("cursing"),
    FRUSTRATED("frustrated");

    public final String text;

    CustomerExitType(String text) {
        this.text = text;
    }
}
