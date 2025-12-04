package com.barbershopsim.eventlistener;

import com.barbershopsim.model.events.Event;
import com.google.common.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

public class BusManager {
    private static final EventProcessor eventProcessor = new EventProcessor();
    private static BusManager instance;
    private final EventBus bus = new EventBus();
    private final Set<EventListener> subscribed = new HashSet<>();

    public static BusManager getInstance() {
        if (instance == null)
            instance = new BusManager();
        return instance;
    }

    public void register(EventListener listener) {
        bus.register(listener);
        subscribed.add(listener);
        if (!subscribed.contains(eventProcessor)) {
            bus.register(eventProcessor);
            subscribed.add(eventProcessor);
        }
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
