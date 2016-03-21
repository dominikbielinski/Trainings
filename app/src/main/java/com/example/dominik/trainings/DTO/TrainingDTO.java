package com.example.dominik.trainings.DTO;

import com.example.dominik.trainings.entities.Training;

import java.util.Date;
import java.util.List;

/**
 * Created by Dominik on 2016-02-09.
 */
public class TrainingDTO {

    int id, remoteId, activityid;
    String name;
    String description;
    double duration;
    double speed;
    double distance;
    double activeTime;
    Date startDate;
    Date stopDate;

    List<MovepointDTO> movepoints;

    public TrainingDTO(Training training, List<MovepointDTO> movepoints) {
        this.movepoints = movepoints;

        this.name = training.getName();
        this.description = training.getDescription();
        this.activeTime = training.getActiveTime();
        this.activityid = training.getActivityId();
        this.distance = training.getDistance();
        this.duration = training.getDuration();
        this.speed = training.getSpeed();
        this.startDate = training.getStartDate();
        this.stopDate = training.getStopDate();
        this.remoteId = training.getRemoteId();
        this.id = training.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public int getActivityid() {
        return activityid;
    }

    public void setActivityid(int activityid) {
        this.activityid = activityid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getActiveTime() {
        return activeTime;
    }

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
}
