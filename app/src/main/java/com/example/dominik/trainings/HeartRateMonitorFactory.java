package com.example.dominik.trainings;

/**
 * Created by Dominik on 2015-10-13.
 */
public class HeartRateMonitorFactory {

    public static HeartRateMonitor getHeartRateMonitor(String name) {
        if (name.equals("Polar iWL")) {
            return new PolarHeartRateMonitor();
        }
        else {
            return null;
        }
    }
}
