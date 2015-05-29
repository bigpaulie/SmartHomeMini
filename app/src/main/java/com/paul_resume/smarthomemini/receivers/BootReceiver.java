package com.paul_resume.smarthomemini.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paul_resume.smarthomemini.services.MqttService;

/**
 * Created by paul on 29.05.2015.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MqttService.class);
        context.startService(intent1);
    }
}
