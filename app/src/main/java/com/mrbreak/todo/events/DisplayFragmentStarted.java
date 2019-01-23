package com.mrbreak.todo.events;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public class DisplayFragmentStarted extends EventBus {
    private Fragment fragment;
    private EventBus eventBus;

    public DisplayFragmentStarted(EventBus eventBus, Fragment fragment) {
        this.fragment = fragment;
        this.eventBus = eventBus;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
