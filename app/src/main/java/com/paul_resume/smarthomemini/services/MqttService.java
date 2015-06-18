package com.paul_resume.smarthomemini.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.paul_resume.smarthomemini.AppSettings;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by paul on 29.05.2015.
 */
public class MqttService extends Service implements MqttCallback {

    public static final String TAG = MqttService.class.getName();
    public static final String ACTIION_NETWORK_CHANGE = MqttService.class.getName() + "Action.Network",
            ACTION_PUBLISH = MqttService.class.getName() + "Action.Publish",
            ACTION_PING = MqttService.class.getName() + "Action.Ping",
            ACTION_SETTINGS_CHANGE = MqttService.class.getName() + "Action.SettingsChange",
            ACTION_TOAST = MqttService.class.getName() + "Action.Toast",
            EXTRA_MESSAGE = MqttService.class.getName() + "Extra.Message";

    private static final int KEEP_ALIVE = 20 * 60;

    MqttClient client = null;
    MqttConnectOptions options = null;
    AppSettings settings = null;
    PowerManager powerManager = null;
    AlarmManager alarmManager = null;
    private PowerManager.WakeLock wakeLock = null;

    /**
     * Ping intent keep connection alive
     *
     * @param context
     * @return
     */
    public static Intent pingIntent(Context context) {
        Intent intent = new Intent(context, MqttService.class);
        intent.setAction(ACTION_PING);
        return intent;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        /**
         * Get the user defined settings
         */
        settings = new AppSettings(getApplicationContext());

        /**
         * Attempt to connect to the MQTT Server
         */
        serviceConnect();

        /**
         * Register Broadcast Receivers
         */
        registerReceiver(new NetworkChangeReceiver(), ACTIION_NETWORK_CHANGE);
        registerReceiver(new PublishReceiver(), ACTION_PUBLISH);
        registerReceiver(new SettingsReceiver(), ACTION_SETTINGS_CHANGE);
    }

    /**
     * Register Broadcast receivers
     *
     * @param receiver
     * @param Filter
     */
    public void registerReceiver(BroadcastReceiver receiver, String Filter) {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,
                new IntentFilter(Filter));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        if (intent != null || intent.getAction().equals(ACTION_PING)) {
            if (client == null || !client.isConnected()) {
                serviceConnect();
            } else {
                try {
                    client.publish(settings.getTopic(), new MqttMessage("PING".getBytes()));
                } catch (MqttPersistenceException e) {
                    e.printStackTrace();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, MqttService.pingIntent(this),
                PendingIntent.FLAG_NO_CREATE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

        wakeLock.release();
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
        /**
         * Wake the CPU and reconnect to the MQTT Server
         * !!! release the wake look asap !.
         */
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
        serviceConnect();
        wakeLock.release();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        Log.d(TAG, topic);
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle("Smart Home Mini Alert");
            builder.setContentText(mqttMessage.toString());

            int notificationID = 001;
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(notificationID, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean isDeviceConnected() {
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
                        client.subscribe(settings.getTopic());
                        client.subscribe("/paul/alerts");
                    } else {
                        options.setKeepAliveInterval(KEEP_ALIVE);
                        client.setCallback(this);
                        client.connect(options);
                        client.subscribe(settings.getTopic());
                        client.subscribe("/paul/alerts");
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

    public class PublishReceiver extends BroadcastReceiver {
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

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Network Change Receiver");
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire();
            if (isDeviceConnected()) {
                serviceDisconnect();
                serviceConnect();
            } else {
                serviceDisconnect();
            }
            wakeLock.release();
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
