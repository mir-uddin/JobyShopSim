package com.barbershopsim.simulation.model.events;

import com.barbershopsim.simulation.model.Barber;

import static com.barbershopsim.simulation.SimUtils.formatTime;

public interface ShopEvent extends Event {

    record ShopOpen(int time, String name) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + name + " is open for business!";
        }
    }

    record ShopClose(int time, String name) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + name + " is closed";
        }
    }

    record ShiftStart(int time, Barber barber) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + barber.name + " started shift";
        }
    }

    record ShiftEnd(int time, Barber barber) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + barber.name + " ended shift";
        }
    }

    record CutStart(int time, Barber barber, int customer) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + barber.name + " started cutting Customer-" + customer + "'s hair";
        }
    }

    record CutEnd(int time, Barber barber, int customer) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + barber.name + " finished cutting Customer-" + customer + "'s hair";
        }
    }

    record CustomerEnter(int time, int customer) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + "Customer-" + customer + " entered";
        }
    }

    record CustomerExit(int time, int customer, ExitType type) implements ShopEvent {
        @Override
        public String toString() {
            return formatTime(time) + "Customer-" + customer + " leaves " + type.text;
        }
    }
}
