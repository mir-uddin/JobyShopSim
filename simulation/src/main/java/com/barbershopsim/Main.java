package com.barbershopsim;

import com.barbershopsim.simulation.eventlistener.EventOutputter;
import com.barbershopsim.simulation.Simulation;

public class Main {

    public static void main(String[] args) {
        new Simulation().start(new EventOutputter());
    }
}
