package com.example.dominik.trainings;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;

/**
 * Created by Dominik on 2015-10-13.
 */

@EService
public class TrainingService extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    private boolean recording = false;

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private boolean paused = false;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private final IBinder mBinder = new LocalBinder();
    Training training;

    DbHelper database;

    @Pref
    MyPrefs_ myPrefs;

    private int heartRate;
    private BluetoothDeviceConnection bluetoothDeviceConnection;

    private BluetoothBroadcastReceiver bbr = new BluetoothBroadcastReceiver();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

        database = new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, 1);

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext(), this, this).addApi(LocationServices.API).build();

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        googleApiClient.connect();
        bluetoothDeviceConnection = new BluetoothDeviceConnection(myPrefs);
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            bluetoothDeviceConnection.connect();
        }

        registerReceiver(bbr, new IntentFilter() {
            {
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(myPrefs.locationUpdates().get());
        locationRequest.setFastestInterval(myPrefs.locationUpdates().get());
        locationRequest.setPriority(myPrefs.accuracy().get());
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (recording && !paused) {
            if (bluetoothDeviceConnection.getHeartRateMonitor() != null) {
                heartRate = bluetoothDeviceConnection.getHeartRateMonitor().getHeartRate().get();
                saveLocationAndHeartRateToDB(heartRate, location);
            } else {
                saveLocationToDB(location);
            }
        }
    }

    private void saveLocationToDB(Location location) {
        database.insertPoint(location, training.getId());
    }

    private void saveLocationAndHeartRateToDB(int heartRate, Location location) {
        database.insertPoint(location, heartRate, training.getId());
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRecording() {
        return recording;
    }

    public void finishTraining() {

        recording = false;
        paused = false;
        googleApiClient.disconnect();
        stopSelf();
        training.setStopDate(new Date());
        database.updateTraining(training);

        startActivity(new Intent(getApplicationContext(), TrainingFinishOverview.class) {
            {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });

    }

    public void removeListener() {
        unregisterReceiver(bbr);
    }

    public void startRecording() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        training = new Training();
        long id = database.insertTraining(training);
        training.setId(id);
        recording = true;
        paused = false;
    }

    public void makeBackground() {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        startForeground(1, mBuilder.build());
    }

    public void stopBackground() {
        stopForeground(true);
    }


    public class LocalBinder extends Binder {
        TrainingService getService() {
            return TrainingService.this;
        }
    }

    @Override
    public void onDestroy() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        unregisterReceiver(bbr);
        super.onDestroy();
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    bluetoothDeviceConnection.setBluetoothState(BluetoothAdapter.STATE_ON);
                    bluetoothDeviceConnection.connect();
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_OFF) {
                    bluetoothDeviceConnection.setBluetoothState(BluetoothAdapter.STATE_OFF);
                    bluetoothDeviceConnection.reset();
                }
            } else if (action.equals(BluetoothAdapter.STATE_DISCONNECTED)) {
                bluetoothDeviceConnection.reset();
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                bluetoothDeviceConnection.setDeviceState(BluetoothDevice.ACTION_ACL_CONNECTED);
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                bluetoothDeviceConnection.setDeviceState(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                bluetoothDeviceConnection.reset();
            }

        }
    }
}
