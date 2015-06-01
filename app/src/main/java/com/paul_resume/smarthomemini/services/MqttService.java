package com.paul_resume.smarthomemini.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.paul_resume.smarthomemini.AppSettings;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by paul on 29.05.2015.
 */
public class MqttService extends Service implements MqttCallback{

    public static final String TAG = MqttService.class.getName();
    public static final String ACTIION_NETWORK_CHANGE = MqttService.class.getName() + "Action.Network",
            ACTION_PUBLISH = MqttService.class.getName() + "Action.Publish",
            ACTION_SETTINGS_CHANGE = MqttService.class.getName() + "Action.SettingsChange",
            ACTION_TOAST = MqttService.class.getName() + "Action.Toast",
            EXTRA_MESSAGE = MqttService.class.getName() + "Extra.Message";

    private static final int KEEP_ALIVE = 20 * 60;

    MqttClient client = null;
    MqttConnectOptions options = null;
    AppSettings settings = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        settings = new AppSettings(getApplicationContext());
        serviceConnect();

        /**
         * Register Broadcast Receivers
         */
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(new NetworkChangeReceiver(), new IntentFilter(ACTIION_NETWORK_CHANGE));

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(new PublishReceiver(), new IntentFilter(ACTION_PUBLISH));

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(new SettingsReceiver(), new IntentFilter(ACTION_SETTINGS_CHANGE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceDisconnect();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d(TAG, "Connection lost !");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, "Calling deliveryComplete()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getDeviceId() {
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public boolean isDeviceConnected(){
        Log.d(TAG, "Calling isDeviceConnected()");
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isConnected = false;
        try {
            isConnected = info != null && info.isConnected();
            if (isConnected) {
                Log.d(TAG, "Device is connected !");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    public void serviceConnect() {
        Log.d(TAG, "Calling serviceConnect()");
        try {
            if (isDeviceConnected()) {
                if (!settings.getBroker().isEmpty()) {
                    client = new MqttClient(settings.getBroker(), getDeviceId(),
                            new MemoryPersistence());

                    options = new MqttConnectOptions();

                    if (!settings.getUser().isEmpty() || !settings.getPass().isEmpty()) {
                        options.setUserName(settings.getUser());
                        options.setPassword(settings.getPass().toCharArray());
                        options.setKeepAliveInterval(KEEP_ALIVE);
                        client.setCallback(this);
                        client.connect(options);
                    } else {
                        options.setKeepAliveInterval(KEEP_ALIVE);
                        client.setCallback(this);
                        client.connect(options);
                    }

                    if (client.isConnected()) {
                        Log.d(TAG, "Connected to MQTT Service : " + settings.getBroker());
                    }

                } else {
                    Log.d(TAG, "Broker not set !");
                }
            } else {
                Log.d(TAG, "Device is not connected to the internet !");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void serviceDisconnect() {
        Log.d(TAG, "Calling serviceDisconnect()");
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public class PublishReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "PublishReceiver onReceive()");
            MqttMessage message = new MqttMessage(intent.getStringExtra(EXTRA_MESSAGE).getBytes());
            try {
                if (client != null && client.isConnected()) {
                    client.publish(settings.getTopic(), message);
                } else {
                    Log.d(TAG, "Client is not connected to " + settings.getBroker());
                    Intent i = new Intent(ACTION_TOAST);
                    i.putExtra(EXTRA_MESSAGE, "Client is not connected to MQTT Broker ...");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);

                }
            } catch (MqttException e) {
                e.printStackTrace();
                Log.d(TAG, e.getCause().toString());
            }
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public class DeliveryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public class ConnetionLostReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Network Change Receiver");
            if (isDeviceConnected()) {
                serviceDisconnect();
                serviceConnect();
            } else {
                serviceDisconnect();
            }
        }
    }

    public class SettingsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Settings Change Receiver");
            if (isDeviceConnected()) {
                serviceDisconnect();
                serviceConnect();
            } else {
                serviceDisconnect();
            }
        }
    }
}
