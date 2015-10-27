package com.example.dominik.trainings;

import java.util.Date;
import java.util.List;

/**
 * Created by Dominik on 2015-10-23.
 */
public class Training {

    long id;
    String name;
    String description;
    List<MovePoint> movePointList;
    double duration;
    double speed;
    double distance;
    double activeTime;
    Date startDate;
    Date stopDate;
    String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<MovePoint> getMovePointList() {
        return movePointList;
    }

    public void setMovePointList(List<MovePoint> movePointList) {this.movePointList = movePointList;}
}
