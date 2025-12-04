package com.barbershopsim.model.events;

import com.barbershopsim.model.Barber;

public record CutEnd(Barber barber, String customer) implements Event {
    @Override
    public String toString() {
        return barber.name + " finished cutting Customer-" + customer + "'s hair";
    }
}
