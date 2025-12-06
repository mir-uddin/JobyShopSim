package com.barbershopsim.simulation.model.events;

public interface CustomEvent extends Event {
    record ClockTick(int time) implements CustomEvent {
    }

    record AttemptHaircut(int time) implements CustomEvent {
    }

    record ProgramTermination() implements CustomEvent {
    }
}
