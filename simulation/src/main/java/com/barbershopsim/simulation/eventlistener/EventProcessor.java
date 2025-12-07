package com.barbershopsim.simulation.eventlistener;

import com.barbershopsim.simulation.model.Barber;
import com.barbershopsim.simulation.model.State;
import com.barbershopsim.simulation.model.events.CustomEvent;
import com.barbershopsim.simulation.model.events.Event;
import com.barbershopsim.simulation.model.events.ExitType;
import com.barbershopsim.simulation.model.events.ShopEvent;
import com.google.common.eventbus.Subscribe;

import java.util.Random;
import java.util.Set;

/**
 * Processes shop events and updates state accordingly.
 */
public class EventProcessor implements EventListener {
    private static final Random random = new Random();
    private final BusManager busManager;
    // State
    private final State state;
    private final State.ShiftInfo shiftInfo;
    private final State.WaitingArea waitingArea;
    private final State.Chairs chairs;

    public EventProcessor(State state, BusManager busManager) {
        this.state = state;
        shiftInfo = state.shiftInfo();
        waitingArea = state.waitingArea();
        chairs = state.chairs();
        this.busManager = busManager;
    }

    @Subscribe
    public void onEventProcess(Event event) {
        switch (event) {
            case ShopEvent.ShopOpen _ -> state.isShopOpen = true;
            case ShopEvent.ShopClose(int time, _) -> {
                state.isShopOpen = false;
                while (!waitingArea.isEmpty()) {
                    int customer = waitingArea.removePriorityCustomer();
                    busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.CURSING));
                }
            }
            case ShopEvent.ShiftStart(_, Barber barber) -> {
                State.Chair chair = chairs.assignedToBarber(null);
                chair.setBarber(barber);
                shiftInfo.notWorking().remove();
                shiftInfo.working().add(barber);
            }
            case ShopEvent.ShiftEnd(_, Barber barber) -> {
                shiftInfo.notWorking().add(barber);
                shiftInfo.working().remove(barber);
                State.Chair chair = chairs.assignedToBarber(barber);
                if (chair != null) {
                    chair.setBarber(null);
                }
            }
            case ShopEvent.CustomerEnter(int time, int customer) -> {
                if (time >= State.END_TIME) {
                    busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.DISAPPOINTED));
                } else if (!waitingArea.addCustomer(customer, time)) {
                    busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.UNFULFILLED));
                }
            }
            case ShopEvent.CutEnd(int time, _, int customer) ->
                    busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.SATISFIED));
            case CustomEvent.ClockTick(int time) -> {
                checkCutEnds(time);
                busManager.post(new CustomEvent.AttemptHaircut(time));
            }
            case CustomEvent.AttemptHaircut(int time) -> attemptHaircut(time);
            case null, default -> {
                // no-op
            }
        }

        if (!state.isShopOpen && shiftInfo.working().isEmpty()) {
            busManager.post(new CustomEvent.ProgramTermination());
        }
    }

    private void checkCutEnds(int time) {
        for (State.Chair chair : chairs.endedCuts(time)) {
            busManager.post(new ShopEvent.CutEnd(time, chair.barber(), chair.customer()));
            chair.setCustomer(-1);
            chair.setEndTime(-1);
            retireBarberIfNeeded(time, chair);
        }
    }

    private void retireBarberIfNeeded(int time, State.Chair chair) {
        Set<Barber> wrappingUpWork = shiftInfo.wrappingUpWork();
        Barber barber = chair.barber();
        if (wrappingUpWork.contains(barber)) {
            wrappingUpWork.remove(barber);
            busManager.post(new ShopEvent.ShiftEnd(time, barber));

            if (time < State.END_TIME) {
                Barber nextBarber = shiftInfo.notWorking().peek();
                busManager.post(new ShopEvent.ShiftStart(time, nextBarber));
            }
        }
    }

    private void attemptHaircut(int startTime) {
        int customer = waitingArea.getPriorityCustomer();
        int customerStartTime = waitingArea.getPriorityCustomerStartTime();
        while (customer != -1 && startTime >= customerStartTime + State.CUSTOMER_FRUSTRATION_THRESHOLD) {
            busManager.post(new ShopEvent.CustomerExit(startTime, customer, ExitType.FRUSTRATED));
            waitingArea.removePriorityCustomer();
            customer = waitingArea.getPriorityCustomer();
            customerStartTime = waitingArea.getPriorityCustomerStartTime();
        }
        if (customer != -1) {
            State.Chair chair = chairs.availableForCustomer();
            if (chair != null) {
                waitingArea.removePriorityCustomer();
                chair.setCustomer(customer);
                chair.setEndTime(startTime + random.nextInt(State.CUT_DURATION_MIN, State.CUT_DURATION_MAX + 1 /*exclusive*/) * 60);
                busManager.post(new ShopEvent.CutStart(startTime, chair.barber(), customer));
            }
        }
    }
}
