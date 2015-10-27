package com.example.dominik.trainings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.dominik.trainings.TrainingService.LocalBinder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    Intent intent = null;

    @ViewById(R.id.startStop)
    Button startStop;

    @ViewById(R.id.trainingList)
    Button trainingList;

    @ViewById(R.id.longitude)
    TextView longitude;

    @ViewById(R.id.latitude)
    TextView latitude;

    @ViewById(R.id.heartRate)
    TextView heartRate;

    @ViewById(R.id.gps_status)
    TextView gpsStatus;

    @Pref
    MyPrefs_ myPrefs;

    IBinder.DeathRecipient dr = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            int ba = 10;
        }
    };

    private LocalBinder mBinder;

    private TrainingService myService;

    private ServiceConnection serviceConnection;

    @Click(R.id.options)
    void options() {
        startActivity(new Intent(MainActivity.this, SettingsActivity_.class));
    }

    @Click(R.id.startStop)
    void startStop() {
        if (!myService.isRecording()) {
            myService.startRecording();
        }
        else if (myService.isPaused()){
            myService.setPaused(false);
        }
        else {
            myService.setPaused(true);
        }
    }

    @Click(R.id.trainingList)
    void trainingList() {
        startActivity(new Intent(MainActivity.this, TrainingListActivity_.class));
        int a =5;
    }

    @Click(R.id.finishTraining)
    void finishTraining() {
        myService.finishTraining();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent =  TrainingService_.intent(getApplication()).get();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (LocalBinder) service;
                myService = mBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        startService(intent);
        bindService(intent, serviceConnection, BIND_ALLOW_OOM_MANAGEMENT);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (myService != null) {
            if (myService.isRecording()) {
                myService.stopBackground();
            } else {
                startService(intent);
                bindService(intent, serviceConnection, BIND_ALLOW_OOM_MANAGEMENT);
            }
        }
    }

    @Override
    protected void onPause() {
        if (myService.isRecording()) {
            myService.makeBackground();
        } else {
            stopService(new Intent("com.example.dominik.trainings.TrainingService_"));
            unbindService(serviceConnection);
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
