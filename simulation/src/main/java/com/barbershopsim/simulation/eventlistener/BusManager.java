package com.barbershopsim.simulation.eventlistener;

import com.barbershopsim.simulation.model.events.Event;
import com.google.common.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

public class BusManager {
    private final EventBus bus = new EventBus();
    private final Set<EventListener> subscribed = new HashSet<>();

    public void register(EventListener listener) {
        bus.register(listener);
        subscribed.add(listener);
    }

    public void unregister(EventListener listener) {
        bus.unregister(listener);
        subscribed.remove(listener);
    }

    public void unregisterAll() {
        for (EventListener listener : subscribed) {
            bus.unregister(listener);
        }
        subscribed.clear();
    }

    public void post(Event event) {
        bus.post(event);
    }
}
