package com.paul_resume.smarthomemini.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.paul_resume.smarthomemini.services.MqttService;

/**
 * Created by paul on 29.05.2015.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(MqttService.ACTIION_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
    }
}
