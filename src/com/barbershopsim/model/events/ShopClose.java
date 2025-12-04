package com.barbershopsim.model.events;

public record ShopClose(String name) implements Event {
    @Override
    public String toString() {
        return name + " is closed";
    }
}
