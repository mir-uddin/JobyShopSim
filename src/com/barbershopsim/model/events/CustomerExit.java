package com.barbershopsim.model.events;

public record CustomerExit(String customer, CustomerExitType type) implements Event {
    @Override
    public String toString() {
        return "Customer-" + customer + " leaves " + type.text;
    }
}
