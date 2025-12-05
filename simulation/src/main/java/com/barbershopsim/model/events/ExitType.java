package com.barbershopsim.model.events;

public enum ExitType {
    SATISFIED("satisfied"),
    UNFULFILLED("unfulfilled"),
    DISAPPOINTED("disappointed"),
    CURSING("cursing"),
    FRUSTRATED("frustrated");

    public final String text;

    ExitType(String text) {
        this.text = text;
    }
}
