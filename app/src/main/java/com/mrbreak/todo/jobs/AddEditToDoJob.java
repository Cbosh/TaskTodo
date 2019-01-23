package com.mrbreak.todo.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.mrbreak.todo.events.AddEditToDoFinished;
import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;


import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AddEditToDoJob extends Job {
    private static final int PRIORITY = 1;
    private EventBus eventBus;
    private ToDo toDo;

    public AddEditToDoJob(EventBus eventBus, ToDo toDo) {
        super(new Params(PRIORITY));
        this.eventBus = eventBus;
        this.toDo = toDo;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (toDo.getId() == 0) {
                        Number currentIdNum = realm.where(ToDo.class).max("id");
                        int nextId;
                        if (currentIdNum == null) {
                            nextId = 1;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        toDo.setId(nextId);
                    }
                    realm.insertOrUpdate(toDo);
                    eventBus.post(new AddEditToDoFinished(getToDoList(toDo)));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ToDo> getToDoList(ToDo newToDo) {
        List<ToDo> toDos = Realm.getDefaultInstance().where(ToDo.class).findAll();
        List<ToDo> toDoList = new ArrayList<>();
        if (toDos != null && toDos.size() > 0) {
            for (ToDo currentToDo : toDos) {
                toDoList.add(assembleToDo(currentToDo));
            }
        }

        toDoList.add(assembleToDo(newToDo));

        return toDoList;
    }

    private ToDo assembleToDo(ToDo currentToDo) {
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
        return toDo;
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
