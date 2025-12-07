package com.barbershopsim.simulation;

public class SimUtils {

    public static String formatTime(int seconds) {
        int hours = (seconds / (60 * 60)) % 24;
        int minutes = (seconds % (60 * 60)) / 60;
        return "[" + String.format("%02d:%02d", hours, minutes) + "] ";
    }
}
