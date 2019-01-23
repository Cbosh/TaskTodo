package com.mrbreak.todo.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class GetToDoJob extends Job {
    private static final int PRIORITY = 1;
    private EventBus eventBus;

    public GetToDoJob(EventBus eventBus) {
        super(new Params(PRIORITY));
        this.eventBus = eventBus;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        List<ToDo> toDos = new ArrayList<>();
        List<ToDo> toDoList = new ArrayList<>();
        try {
            toDos = Realm.getDefaultInstance().where(ToDo.class).findAll();
            if (toDos != null && toDos.size() > 0) {
                for (ToDo currentToDo : toDos) {
                    ToDo toDo = new ToDo();
                    toDo.setCreatedDate(currentToDo.getCreatedDate());
                    toDo.setContent(currentToDo.getContent());
                    toDo.setDueDate(currentToDo.getDueDate());
                    toDo.setId(currentToDo.getId());
                    toDo.setCategory(currentToDo.getCategory());
                    toDo.setStartTime(currentToDo.getStartTime());
                    toDo.setPriority(currentToDo.getPriority());
                    toDo.setDone(currentToDo.isDone());
                    toDo.setEndTime(currentToDo.getEndTime());
                    toDo.setCompletedDate(currentToDo.getCompletedDate());
                    toDo.setRemindMeBefore(currentToDo.getRemindMeBefore());
                    toDoList.add(toDo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(toDoList);

        eventBus.post(new GetToDosFinished(toDoList));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
                                                     int maxRunCount) {
        return null;
    }
}
