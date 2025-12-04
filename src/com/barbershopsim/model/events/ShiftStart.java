package com.barbershopsim.model.events;

import com.barbershopsim.model.Barber;

public record ShiftStart(Barber barber) implements Event {
    @Override
    public String toString() {
        return barber.name + " started shift";
    }
}
