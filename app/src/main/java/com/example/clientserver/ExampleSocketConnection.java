package com.example.clientserver;

/**
 * Created by Константин on 29.01.2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.prefs.Preferences;


public class ExampleSocketConnection implements ClientWebSocket.MessageListener {
    private ClientWebSocket clientWebSocket;
    private Context context;
    public Gson gson = new Gson();
    private Handler socketConnectionHandler;

    private Runnable checkConnectionRunnable = () -> {
        if (!clientWebSocket.getConnection().isOpen()) {
            openConnection();
        }
        startCheckConnection();
    };

    private void startCheckConnection() {
        socketConnectionHandler.postDelayed(checkConnectionRunnable, 5000);
    }

    private void stopCheckConnection() {
        socketConnectionHandler.removeCallbacks(checkConnectionRunnable);
    }

    public ExampleSocketConnection(Context context) {
        this.context = context;
        socketConnectionHandler = new Handler();
    }

    public void openConnection() {
       // if (!Preferences.getManager().isAuth()) {
        String url="ws://api.parad:8000/notice?token=";
        String token="SDWwAe7N5N35OIA1QMDzv0fA6Dw7ZvLZ_1513852454";
        if (!isAuth()) {
            Log.i("Websocket", "Error: User is not authorize");
            return;
        }
        if (clientWebSocket != null) clientWebSocket.close();
        try {
            clientWebSocket = new ClientWebSocket(this,
                    url+token);
            clientWebSocket.connect();
            Log.i("Websocket", "Socket connected by user " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
       // initScreenStateListener();
        startCheckConnection();
    }

    public void closeConnection() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
            clientWebSocket = null;
        }
        releaseScreenStateListener();
        stopCheckConnection();
    }


    @Override
    public void onSocketMessage(String message) {
        EventBus.getDefault().post(gson.fromJson(message, RealTimeEvent.class));
    }

    /**
     * Screen state listener for socket live cycle
     */
    private void initScreenStateListener() {
        context.registerReceiver(screenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        context.registerReceiver(screenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private void releaseScreenStateListener() {
        try {
            context.unregisterReceiver(screenStateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("Websocket", "Screen ON");
                openConnection();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("Websocket", "Screen OFF");
                closeConnection();
            }
        }
    };

    public boolean isConnected() {
        return clientWebSocket != null &&
                clientWebSocket.getConnection() != null &&
                clientWebSocket.getConnection().isOpen();
    }

    private boolean isAuth(){
        return true;
    }
}