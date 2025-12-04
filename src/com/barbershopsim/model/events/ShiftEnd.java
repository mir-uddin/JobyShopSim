package com.barbershopsim.model.events;

import com.barbershopsim.model.Barber;

public record ShiftEnd(Barber barber) implements Event {
    @Override
    public String toString() {
        return barber.name + " ended shift";
    }
}
