package com.mrbreak.todo.events;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public class DisplayFragmentFinished extends EventBus {
    private Fragment fragment;

    public DisplayFragmentFinished(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
