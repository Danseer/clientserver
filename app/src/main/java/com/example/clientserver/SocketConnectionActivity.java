package com.example.clientserver;

/**
 * Created by Константин on 29.01.2018.
 */


import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public abstract class SocketConnectionActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void handleRealTimeMessage(RealTimeEvent event) {
        // processing of all real-time events
    }
}