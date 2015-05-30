package com.paul_resume.smarthomemini;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final AppSettings settings = new AppSettings(this);

        /**
         * Get all views
         */
        final EditText broker = (EditText) findViewById(R.id.txtBroker);
        final EditText username = (EditText) findViewById(R.id.txtUsername);
        final EditText password = (EditText) findViewById(R.id.txtPassword);
        final EditText topic = (EditText) findViewById(R.id.txtTopic);

        final EditText message1 = (EditText) findViewById(R.id.txtMessage1);
        final EditText message2 = (EditText) findViewById(R.id.txtMessage2);
        final EditText message3 = (EditText) findViewById(R.id.txtMessage3);

        EditText wifi_ssid = (EditText) findViewById(R.id.txtWifiSSID);
        EditText wifi_onconnect = (EditText) findViewById(R.id.txtWifiConnect);
        EditText wifi_ondisconnect = (EditText) findViewById(R.id.txtWifiDisconnect);

        Button save = (Button) findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setBroker(broker.getText().toString());
                settings.setUser(username.getText().toString());
                settings.setPass(password.getText().toString());
                settings.setTopic(topic.getText().toString());
                settings.setMessage1(message1.getText().toString());
                settings.setMessage2(message2.getText().toString());
                settings.setMessage3(message3.getText().toString());

                settings.commit();
            }
        });

        /**
         * Populate fields
         */

        if (!settings.getBroker().isEmpty()) {
            broker.setText(settings.getBroker());
        }

        if (!settings.getUser().isEmpty()) {
            username.setText(settings.getUser());
        }

        if (!settings.getPass().isEmpty()) {
            password.setText(settings.getPass());
        }

        if (!settings.getTopic().isEmpty()) {
            topic.setText(settings.getTopic());
        }

        if (!settings.getMessage1().isEmpty()) {
            message1.setText(settings.getMessage1());
        }

        if (!settings.getMessage2().isEmpty()) {
            message2.setText(settings.getMessage2());
        }

        if (!settings.getMessage3().isEmpty()) {
            message3.setText(settings.getMessage3());
        }

        // save settings

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
