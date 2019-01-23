package com.mrbreak.todo.handlers;

import com.mrbreak.todo.events.AddEditToDoStarted;
import com.mrbreak.todo.events.DeleteToDoStarted;
import com.mrbreak.todo.events.DisplayFragmentStarted;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.jobs.AddEditToDoJob;
import com.mrbreak.todo.jobs.DeleteToDoJob;
import com.mrbreak.todo.jobs.DisplayFragmentJob;
import com.mrbreak.todo.jobs.GetToDoJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ToDoEventHandler extends EventBus {

    @Subscribe
    public void onEvent(DisplayFragmentStarted e) {
        new DisplayFragmentJob(e.getEventBus(), e.getFragment());
    }

    @Subscribe
    public void onEvent(GetToDosStarted e) {
        new GetToDoJob(e.getEventBus());
    }

    @Subscribe
    public void onEvent(AddEditToDoStarted e) {
        new AddEditToDoJob(e.getEventBus(), e.getToDo());
    }

    @Subscribe
    public void onEvent(DeleteToDoStarted e) {
         new DeleteToDoJob(e.getEventBus(), e.getToDo());
    }
}
