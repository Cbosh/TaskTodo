package com.mrbreak.todo.events;

import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AddEditToDoFinished extends EventBus {
    private List<ToDo> toDos;

    public AddEditToDoFinished(List<ToDo> toDos) {
        this.toDos = toDos;
    }

    public List<ToDo> getToDos() {
        return toDos;
    }

}
