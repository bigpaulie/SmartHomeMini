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
        registerReceiver(new NetworkChangeBroadcast(),
                new IntentFilter(ACTIION_NETWORK_CHANGE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isDeviceConnected(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isConnected = false;
        try {
            isConnected = info != null && info.isConnected();
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
                    client = new MqttClient(settings.getBroker(), Settings.Secure.ANDROID_ID,
                            new MemoryPersistence());

                    options = new MqttConnectOptions();

                    if (!settings.getUser().isEmpty() || !settings.getPass().isEmpty()) {
                        options.setUserName(settings.getUser());
                        options.setPassword(settings.getPass().toCharArray());
                        options.setKeepAliveInterval(20 * 60);
                        client.connect(options);
                    } else {
                        options.setKeepAliveInterval(KEEP_ALIVE);
                        client.connect(options);
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

    public class NetworkChangeBroadcast extends BroadcastReceiver {
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
}
