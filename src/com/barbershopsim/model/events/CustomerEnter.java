package com.barbershopsim.model.events;

public record CustomerEnter(String customer) implements Event {
    @Override
    public String toString() {
        return "Customer-" + customer + " entered";
    }
}
