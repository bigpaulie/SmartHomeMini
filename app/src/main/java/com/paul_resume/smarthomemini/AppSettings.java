package com.paul_resume.smarthomemini;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.paul_resume.smarthomemini.services.MqttService;

/**
 * Created by paul on 29.05.2015.
 */
public class AppSettings {

    private static final String PACKAGE = "com.paul_resume.smarthomemini.settings",
            SETTING_MQTT_BROKER = "BROKER",
            SETTING_MQTT_PORT = "PORT",
            SETTING_MQTT_USER = "USER",
            SETTING_MQTT_PASS = "PASS",
            SETTING_MQTT_TOPIC = "TOPIC",
            SETTING_MQTT_MESSAGE1 = "MESSAGE1",
            SETTING_MQTT_MESSAGE2 = "MESSAGE2",
            SETTING_MQTT_MESSAGE3 = "MESSAGE3",
            SETTING_WIFI_PRESENCE_ENABLE = "WIFIPRESENCE",
            SETTING_WIFI_SSID = "WIFISSID",
            SETTING_WIFI_CONNECT = "WIFICONNECT",
            SETTING_WIFI_DISCONNECT = "WIFIDISCONNECT";

    Context context;
    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    public AppSettings(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getBroker() {
        return preferences.getString(SETTING_MQTT_BROKER, "");
    }

    public void setBroker(String broker) {
        editor.putString(SETTING_MQTT_BROKER, broker);
    }

    public Integer getPort() {
        return preferences.getInt(SETTING_MQTT_PORT, 1883);
    }

    public void setPort(int port) {
        editor.putInt(SETTING_MQTT_PORT, port);
    }

    public String getUser() {
        return preferences.getString(SETTING_MQTT_USER, "");
    }

    public void setUser(String user) {
        editor.putString(SETTING_MQTT_USER, user);
    }

    public String getPass() {
        return preferences.getString(SETTING_MQTT_PASS, "");
    }

    public void setPass(String pass) {
        editor.putString(SETTING_MQTT_PASS, pass);
    }

    public String getTopic() {
        return preferences.getString(SETTING_MQTT_TOPIC, "");
    }

    public void setTopic(String topic) {
        editor.putString(SETTING_MQTT_TOPIC, topic);
    }

    public String getMessage1() {
        return preferences.getString(SETTING_MQTT_MESSAGE1, "");
    }

    public void setMessage1(String message) {
        editor.putString(SETTING_MQTT_MESSAGE1, message);
    }

    public String getMessage2() {
        return preferences.getString(SETTING_MQTT_MESSAGE2, "");
    }

    public void setMessage2(String message) {
        editor.putString(SETTING_MQTT_MESSAGE2, message);
    }

    public String getMessage3() {
        return preferences.getString(SETTING_MQTT_MESSAGE3, "");
    }

    public void setMessage3(String message) {
        editor.putString(SETTING_MQTT_MESSAGE3, message);
    }

    public boolean getWifiPresence() {
        return preferences.getBoolean(SETTING_WIFI_PRESENCE_ENABLE, false);
    }

    public void setWifiPresence(boolean wifiPresence) {
        editor.putBoolean(SETTING_WIFI_PRESENCE_ENABLE, wifiPresence);
    }

    public String getWifiSSID() {
        return preferences.getString(SETTING_WIFI_SSID, "");
    }

    public void setWifiSSID(String ssid) {
        editor.putString(SETTING_WIFI_SSID, ssid);
    }

    public String getWifiConnectMessage() {
        return preferences.getString(SETTING_WIFI_CONNECT, "");
    }

    public void setWifiConnectMessage(String message) {
        editor.putString(SETTING_WIFI_CONNECT, message);
    }

    public String getWifiDisconnectMessage() {
        return preferences.getString(SETTING_WIFI_DISCONNECT, "");
    }

    public void setWifiDisconnectMessage(String message) {
        editor.putString(SETTING_WIFI_DISCONNECT, message);
    }

    /**
     * Commit settings
     *
     * @param showToast boolean
     */
    public void commit(boolean showToast) {
        editor.commit();
        sendBroadcast();
        // Show a toast message indicating that the settings are beeing saved
        if (showToast) {
            Toast.makeText(context, "Savaing settings ...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Broadcast settings change event
     */
    public void sendBroadcast() {
        Intent intent = new Intent(MqttService.ACTION_SETTINGS_CHANGE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
