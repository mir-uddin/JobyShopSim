package com.barbershopsim.simulation;

import com.barbershopsim.simulation.eventlistener.BusManager;
import com.barbershopsim.simulation.eventlistener.EventListener;
import com.barbershopsim.simulation.eventlistener.EventProcessor;
import com.barbershopsim.simulation.eventsource.EventGenerator;
import com.barbershopsim.simulation.model.State;
import com.barbershopsim.simulation.model.events.CustomEvent;
import com.google.common.eventbus.Subscribe;

public class Simulation implements EventListener {
    private final EventGenerator eventGenerator;
    private final EventProcessor eventProcessor;
    private final BusManager busManager;

    private boolean ended = false;

    public Simulation(int timescaleFactor) {
        busManager = new BusManager();
        State state = new State();
        eventGenerator = new EventGenerator(state, busManager, timescaleFactor);
        eventProcessor = new EventProcessor(state, busManager);

        busManager.register(eventProcessor);
        // Program termination listener
        busManager.register(this);
    }

    public void start(EventListener listener) throws IllegalStateException {
        if (ended) {
            throw new IllegalStateException("Simulation ended. Create a new object to start a new simulation.");
        }
        busManager.register(listener);
        eventGenerator.start();
    }

    @Subscribe
    public void onProgramTermination(CustomEvent.ProgramTermination event) {
        end();
    }

    public void end() {
        ended = true;
        eventGenerator.end();
        busManager.unregisterAll();
    }
}
