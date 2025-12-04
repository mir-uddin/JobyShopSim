package com.barbershopsim.main;

import com.barbershopsim.model.events.Event;

public class Publisher {
    static void publish(Event e, int time) {
        String text = "[" + formatTime(time) + "] " + e.toString();
        System.out.println(text);
    }

    private static String formatTime(int time) {

    }
}
