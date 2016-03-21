package com.example.dominik.trainings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.dominik.trainings.entities.HeartRatePoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Dominik on 2015-09-07.
 */
public class BluetoothDeviceConnection  {

    private HeartRateMonitor heartRateMonitor;
    private static BluetoothDeviceConnection instance;
    private MyPrefs_ userPrefs;

    private Connected connectedThread = null;
    private byte[] buffer = new byte[16];
    private Connect connectThread = null;
    private Context context;
    private List<HeartRatePoint> heartRatePoints = new ArrayList<>();
    private BluetoothSocket bluetoothSocket;

    public void setUserPrefs(MyPrefs_ userPrefs) {
        this.userPrefs = userPrefs;
    }


    private BluetoothDeviceConnection() {}

    public static BluetoothDeviceConnection get() {
        if (instance == null) {
            instance = new BluetoothDeviceConnection();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HeartRatePoint getLatestHeartRatePoint() {
        if (heartRatePoints.size() != 0) {
            return heartRatePoints.get(heartRatePoints.size() - 1);
        }
        else return null;
    }

    public void connect() {
        if (connectThread == null) {
            connectThread = new Connect();
            connectThread.start();
        }
    }
    private class Connect extends Thread {

        @Override
        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                if (bluetoothDevice.getName().equals(userPrefs.device().get())) {
                    try {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        bluetoothSocket.connect();
                        connectedThread = new Connected();
                        connectedThread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void reset() {
        if (connectedThread != null) {
            connectedThread.interrupt();
            connectedThread = null;
        }
        if (connectThread != null) {
            connectThread.interrupt();
            connectThread = null;
        }
        heartRateMonitor = null;
        bluetoothSocket = null;
    }


    private class Connected extends Thread {

        public Connected() {
        }

        @Override
        public void run() {

            if (heartRateMonitor == null) {
                heartRateMonitor = HeartRateMonitorFactory.getHeartRateMonitor(userPrefs.device().get());
            }

            while (!Thread.currentThread().isInterrupted()) {

                int heartRate = getHeartRate(heartRateMonitor);
                if (heartRate != 0) {
                    heartRatePoints.add(new HeartRatePoint(MyApplication.getNewHeartRatePointId(), heartRate, new Date()));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        private int getHeartRate(HeartRateMonitor heartRateMonitor) {
            if (heartRateMonitor instanceof PolarHeartRateMonitor) {
                try {
                    if (bluetoothSocket.isConnected()) {
                        int readBytes = bluetoothSocket.getInputStream().read(buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean heartRateValid;
                for (int i = 0; i < buffer.length - 8; i++) {
                    heartRateValid = heartRateMonitor.packetValid(buffer, i);
                    if (heartRateValid) {
                        return buffer[i + 5] & 0xFF;
                    }
                }
            }
            return 0;
        }
    }

    public void resetHeartRatePoints() {
        heartRatePoints = new ArrayList<>();
    }
}
