package com.barbershopsim.model.events;

import com.barbershopsim.model.Barber;

public record CutStart(Barber barber, String customer) implements Event {
    @Override
    public String toString() {
        return barber.name + " started cutting Customer-" + customer + "'s hair";
    }
}
