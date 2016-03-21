package com.example.dominik.trainings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @ViewById(R.id.send)
    Button start;

    @ViewById(R.id.trainingList)
    Button trainingList;

    @ViewById(R.id.longitude)
    TextView longitude;

    @ViewById(R.id.latitude)
    TextView latitude;

    @ViewById(R.id.heartRateView)
    TextView heartRateView;

    @ViewById(R.id.gps_status)
    TextView gpsStatus;

    @ViewById(R.id.diff_value)
    TextView diffValue;

    @ViewById(R.id.accuracy_value)
    TextView accuracyValue;

    @Pref
    MyPrefs_ myPrefs;

    private LocalBinder mBinder;

    private TrainingService myService;

    private ServiceConnection serviceConnection;

    private BluetoothBroadcastReceiver bluetoothReceiver;

    private boolean isBluetoothReceiverRegistered = false;
    private int index = 2;

    @Click(R.id.options)
    void options() {
        startActivity(new Intent(MainActivity.this, SettingsActivity_.class));
    }

    @Click(R.id.startStop)
    void startStop() {
        if (!myService.isRecording()) {
            myService.startRecording();
            startStop.setText("STOP");
        }
        else if (myService.isPaused()){
            myService.setPaused(false);
            startStop.setText("STOP");
        }
        else {
            myService.setPaused(true);
            startStop.setText("RESUME");
        }
    }

    @Click(R.id.trainingList)
    void trainingList() {
        startActivity(new Intent(MainActivity.this, TrainingListActivity_.class));
    }

    @Click(R.id.finishTraining)
    void finishTraining() {
        myService.finishTraining();
        stopService(new Intent("com.example.dominik.trainings.TrainingService_"));
        unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trainingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
            }
        });
        intent =  TrainingService_.intent(getApplication()).get();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (LocalBinder) service;
                myService = mBinder.getService();
                myService.setMainActivity(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        startService(intent);
        bindService(intent, serviceConnection, BIND_ALLOW_OOM_MANAGEMENT);
        bluetoothReceiver = new BluetoothBroadcastReceiver();
        registerReceiver(bluetoothReceiver, new IntentFilter() {{
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        }
        });
        isBluetoothReceiverRegistered = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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
                resetUI();
                startService(intent);
                bindService(intent, serviceConnection, BIND_ALLOW_OOM_MANAGEMENT);
                registerReceiver(bluetoothReceiver, new IntentFilter() {{
                    addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                    addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                }
                });
                isBluetoothReceiverRegistered = true;
                MyApplication.bluetoothConnection.connect();
            }
        }
        else {
            startService(intent);
            bindService(intent, serviceConnection, BIND_ALLOW_OOM_MANAGEMENT);
        }
    }

    private void resetUI() {
        startStop.setText("START");
    }

    @Override
    protected void onPause() {
        if (myService != null) {
            if (myService.isRecording()) {
                myService.makeBackground();
            } else {
                if (isBluetoothReceiverRegistered) {
                    unregisterReceiver(bluetoothReceiver);
                    isBluetoothReceiverRegistered = false;
//                    MyApplication.bluetoothConnection.reset();
                }
            }
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        stopService(new Intent("com.example.dominik.trainings.TrainingService_"));
        unbindService(serviceConnection);
        if (isBluetoothReceiverRegistered) {
            unregisterReceiver(bluetoothReceiver);
        }
        super.onDestroy();
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    MyApplication.bluetoothConnection.reset();
                    MyApplication.bluetoothConnection.connect();
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_OFF) {
                    MyApplication.bluetoothConnection.reset();
                }
            } else if (action.equals(BluetoothAdapter.STATE_DISCONNECTED)) {
                MyApplication.bluetoothConnection.reset();
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                MyApplication.bluetoothConnection.reset();
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                MyApplication.bluetoothConnection.connect();
            }
        }
    }

}
