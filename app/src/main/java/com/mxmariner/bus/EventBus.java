package com.mxmariner.bus;

import com.squareup.otto.Bus;

public class EventBus extends Bus {
    private static EventBus ourInstance = new EventBus();

    public static EventBus getInstance() {
        return ourInstance;
    }

    private EventBus() {
    }
}
