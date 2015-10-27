package com.example.dominik.trainings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dominik on 2015-10-20.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "trainings";

    private static final String TRAINING_TABLE_NAME = "training";
    private static final String TRAINING_COLUMN_ID = "id";
    private static final String TRAINING_COLUMN_STARTDATE = "startdate";
    private static final String TRAINING_COLUMN_STOPDATE = "stopdate";
    private static final String TRAINING_COLUMN_ACTIVETIME = "activetime";
    private static final String TRAINING_COLUMN_DISTANCE = "distance";
    private static final String TRAINING_COLUMN_SPEED = "speed";
    private static final String TRAINING_COLUMN_TYPE = "type";
    private static final String TRAINING_COLUMN_DESCRIPTION = "description";

    private static final int TRAINING_COLUMN_STARTDATE_INDEX = 1;
    private static final int TRAINING_COLUMN_STOPDATE_INDEX = 2;
    private static final int TRAINING_COLUMN_ACTIVETIME_INDEX = 3;
    private static final int TRAINING_COLUMN_DISTANCE_INDEX = 4;
    private static final int TRAINING_COLUMN_SPEED_INDEX = 5;
    private static final int TRAINING_COLUMN_TYPE_INDEX = 6;
    private static final int TRAINING_COLUMN_DESCRIPTION_INDEX = 7;

    private static final int POINT_COLUMN_LONGITUDE_INDEX = 1;
    private static final int POINT_COLUMN_LATITUDE_INDEX = 2;
    private static final int POINT_COLUMN_ALTITUDE_INDEX = 3;
    private static final int POINT_COLUMN_HEARTRATE_INDEX = 4;
    private static final int POINT_COLUMN_DATETIME_INDEX = 5;

    public static String POINT_TABLE_NAME = "point";
    public static String POINT_COLUMN_LONGITUDE = "longitude";
    public static String POINT_COLUMN_TRAINING = "trainingId";
    public static String POINT_COLUMN_LATITUDE = "latitude";
    public static String POINT_COLUMN_ALTITUDE = "altitude";
    public static String POINT_COLUMN_HEARTRATE = "heartrate";
    public static String POINT_COLUMN_DATETIME = "datetime";
    public static String POINT_COLUMN_ID = "id";


    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists " + POINT_TABLE_NAME +
                        "(" + POINT_COLUMN_ID + " integer primary key," +
                        POINT_COLUMN_LONGITUDE + " text," +
                        POINT_COLUMN_LATITUDE + " text," +
                        POINT_COLUMN_ALTITUDE + " real," +
                        POINT_COLUMN_HEARTRATE + " integer," +
                        POINT_COLUMN_DATETIME + " text," +
                        POINT_COLUMN_TRAINING + " integer references training on delete cascade)"
        );
        db.execSQL(
                "create table if not exists " + TRAINING_TABLE_NAME +
                        "(" + TRAINING_COLUMN_ID + " integer primary key," +
                        TRAINING_COLUMN_STARTDATE + " text," +
                        TRAINING_COLUMN_STOPDATE + " text," +
                        TRAINING_COLUMN_ACTIVETIME + " text," +
                        TRAINING_COLUMN_DISTANCE + " real," +
                        TRAINING_COLUMN_SPEED + " real," +
                        TRAINING_COLUMN_TYPE + " text," +
                        TRAINING_COLUMN_DESCRIPTION + " text)"
        );
    }

    public boolean insertPoint(Location location, long trainingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POINT_COLUMN_LONGITUDE, location.getLongitude());
        contentValues.put(POINT_COLUMN_LATITUDE, location.getLatitude());

        if (location.hasAltitude()) {
            contentValues.put(POINT_COLUMN_ALTITUDE, location.getAltitude());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(new Date());

        contentValues.put(POINT_COLUMN_DATETIME, date);
        contentValues.put(POINT_COLUMN_TRAINING, trainingId);

        db.insert(POINT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertPoint(Location location, int heartRate, long trainingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POINT_COLUMN_LONGITUDE, location.getLongitude());
        contentValues.put(POINT_COLUMN_LATITUDE, location.getLatitude());

        if (location.hasAltitude()) {
            contentValues.put(POINT_COLUMN_ALTITUDE, location.getAltitude());
        }

        contentValues.put(POINT_COLUMN_HEARTRATE, heartRate);
        contentValues.put(POINT_COLUMN_TRAINING, trainingId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(new Date());

        contentValues.put(POINT_COLUMN_DATETIME, date);
        db.insert(POINT_TABLE_NAME, null, contentValues);
        return true;
    }

    public long getLatestTrainingId() {
        SQLiteDatabase db = this.getWritableDatabase();
        long numberOfTrainings = DatabaseUtils.queryNumEntries(db, TRAINING_TABLE_NAME);
        return numberOfTrainings;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Training> getAllTrainings() {

        List<Training> trainings = new ArrayList<Training>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from training", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Training training = new Training();
            double distance = cursor.getDouble(TRAINING_COLUMN_DISTANCE_INDEX);
            double speed = cursor.getDouble(TRAINING_COLUMN_SPEED_INDEX);
            double activeTime = cursor.getDouble(TRAINING_COLUMN_ACTIVETIME_INDEX);
            Date startDate = null;
            Date stopDate = null;
            String type = cursor.getString(TRAINING_COLUMN_TYPE_INDEX);
            String description = cursor.getString(TRAINING_COLUMN_DESCRIPTION_INDEX);
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(cursor.getString(TRAINING_COLUMN_STARTDATE_INDEX));
                stopDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(cursor.getString(TRAINING_COLUMN_STOPDATE_INDEX));
            } catch (Exception e) {
            }
            training.setSpeed(speed);
            training.setDistance(distance);
            training.setStartDate(startDate);
            training.setStopDate(stopDate);
            training.setActiveTime(activeTime);
            training.setType(type);
            training.setDescription(description);

            trainings.add(training);
            cursor.moveToNext();
        }

        return trainings;
    }

    public long insertTraining(Training training) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAINING_COLUMN_STARTDATE, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        long id = db.insert(TRAINING_TABLE_NAME, null, contentValues);
        return id;
    }

    public boolean updateTraining(Training training) {

        ArrayList<MovePoint> movePointsList = getAllMovePoints(training.getId());
        double totalDistance = TrainingHelper.calculateDistance(movePointsList);
        training.setDistance(totalDistance);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAINING_COLUMN_DISTANCE, training.getDistance());
        contentValues.put(TRAINING_COLUMN_SPEED, training.getSpeed());
        contentValues.put(TRAINING_COLUMN_ACTIVETIME, training.getActiveTime());
        contentValues.put(TRAINING_COLUMN_TYPE, training.getType());
        contentValues.put(TRAINING_COLUMN_STOPDATE, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(training.getStopDate()));
        contentValues.put(TRAINING_COLUMN_DESCRIPTION, training.getDescription());

        db.update(TRAINING_TABLE_NAME, contentValues, "id = ?", new String[] {Long.toString(training.getId())});

        return true;
    }

    public ArrayList<MovePoint> getAllMovePoints(long trainingId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ArrayList<MovePoint> movePointArrayList = new ArrayList<MovePoint>();
        Cursor cursor = db.rawQuery("select * from point where trainingId = ?", new String[] {Long.toString(trainingId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            double longitude,altitude,latitude;
            Date dateTime = null;
            int heartRate;

            try {
                dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(cursor.getString(POINT_COLUMN_DATETIME_INDEX));
            } catch (Exception e) {
            }

            longitude = cursor.getDouble(POINT_COLUMN_LONGITUDE_INDEX);
            latitude = cursor.getDouble(POINT_COLUMN_LATITUDE_INDEX);
            altitude = cursor.getDouble(POINT_COLUMN_ALTITUDE_INDEX);
            heartRate = cursor.getInt(POINT_COLUMN_HEARTRATE_INDEX);

            movePointArrayList.add(new MovePoint(heartRate, dateTime, longitude, latitude, altitude));
            cursor.moveToNext();
        }
        return movePointArrayList;
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
}

