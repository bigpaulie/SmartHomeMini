package com.paul_resume.smarthomemini.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.paul_resume.smarthomemini.services.MqttService;

/**
 * Created by paul on 29.05.2015.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NetworkReceiver onReceive()");
        Intent intent1 = new Intent(MqttService.ACTIION_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
    }
}
