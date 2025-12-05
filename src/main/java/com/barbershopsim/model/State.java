package com.barbershopsim.model;

import java.util.*;

public class State {
    public static final String SHOP_NAME = "RWB cuts";
    /**
     * 09:00 in seconds.
     */
    public static final int START_TIME = 9 * 60 * 60;
    /**
     * 17:00 in seconds. 8 hours after START_TIME.
     */
    public static final int END_TIME = START_TIME + 8 * 60 * 60;
    /**
     * 5 minutes.
     */
    public static final int CUSTOMER_FREQUENCY = 5 * 60;
    /**
     * 4-hour shift.
     */
    public static final int SHIFT_DURATION = 4 * 60 * 60;
    /**
     * 20 minutes.
     */
    public static final int CUSTOMER_FRUSTRATION_THRESHOLD = 20 * 60;
    /**
     * 20 minutes (in minutes, not seconds).
     */
    public static final int CUT_DURATION_MIN = 20;
    /**
     * 40 minutes (in minutes, not seconds).
     */
    public static final int CUT_DURATION_MAX = 40;
    private static State instance;
    private final Chairs chairs = new Chairs();
    private final WaitingArea waitingArea = new WaitingArea();
    private final ShiftInfo shiftInfo = new ShiftInfo();
    public boolean isShopOpen = false;

    public static State getInstance() {
        if (instance == null)
            instance = new State();
        return instance;
    }

    public Chairs chairs() {
        return chairs;
    }

    public WaitingArea waitingArea() {
        return waitingArea;
    }

    public ShiftInfo shiftInfo() {
        return shiftInfo;
    }

    public static final class Chair {
        private Barber barber;
        private int customer;
        private int endTime;

        public Chair(Barber barber, int customer, int endTime) {
            this.barber = barber;
            this.customer = customer;
            this.endTime = endTime;
        }

        public Barber barber() {
            return barber;
        }

        public int customer() {
            return customer;
        }

        public int endTime() {
            return endTime;
        }

        public void setBarber(Barber barber) {
            this.barber = barber;
        }

        public void setCustomer(int customer) {
            this.customer = customer;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }
    }

    public static class Chairs {
        private final List<Chair> chairs = List.of(
                new Chair(null, -1, -1),
                new Chair(null, -1, -1),
                new Chair(null, -1, -1),
                new Chair(null, -1, -1)
        );

        /**
         * @param barber - if null, returns the first Chair that isn't assigned to a barber.
         */
        public Chair assignedToBarber(Barber barber) {
            for (Chair chair : chairs) {
                if (chair.barber() == barber) return chair;
            }
            return null;
        }

        public Chair availableForCustomer() {
            for (Chair chair : chairs) {
                if (chair.customer() == -1) return chair;
            }
            return null;
        }

        public List<Chair> availableForShiftChange() {
            List<Chair> res = new ArrayList<>();
            for (Chair chair : chairs) {
                if (chair.customer() == -1) res.add(chair);
            }
            return res;
        }

        public List<Chair> endedCuts(int time) {
            List<Chair> res = new ArrayList<>();
            for (Chair chair : chairs) {
                if (chair.endTime() > -1 && time >= chair.endTime()) res.add(chair);
            }
            return res;
        }
    }

    public static class WaitingArea {
        private final PriorityQueue<WaitingState> queue = new PriorityQueue<>(Comparator.comparing(a -> a.customer() + a.startTime()));

        public boolean addCustomer(int customer, int startTime) {
            if (queue.size() == 4) return false;
            queue.add(new WaitingState(customer, startTime));
            return true;
        }

        public int getPriorityCustomer() {
            if (queue.isEmpty()) return -1;
            return queue.peek().customer;
        }

        public int getPriorityCustomerStartTime() {
            if (queue.isEmpty()) return -1;
            return queue.peek().startTime;
        }

        public int removePriorityCustomer() {
            return queue.remove().customer;
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        private record WaitingState(int customer, int startTime) {
        }
    }

    public static class ShiftInfo {
        private final Set<Barber> working = new HashSet<>();
        private final Set<Barber> wrappingUpWork = new HashSet<>();
        private final Queue<Barber> notWorking = new ArrayDeque<>(Arrays.asList(Barber.values()));

        public Set<Barber> working() {
            return working;
        }

        public Set<Barber> wrappingUpWork() {
            return wrappingUpWork;
        }

        public Queue<Barber> notWorking() {
            return notWorking;
        }
    }
}