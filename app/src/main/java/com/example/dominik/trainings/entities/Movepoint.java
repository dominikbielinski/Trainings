package com.example.dominik.trainings.entities;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Dominik on 2015-10-23.
 */
public class Movepoint extends RealmObject {

    @PrimaryKey
    private int id;

    private Training training;
    private double latitude,longitude,altitude;
    private Date datetime;
    private HeartRatePoint heartRatePoint;
    private int synced;

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

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

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public HeartRatePoint getHeartRatePoint() {
        return heartRatePoint;
    }

    public void setHeartRatePoint(HeartRatePoint heartRatePoint) {
        this.heartRatePoint = heartRatePoint;
    }

    public Movepoint() {}

    public Movepoint(int id, double latitude, double longitude, double altitude, Date datetime, HeartRatePoint heartRatePoint, int synced, Training training) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.datetime = datetime;
        this.heartRatePoint = heartRatePoint;
        this.synced = synced;
        this.training = training;
    }


}
