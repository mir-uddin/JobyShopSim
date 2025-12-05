package com.barbershopsim.eventsource;

import com.barbershopsim.eventlistener.BusManager;
import com.barbershopsim.model.Barber;
import com.barbershopsim.model.State;
import com.barbershopsim.model.events.CustomEvent;
import com.barbershopsim.model.events.ShopEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Generates simulated shop events.
 */
public class EventGenerator implements EventSource {
    private static final BusManager busManager = BusManager.getInstance();
    // State
    private static final State.Chairs chairs = State.getInstance().chairs();
    private static final State.ShiftInfo shiftInfo = State.getInstance().shiftInfo();
    // Simulation
    private static final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    /**
     * 1 real second == 10,000 simulated seconds.
     */
    private static final int TIMESCALE_FACTOR = 10_000;
    /**
     * 60 seconds.
     */
    private static final int CLOCK_TICK = 60;
    private static int time = State.START_TIME;
    private static int customer = 1;

    private static int toScaledMillis(int seconds) {
        return seconds * 1_000 / TIMESCALE_FACTOR;
    }

    public static void start() {
        busManager.post(new ShopEvent.ShopOpen(time, State.SHOP_NAME));
        scheduleClock();
        scheduleShifts();
        scheduleTraffic();
    }

    public static void end() {
        exec.shutdown();
    }

    private static void scheduleClock() {
        exec.scheduleAtFixedRate(() -> {
            busManager.post(new CustomEvent.ClockTick(time));
            time += CLOCK_TICK;
        }, toScaledMillis(CLOCK_TICK), toScaledMillis(CLOCK_TICK), TimeUnit.MILLISECONDS);
    }

    private static void scheduleShifts() {
        exec.scheduleAtFixedRate(() -> {
            if (time == State.START_TIME) {
                int count = 0;
                for (Barber barber : shiftInfo.notWorking()) {
                    if (count == 4)
                        break;
                    busManager.post(new ShopEvent.ShiftStart(time, barber));
                    count++;
                }
            } else if (time == State.START_TIME + State.SHIFT_DURATION) {    // Shift change
                Set<Barber> wrappingUpWork = shiftInfo.wrappingUpWork();
                wrappingUpWork.addAll(shiftInfo.working());
                List<State.Chair> available = chairs.availableForShiftChange();
                for (State.Chair chair : available) {
                    Barber barber = chair.barber();
                    busManager.post(new ShopEvent.ShiftEnd(time, barber));
                    wrappingUpWork.remove(barber);

                    Barber nextBarber = shiftInfo.notWorking().peek();
                    busManager.post(new ShopEvent.ShiftStart(time, nextBarber));
                }
            } else if (time == State.END_TIME) {  // EOD
                busManager.post(new ShopEvent.ShopClose(time, State.SHOP_NAME));

                Set<Barber> wrappingUpWork = shiftInfo.wrappingUpWork();
                wrappingUpWork.addAll(shiftInfo.working());
                List<State.Chair> available = chairs.availableForShiftChange();
                for (State.Chair chair : available) {
                    Barber barber = chair.barber();
                    busManager.post(new ShopEvent.ShiftEnd(time, barber));
                    wrappingUpWork.remove(barber);
                }
            } else {    // time > END_TIME
                // no-op
            }
        }, 0, toScaledMillis(State.SHIFT_DURATION), TimeUnit.MILLISECONDS);
    }

    private static void scheduleTraffic() {
        exec.scheduleAtFixedRate(() -> {
            busManager.post(new ShopEvent.CustomerEnter(time, customer++));
        }, 0, toScaledMillis(State.CUSTOMER_FREQUENCY), TimeUnit.MILLISECONDS);
    }
}