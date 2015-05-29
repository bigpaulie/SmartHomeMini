package com.paul_resume.smarthomemini;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by paul on 29.05.2015.
 */
public class AppSettings {

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    private static final String PACKAGE = "com.paul_resume.smarthomemini.settings",
            SETTING_MQTT_BROKER = "BROKER",
            SETTING_MQTT_PORT = "PORT",
            SETTING_MQTT_USER = "USER",
            SETTING_MQTT_PASS = "PASS";

    public AppSettings(Context context) {
        preferences = context.getSharedPreferences(PACKAGE , Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setBroker(String broker){
        editor.putString(SETTING_MQTT_BROKER , broker);
    }

    public String getBroker(){
        return preferences.getString(SETTING_MQTT_BROKER , "");
    }

    public void setPort(int port){
        editor.putInt(SETTING_MQTT_PORT , port);
    }

    public Integer getPort(){
        return preferences.getInt(SETTING_MQTT_PORT , 1883);
    }

    public void setUser(String user){
        editor.putString(SETTING_MQTT_USER , user);
    }

    public String getUser(){
        return preferences.getString(SETTING_MQTT_USER , "");
    }

    public void setPass(String pass){
        editor.putString(SETTING_MQTT_PASS , pass);
    }

    public String getPass(){
        return preferences.getString(SETTING_MQTT_PASS , "");
    }

    public void commit(){
        editor.commit();
    }

}
