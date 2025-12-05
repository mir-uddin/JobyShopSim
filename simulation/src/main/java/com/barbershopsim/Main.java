package com.barbershopsim;

import com.barbershopsim.eventlistener.BusManager;
import com.barbershopsim.eventlistener.EventOutputter;
import com.barbershopsim.eventsource.EventGenerator;

public class Main {

    public static void main(String[] args) {
        BusManager.getInstance().register(new EventOutputter());
        EventGenerator.start();
    }
}
