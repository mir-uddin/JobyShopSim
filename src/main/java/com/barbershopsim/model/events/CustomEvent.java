package com.barbershopsim.model.events;

public interface CustomEvent extends Event {
    record ClockTick(int time) implements CustomEvent {
    }

    record AttemptHaircut(int time) implements CustomEvent {
    }
}
