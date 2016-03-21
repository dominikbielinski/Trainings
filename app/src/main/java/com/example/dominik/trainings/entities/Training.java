package com.example.dominik.trainings.entities;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Dominik on 2015-10-23.
 */
public class Training extends RealmObject {

    @PrimaryKey
    private int id;

    private int remoteId;
    private int activityId;
    private String name;
    private String description;
    private double duration;
    private double speed;
    private double distance;
    private double activeTime;
    private Date startDate;
    private Date stopDate;
    private int synced;
    private RealmList<Movepoint> movepoints;

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public double getActiveTime() {return activeTime;}

    public void setActiveTime(double activeTime) {
        this.activeTime = activeTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public RealmList<Movepoint> getMovepoints() {
        return movepoints;
    }

    public void setMovepoints(RealmList<Movepoint> movepoints) {
        this.movepoints = movepoints;
    }
}
