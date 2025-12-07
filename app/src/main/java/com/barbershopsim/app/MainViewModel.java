package com.barbershopsim.app;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.barbershopsim.simulation.Simulation;
import com.barbershopsim.simulation.eventlistener.EventListener;
import com.barbershopsim.simulation.model.State;
import com.barbershopsim.simulation.model.events.CustomEvent;
import com.barbershopsim.simulation.model.events.ShopEvent;
import com.google.common.eventbus.Subscribe;

import java.util.List;

import static com.barbershopsim.simulation.Utils.formatTime;

public class MainViewModel extends ViewModel implements EventListener {
    /**
     * 1 real second == 10,000 simulated seconds.
     */
    private static final int TIMESCALE_FACTOR = 10_000;
    private final MutableLiveData<Boolean> _isShopOpen = new MutableLiveData<>();
    public final LiveData<Boolean> isShopOpen = _isShopOpen;
    private final MutableLiveData<Integer> _customerEnter = new MutableLiveData<>();
    public final LiveData<Integer> customerEnter = _customerEnter;
    private final MutableLiveData<Integer> _customerExit = new MutableLiveData<>();
    public final LiveData<Integer> customerExit = _customerExit;
    private final MutableLiveData<List<Integer>> _waitingArea = new MutableLiveData<>();
    public final LiveData<List<Integer>> waitingArea = _waitingArea;
    private final MutableLiveData<List<Pair<String, Integer>>> _haircutArea = new MutableLiveData<>();
    public final LiveData<List<Pair<String, Integer>>> haircutArea = _haircutArea;
    private final MutableLiveData<String> _clock = new MutableLiveData<>();
    public final LiveData<String> clock = _clock;
    private Simulation simulation;

    public void start() {
        if (simulation != null) {
            simulation.end();
        }
        simulation = new Simulation(TIMESCALE_FACTOR);
        simulation.start(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (simulation != null) {
            simulation.end();
        }
    }

    @Subscribe
    public void onEvent(ShopEvent event) {
        switch (event) {
            case ShopEvent.ShopOpen _ -> _isShopOpen.postValue(true);
            case ShopEvent.ShopClose _ -> _isShopOpen.postValue(false);
            case ShopEvent.CustomerEnter(_, int customer) -> _customerEnter.postValue(customer);
            case ShopEvent.CustomerExit(_, int customer, _) -> _customerExit.postValue(customer);
            default -> {
                // no-op
            }
        }

        // Triggered on all shop events
        State state = simulation.getState();
        _waitingArea.postValue(state.waitingArea().customers());
        _haircutArea.postValue(
                state.chairs().list().stream().map(
                        chair ->
                                Pair.create(
                                        chair.barber() != null ? chair.barber().name() : null,
                                        chair.customer()
                                )
                ).toList()
        );
    }

    @Subscribe
    public void onEvent(CustomEvent.ClockTick event) {
        _clock.postValue(formatTime(event.time()));
    }

    @Subscribe
    public void onEvent(CustomEvent.ProgramTermination event) {
        _customerEnter.postValue(null);
        _customerExit.postValue(null);
    }
}