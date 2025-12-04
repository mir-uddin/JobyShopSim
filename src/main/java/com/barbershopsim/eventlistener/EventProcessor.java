package com.barbershopsim.eventlistener;

import com.barbershopsim.eventsource.EventGenerator;
import com.barbershopsim.model.Barber;
import com.barbershopsim.model.State;
import com.barbershopsim.model.events.CustomEvent;
import com.barbershopsim.model.events.Event;
import com.barbershopsim.model.events.ExitType;
import com.barbershopsim.model.events.ShopEvent;
import com.google.common.eventbus.Subscribe;

import java.util.Random;
import java.util.Set;

/**
 * Processes shop events and updates state accordingly.
 */
class EventProcessor implements EventListener {
    private static final BusManager busManager = BusManager.getInstance();
    private static final Random random = new Random();
    // State
    private static final State state = State.getInstance();
    private static final State.ShiftInfo shiftInfo = state.shiftInfo();
    private final State.WaitingArea waitingArea = State.getInstance().waitingArea();
    private final State.Chairs chairs = State.getInstance().chairs();

    @Subscribe
    public void onEventProcess(Event event) {
        if (event instanceof ShopEvent.ShopOpen) {
            state.isShopOpen = true;
        } else if (event instanceof ShopEvent.ShopClose(int time, _)) {
            state.isShopOpen = false;
            while (!waitingArea.isEmpty()) {
                int customer = waitingArea.removePriorityCustomer();
                busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.CURSING));
            }
        } else if (event instanceof ShopEvent.ShiftStart(_, Barber barber)) {
            State.Chair chair = chairs.assignedToBarber(null);
            chair.setBarber(barber);
            shiftInfo.notWorking().remove();
            shiftInfo.working().add(barber);
        } else if (event instanceof ShopEvent.ShiftEnd(_, Barber barber)) {
            shiftInfo.notWorking().add(barber);
            shiftInfo.working().remove(barber);
            State.Chair chair = chairs.assignedToBarber(barber);
            if (chair != null) {
                chair.setBarber(null);
            }
        } else if (event instanceof ShopEvent.CustomerEnter(int time, int customer)) {
            if (time >= State.END_TIME) {
                busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.DISAPPOINTED));
            } else if (!waitingArea.addCustomer(customer, time)) {
                busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.UNFULFILLED));
            }
        } else if (event instanceof ShopEvent.CutEnd(int time, _, int customer)) {
            busManager.post(new ShopEvent.CustomerExit(time, customer, ExitType.SATISFIED));
        } else if (event instanceof CustomEvent.ClockTick(int time)) {
            checkCutEnds(time);
            busManager.post(new CustomEvent.AttemptHaircut(time));
        } else if (event instanceof CustomEvent.AttemptHaircut(int time)) {
            attemptHaircut(time);
        }

        if (!state.isShopOpen && shiftInfo.working().isEmpty()) {
            EventGenerator.end();
            busManager.unregisterAll();
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
                chair.setEndTime(startTime + random.nextInt(State.CUT_DURATION_MIN_INCL, State.CUT_DURATION_MAX_EXCL) * 60);
                busManager.post(new ShopEvent.CutStart(startTime, chair.barber(), customer));
            }
        }
    }
}
