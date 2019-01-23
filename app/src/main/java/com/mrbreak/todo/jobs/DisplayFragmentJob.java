package com.mrbreak.todo.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.mrbreak.todo.events.DisplayFragmentFinished;

import org.greenrobot.eventbus.EventBus;

public class DisplayFragmentJob extends Job {
    private static final int PRIORITY = 1;
    private EventBus eventBus;
    private Fragment fragment;

    public DisplayFragmentJob(EventBus eventBus, Fragment fragment) {
        super(new Params(PRIORITY));
        this.eventBus = eventBus;
        this.fragment = fragment;
    }

    @Override
    public void onAdded() {

    }


    @Override
    public void onRun() throws Throwable {
        eventBus.post(new DisplayFragmentFinished(fragment));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
