package com.example.dominik.trainings;

import java.util.Date;

/**
 * Created by Dominik on 2015-10-23.
 */
public class MovePoint {

    double latitude,longitude,altitude;
    Date dateTime;
    int heartRate;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public MovePoint(int heartRate, Date dateTime, double longitude, double latitude, double altitude) {
        this.heartRate = heartRate;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
}
