package com.example.dominik.trainings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dominik on 2015-09-03.
 */
public abstract class HeartRateMonitor  {

    public AtomicInteger heartRate = new AtomicInteger(0);

    public AtomicInteger getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate.set(heartRate);
    }

    public abstract boolean packetValid(byte[] buffer, int i);

}
