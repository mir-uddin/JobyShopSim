package com.barbershopsim;

import com.barbershopsim.simulation.Simulation;
import com.barbershopsim.simulation.eventlistener.EventOutputter;

public class Main {
    /**
     * 1 real second == 10,000 simulated seconds.
     */
    private static final int TIMESCALE_FACTOR = 10_000;

    public static void main(String[] args) {
        new Simulation(TIMESCALE_FACTOR).start(new EventOutputter());
    }
}
