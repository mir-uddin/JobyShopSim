package com.barbershopsim.app

import android.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barbershopsim.simulation.SimUtils
import com.barbershopsim.simulation.Simulation
import com.barbershopsim.simulation.eventlistener.EventListener
import com.barbershopsim.simulation.model.State
import com.barbershopsim.simulation.model.events.CustomEvent.ClockTick
import com.barbershopsim.simulation.model.events.CustomEvent.ProgramTermination
import com.barbershopsim.simulation.model.events.ShopEvent
import com.google.common.eventbus.Subscribe

class MainViewModel : ViewModel(), EventListener {
    private var simulation: Simulation? = null

    val shopName: String
        get() = State.SHOP_NAME

    val timescaleFactor: String
        get() = "1 real second =" + "%,d".format(TIMESCALE_FACTOR) + " simulated seconds"

    private val _isShopOpen = MutableLiveData<Boolean>()
    val isShopOpen: LiveData<Boolean> = _isShopOpen
    private val _customerEnter = MutableLiveData<Int?>()
    val customerEnter: LiveData<Int?> = _customerEnter
    private val _customerExit = MutableLiveData<Int?>()
    val customerExit: LiveData<Int?> = _customerExit
    private val _waitingArea = MutableLiveData<List<Int>>()
    val waitingArea: LiveData<List<Int>> = _waitingArea
    private val _haircutArea = MutableLiveData<List<Pair<String?, Int>>>()
    val haircutArea: LiveData<List<Pair<String?, Int>>> = _haircutArea
    private val _clock = MutableLiveData<String>()
    val clock: LiveData<String> = _clock

    companion object {
        /**
         * 1 real second == 10,000 simulated seconds.
         */
        private const val TIMESCALE_FACTOR = 10_000
    }

    fun start() {
        simulation?.end()
        simulation = Simulation(TIMESCALE_FACTOR)
        simulation?.start(this)
    }

    override fun onCleared() {
        super.onCleared()
        simulation?.end()
    }

    @Subscribe
    fun onEvent(event: ShopEvent) {

        when (event) {
            is ShopEvent.ShopOpen -> _isShopOpen.postValue(true)
            is ShopEvent.ShopClose -> _isShopOpen.postValue(false)
            is ShopEvent.CustomerEnter -> _customerEnter.postValue(event.customer())
            is ShopEvent.CustomerExit -> _customerExit.postValue(event.customer())
            else -> {}
        }

        // Triggered on all shop events
        val state = simulation!!.state
        _waitingArea.postValue(state.waitingArea().customers())
        _haircutArea.postValue(
            state.chairs().list().map { chair ->
                Pair.create(chair.barber()?.name, chair.customer())
            })
    }

    @Subscribe
    fun onEvent(event: ClockTick) {
        _clock.postValue(SimUtils.formatTime(event.time))
    }

    @Subscribe
    fun onEvent(@Suppress("unused") event: ProgramTermination) {
        _customerEnter.postValue(null)
        _customerExit.postValue(null)
    }
}