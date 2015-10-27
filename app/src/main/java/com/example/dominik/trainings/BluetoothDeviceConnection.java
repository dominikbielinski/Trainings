package com.example.dominik.trainings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dominik on 2015-09-07.
 */
public class BluetoothDeviceConnection {

    private HeartRateMonitor heartRateMonitor;
    private final MyPrefs_ userPrefs;

    private Connected connectedThread = null;

    private volatile String deviceState = BluetoothDevice.ACTION_ACL_DISCONNECTED;

    private byte[] buffer = new byte[16];
    private Connect connectThread = null;

    public void setBluetoothState(int bluetoothState) {
        this.bluetoothState.set(bluetoothState);
    }

    private AtomicInteger bluetoothState = new AtomicInteger(BluetoothAdapter.STATE_DISCONNECTED);

    BluetoothDeviceConnection(MyPrefs_ userPrefs) {

        this.userPrefs = userPrefs;

    }

    public void connect() {
        connectThread = new Connect();
        connectThread.start();
    }


    private class Connect extends Thread {

        @Override
        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                if (bluetoothDevice.getName().equals(userPrefs.device().get())) {
                    try {
                        BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        bluetoothSocket.connect();
                        connectedThread = new Connected(bluetoothSocket);
                        connectedThread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public HeartRateMonitor getHeartRateMonitor() {
        return heartRateMonitor;
    }

    public void reset() {
        if (connectedThread != null) {
            connectedThread.interrupt();
        }
        if (connectThread != null) {
        connectThread.interrupt();
        }

        heartRateMonitor = null;
    }


    private class Connected extends Thread {

        private BluetoothSocket bluetoothSocket;

        public Connected(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted())
                while (bluetoothState.get() == BluetoothAdapter.STATE_ON) {
                    while (deviceState.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {

                        if (heartRateMonitor == null) {
                            heartRateMonitor = HeartRateMonitorFactory.getHeartRateMonitor(userPrefs.device().get());
                        }

                        try {
                            int readBytes = this.bluetoothSocket.getInputStream().read(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        boolean heartRateValid;

                        for (int i = 0; i < buffer.length - 8; i++) {
                            heartRateValid = heartRateMonitor.packetValid(buffer, i);
                            if (heartRateValid) {
                                heartRateMonitor.setHeartRate(buffer[i + 5] & 0xFF);
                                break;
                            }
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }
}
