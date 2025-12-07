package com.barbershopsim.simulation.eventsource;

import com.barbershopsim.simulation.eventlistener.BusManager;
import com.barbershopsim.simulation.model.Barber;
import com.barbershopsim.simulation.model.State;
import com.barbershopsim.simulation.model.events.CustomEvent;
import com.barbershopsim.simulation.model.events.ShopEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Generates simulated shop events.
 */
public class EventGenerator implements EventSource {
    // Simulation
    /**
     * 60 seconds.
     */
    private static final int CLOCK_TICK = 60;
    private final int timescaleFactor;
    private final ScheduledExecutorService exec;
    private final BusManager busManager;
    // State
    private final State.Chairs chairs;
    private final State.ShiftInfo shiftInfo;
    private int time = State.START_TIME;
    private int customer = 1;

    public EventGenerator(State state, BusManager busManager, int timescaleFactor) {
        this.chairs = state.chairs();
        this.shiftInfo = state.shiftInfo();
        this.busManager = busManager;
        this.exec = Executors.newSingleThreadScheduledExecutor();
        this.timescaleFactor = timescaleFactor;
    }

    private int toScaledMillis(int seconds) {
        return seconds * 1_000 / timescaleFactor;
    }

    public void start() {
        busManager.post(new ShopEvent.ShopOpen(time, State.SHOP_NAME));
        scheduleClock();
        scheduleShifts();
        scheduleTraffic();
    }

    public void end() {
        exec.shutdown();
    }

    private void scheduleClock() {
        exec.scheduleAtFixedRate(() -> {
            busManager.post(new CustomEvent.ClockTick(time));
            time += CLOCK_TICK;
        }, toScaledMillis(CLOCK_TICK), toScaledMillis(CLOCK_TICK), TimeUnit.MILLISECONDS);
    }

    private void scheduleShifts() {
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

    private void scheduleTraffic() {
        exec.scheduleAtFixedRate(() -> {
            busManager.post(new ShopEvent.CustomerEnter(time, customer++));
        }, 0, toScaledMillis(State.CUSTOMER_FREQUENCY), TimeUnit.MILLISECONDS);
    }
}