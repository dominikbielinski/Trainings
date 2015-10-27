package com.example.dominik.trainings;

/**
 * Created by Dominik on 2015-09-02.
 */
public class PolarHeartRateMonitor extends HeartRateMonitor {

    public PolarHeartRateMonitor() {

    }

    @Override
    public boolean packetValid (byte[] buffer, int i) {
        boolean headerValid = (buffer[i] & 0xFF) == 0xFE;
        boolean checkbyteValid = (buffer[i + 2] & 0xFF) == (0xFF - (buffer[i + 1] & 0xFF));
        boolean sequenceValid = (buffer[i + 3] & 0xFF) < 16;

        return headerValid && checkbyteValid && sequenceValid;
    }


}
