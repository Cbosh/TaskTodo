package com.mrbreak.todo.events;

import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;

public class AddEditToDoStarted extends EventBus {
    private ToDo toDo;
    private EventBus eventBus;

    public AddEditToDoStarted(EventBus eventBus, ToDo toDo) {
        this.toDo = toDo;
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ToDo getToDo() {
        return toDo;
    }

}
