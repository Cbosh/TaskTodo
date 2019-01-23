package com.mrbreak.todo.events;

import org.greenrobot.eventbus.EventBus;

public class DeleteToDoFinished extends EventBus {
    private String id;

    public DeleteToDoFinished(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
