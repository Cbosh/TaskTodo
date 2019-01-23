package com.mrbreak.todo.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.mrbreak.todo.events.DeleteToDoFinished;
import com.mrbreak.todo.model.ToDo;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeleteToDoJob extends Job {
    private static final int PRIORITY = 1;
    private EventBus eventBus;
    private ToDo toDo;

    public DeleteToDoJob(EventBus eventBus, ToDo toDo) {
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
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm mRealm) {
                    RealmResults<ToDo> result = realm.where(ToDo.class).equalTo("id", toDo.getId()).findAll();
                    result.deleteAllFromRealm();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        eventBus.post(new DeleteToDoFinished(String.valueOf(toDo.getId())));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
