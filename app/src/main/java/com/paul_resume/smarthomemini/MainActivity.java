package com.paul_resume.smarthomemini;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paul_resume.smarthomemini.services.MqttService;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppSettings settings = new AppSettings(this);

        /**
         * Check if service is already running
         * if not than start the service
         */
        if (settings.getFirstRun()) {
            if (!isServiceRunning(MqttService.class.getName().toString())) {
                Intent service = new Intent(MainActivity.this, MqttService.class);
                startService(service);
                settings.setFirstRun(false);
                settings.commit(false);
            }
        }

        /**
         * Get all views
         */
        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button)findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);

        /**
         * Set onClick event listener
         */
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settings.getMessage1().isEmpty()) {
                    Intent i = new Intent(MqttService.ACTION_PUBLISH);
                    i.putExtra(MqttService.EXTRA_MESSAGE, settings.getMessage1());
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(i);
                    Toast.makeText(MainActivity.this, "Sending message", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No messegae set for this button", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settings.getMessage2().isEmpty()) {
                    Intent i = new Intent(MqttService.ACTION_PUBLISH);
                    i.putExtra(MqttService.EXTRA_MESSAGE, settings.getMessage2());
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(i);
                    Toast.makeText(MainActivity.this, "Sending message", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No messegae set for this button", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settings.getMessage3().isEmpty()) {
                    Intent i = new Intent(MqttService.ACTION_PUBLISH);
                    i.putExtra(MqttService.EXTRA_MESSAGE, settings.getMessage3());
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(i);
                    Toast.makeText(MainActivity.this, "Sending message", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No messegae set for this button", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Register Broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(new ToastReceiver(),
                new IntentFilter(MqttService.ACTION_TOAST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_start_service) {
            Intent i = new Intent(MainActivity.this, MqttService.class);
            startService(i);
        }

        if (id == R.id.action_stop_service) {
            Intent i = new Intent(MainActivity.this, MqttService.class);
            stopService(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if service is running
     *
     * @param serviceName
     * @return
     */
    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getClass().getName().equals(serviceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Broadcast Event
     * */
    public void sendBroadcast(String action , String message){
        Intent intent = new Intent(action);
        intent.putExtra(MqttService.EXTRA_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class ToastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, intent.getStringExtra(MqttService.EXTRA_MESSAGE),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
