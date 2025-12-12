package com.barbershopsim.app

import android.util.Pair
import androidx.lifecycle.ViewModel
import com.barbershopsim.simulation.SimUtils
import com.barbershopsim.simulation.Simulation
import com.barbershopsim.simulation.eventlistener.EventListener
import com.barbershopsim.simulation.model.State
import com.barbershopsim.simulation.model.events.CustomEvent.ClockTick
import com.barbershopsim.simulation.model.events.CustomEvent.ProgramTermination
import com.barbershopsim.simulation.model.events.ShopEvent
import com.google.common.eventbus.Subscribe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel(), EventListener {
    private var simulation: Simulation? = null

    val shopName: String
        get() = State.SHOP_NAME

    val timescaleFactor: String
        get() = "1 real second = " + "%,d".format(TIMESCALE_FACTOR) + " simulated seconds"

    private val _isShopOpen = MutableStateFlow(false)
    val isShopOpen: StateFlow<Boolean> = _isShopOpen
    private val _customerEnter = MutableStateFlow<Int?>(null)
    val customerEnter: StateFlow<Int?> = _customerEnter
    private val _customerExit = MutableStateFlow<Int?>(null)
    val customerExit: StateFlow<Int?> = _customerExit
    private val _waitingArea = MutableStateFlow<List<Int>>(emptyList())
    val waitingArea: StateFlow<List<Int>> = _waitingArea
    private val _haircutArea = MutableStateFlow<List<Pair<String?, Int>>>(emptyList())
    val haircutArea: StateFlow<List<Pair<String?, Int>>> = _haircutArea
    private val _clock = MutableStateFlow("")
    val clock: StateFlow<String> = _clock

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
            is ShopEvent.ShopOpen -> _isShopOpen.value = true
            is ShopEvent.ShopClose -> _isShopOpen.value = false
            is ShopEvent.CustomerEnter -> _customerEnter.value = event.customer()
            is ShopEvent.CustomerExit -> _customerExit.value = event.customer()
            else -> {}
        }

        // Triggered on all shop events
        val state = simulation!!.state
        _waitingArea.value = state.waitingArea().customers()
        _haircutArea.value = state.chairs().list().map { chair ->
            Pair.create(chair.barber()?.name, chair.customer())
        }
    }

    @Subscribe
    fun onEvent(event: ClockTick) {
        _clock.value = SimUtils.formatTime(event.time)
    }

    @Subscribe
    fun onEvent(@Suppress("unused") event: ProgramTermination) {
        _customerEnter.value = null
        _customerExit.value = null
    }
}