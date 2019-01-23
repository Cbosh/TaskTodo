package com.mrbreak.todo.events;

import org.greenrobot.eventbus.EventBus;

public class GetToDosStarted extends EventBus {
    private EventBus eventBus;

    public GetToDosStarted(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
