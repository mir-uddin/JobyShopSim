package com.barbershopsim.eventlistener;

import com.barbershopsim.model.events.ShopEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Use this and a similar implementation to update the UI.
 */
public class EventOutputter implements EventListener {

    @Subscribe
    public void onEvent(ShopEvent event) {
        System.out.println(event);
    }
}
