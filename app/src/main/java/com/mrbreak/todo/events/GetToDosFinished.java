package com.mrbreak.todo.events;

import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class GetToDosFinished extends EventBus {
    private List<ToDo> toDoList;

    public GetToDosFinished(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    public List<ToDo> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }
}
