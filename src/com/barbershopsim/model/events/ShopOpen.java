package com.barbershopsim.model.events;

public record ShopOpen(String name) implements Event {
    @Override
    public String toString() {
        return name + " is open for business!";
    }
}

