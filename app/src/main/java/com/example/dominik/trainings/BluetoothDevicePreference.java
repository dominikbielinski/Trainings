package com.example.dominik.trainings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.Set;

/**
 * Created by Dominik on 2015-09-07.
 */

public class BluetoothDevicePreference extends ListPreference {

    public BluetoothDevicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
        if (pairedDevices.size() > 0) {
            CharSequence[] entries = new CharSequence[pairedDevices.size()];
            CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
            int i = 0;
            for (BluetoothDevice dev : pairedDevices) {
                entries[i] = dev.getName();
                entryValues[i] = dev.getAddress();
                i++;
            }
            setEntries(entries);
            setEntryValues(entries);
        }
    }

    public BluetoothDevicePreference(Context context) {
        this(context, null);
    }
}