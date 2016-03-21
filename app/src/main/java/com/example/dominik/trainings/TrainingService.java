package com.example.dominik.trainings;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.dominik.trainings.DTO.SyncedTrainingDTO;
import com.example.dominik.trainings.DTO.TrainingDTO;
import com.example.dominik.trainings.entities.HeartRatePoint;
import com.example.dominik.trainings.entities.Movepoint;
import com.example.dominik.trainings.entities.Training;
import com.example.dominik.trainings.utils.MovepointUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Dominik on 2015-10-13.
 */

@EService
public class TrainingService extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private Location lastLocation = null;
    private boolean recording = false;
    private long activeTime;
    private Realm realm;

    private List<Movepoint> points = new ArrayList<>();
    private List<Movepoint> unsyncedMovepoints = getUnsyncedMovepoints();

    private boolean paused = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final IBinder mBinder = new LocalBinder();
    private Training training;
    private Long lastResumeTime,lastStopTime;
    private MainActivity mainActivity;

    private HeartRatePoint heartRatePoint;

    private Handler handler = new Handler();

    @Pref
    MyPrefs_ myPrefs;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private class CustomListener<T> implements Response.Listener<T> {

        @Override
        public void onResponse(T response) {

            if (response instanceof SyncedTrainingDTO) {
                SyncedTrainingDTO syncedTraining = (SyncedTrainingDTO) response;
                training.setRemoteId(syncedTraining.getRemoteId());
                updateSyncedMovepoints();
            }
            else {
                List<SyncedTrainingDTO> syncedTrainings = (ArrayList<SyncedTrainingDTO>) response;
                realm.beginTransaction();
                for(SyncedTrainingDTO syncedTrainingDTO : syncedTrainings) {
                    Training training = realm.where(Training.class).equalTo("id", syncedTrainingDTO.getLocalId()).findFirst();
                    training.setSynced(1);
                    training.setRemoteId(syncedTrainingDTO.getRemoteId());
                    List<Movepoint> movepoints = realm.where(Movepoint.class).equalTo("trainingid", syncedTrainingDTO.getLocalId()).findAll();

                    for(int i = 0; i < movepoints.size(); i ++) {
                        movepoints.get(i).setSynced(1);
                    }

                    realm.copyToRealmOrUpdate(movepoints);
                    realm.copyToRealmOrUpdate(training);
                }
                realm.commitTransaction();
            }

        }
    }

    private class CustomErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            int a = 5;
        }
    }

    private Runnable synchronizePointsRunnable = new Runnable() {
        @Override
        public void run() {
            synchronizePoints();
            handler.postDelayed(synchronizePointsRunnable, myPrefs.webUpdates().get() * 1000);
        }
    };

    private void synchronizePoints() {
        if (!getUnsyncedMovepoints().isEmpty()) {

            TrainingDTO trainingToUpload = new TrainingDTO(training, MovepointUtils.toDTO(getUnsyncedMovepoints()));

            final String unsyncedTraining = new Gson().toJson(trainingToUpload);

            String url ="http://62.244.154.41:8084/upload/workout/live/";

            CustomReq<SyncedTrainingDTO> uploadRequest = new CustomReq<>(
                    Request.Method.POST,
                    url,
                    unsyncedTraining,
                    new CustomListener<SyncedTrainingDTO>(),
                    new CustomErrorListener(),
                    "singleTraining");

            RequestQueue queue = Volley.newRequestQueue(TrainingService.this);
            queue.add(uploadRequest);
        }
    }

    private List<Movepoint> getUnsyncedMovepoints() {
        List<Movepoint> unsynced = new ArrayList<>();
        for(Movepoint movepoint : points) {
            if (movepoint.getSynced() == 0) {
                unsynced.add(movepoint);
            }
        }
        unsyncedMovepoints = unsynced;
        return unsynced;
    }

    private void updateSyncedMovepoints() {
        for(Movepoint movepoint : unsyncedMovepoints) {
            movepoint.setSynced(1);
        }
    }

    private void synchronizeTrainings() {

        List<TrainingDTO> trainingsToUpload = new ArrayList<>();
        for(Training training : realm.allObjects(Training.class)) {
            if (training.getSynced() == 0) {
                trainingsToUpload.add(new TrainingDTO(training, MovepointUtils.toDTO(realm
                        .where(Movepoint.class)
                        .equalTo("training.id", training.getId())
                        .equalTo("synced", 0)
                        .findAll())));
            }
        }
        if (!trainingsToUpload.isEmpty()) {

            String unsyncedPoints = new Gson().toJson(trainingsToUpload);

            String url = "http://62.244.154.41:8084/upload/workout/";

            CustomReq<List<SyncedTrainingDTO>> uploadRequest = new CustomReq<>(
                    Request.Method.POST,
                    url,
                    unsyncedPoints,
                    new CustomListener<List<SyncedTrainingDTO>>(),
                    new CustomErrorListener(),
                    "multiTraining");

            RequestQueue queue = Volley.newRequestQueue(TrainingService.this);
            queue.add(uploadRequest);
        }
    }

    public void setPaused(boolean paused) {
        if (paused) {
            lastStopTime = System.currentTimeMillis();
            activeTime += lastStopTime - lastResumeTime;
        }
        else {
            lastResumeTime = System.currentTimeMillis();
        }
        this.paused = paused;
    }

    @Override
    public void onCreate() {
        realm = Realm.getDefaultInstance();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext(), this, this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        synchronizeTrainings();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(myPrefs.locationUpdates().get());
        locationRequest.setFastestInterval(myPrefs.locationUpdates().get());
        locationRequest.setPriority(myPrefs.accuracy().get());
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException ex) {}
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (recording && !paused) {

            heartRatePoint = MyApplication.bluetoothConnection.getLatestHeartRatePoint();

            Movepoint movepoint = new Movepoint(getNewMovepointId(),
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(),
                    new Date(),
                    heartRatePoint != null ?
                            heartRatePoint.getDate().getTime() >= System.currentTimeMillis() - 30000 ?
                                    heartRatePoint : null
                    : null,
                    0,
                    training);

            points.add(movepoint);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(movepoint);
            realm.commitTransaction();
            mainActivity.diffValue.setText(Double.toString(TrainingHelper.calculateDistance(lastLocation, location)));

            lastLocation = location;
            updateUI();
        }
    }

    private int getNewMovepointId() {
        if (realm.where(Movepoint.class).max("id") == null) {
            return 1;
        }
        else {
            return realm.where(Movepoint.class).max("id").intValue() + 1;
        }
    }

    @UiThread
    public void updateUI() {
        mainActivity.latitude.setText(Double.toString(lastLocation.getLatitude()));
        mainActivity.longitude.setText(Double.toString(lastLocation.getLongitude()));
        mainActivity.accuracyValue.setText(Double.toString(lastLocation.getAccuracy()));
        mainActivity.heartRateView.setText(heartRatePoint != null ? Integer.toString(heartRatePoint.getHeartRate()) : "");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRecording() {
        return recording;
    }

    public void finishTraining() {

        if (paused) {

            recording = false;
            paused = false;
            googleApiClient.disconnect();

            training.setStopDate(new Date());
            training.setActiveTime(activeTime);
            training.setDistance(TrainingHelper.calculateDistance(points));
            training.setDuration(40.0);

            training.setSpeed(training.getDistance() / training.getActiveTime());


            handler.removeCallbacks(synchronizePointsRunnable);

            MyApplication.bluetoothConnection.resetHeartRatePoints();

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(training);
            realm.copyToRealmOrUpdate(points);
            realm.commitTransaction();

            startActivity(new Intent(getApplicationContext(), TrainingFinishOverview.class) {
                {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Pause the training before finishing", Toast.LENGTH_LONG).show();
        }
    }

    public void startRecording() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        training = new Training();
        training.setId(getNewTrainingId());
        training.setStartDate(new Date());

        training.setActivityId(1);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(training);
        realm.commitTransaction();
        recording = true;
        paused = false;
        lastResumeTime = System.currentTimeMillis();
        handler.postDelayed(synchronizePointsRunnable, myPrefs.webUpdates().get() * 1000);
    }

    private int getNewTrainingId() {
        if (realm.where(Training.class).max("id") == null) {
            return 1;
        }
        else {
            return realm.where(Training.class).max("id").intValue() + 1;
        }
    }

    public void makeBackground() {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        startForeground(1, mBuilder.build());
    }

    public void stopBackground() {
        stopForeground(true);
    }


    public class LocalBinder extends Binder {
        TrainingService getService() {
            return TrainingService.this;
        }
    }

    @Override
    public void onDestroy() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        super.onDestroy();
    }

    public class CustomReq<T> extends Request<T> {

        private Response.Listener<T> listener;
        private String requestBody;
        private Map<String, String> params;
        private String returnType;

        public CustomReq(int method, String url, String requestBody, Response.Listener<T> listen, Response.ErrorListener error, String returnType) {
            super(method, url, error);
            this.listener = listen;
            this.requestBody = requestBody;
            this.returnType = returnType;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String data = new String(response.data);
            try {
                if (returnType.equals("singleTraining")) {
                    T syncedTrainings = new Gson().fromJson(data, new TypeToken<SyncedTrainingDTO>() {
                    }.getType());
                    return Response.success(syncedTrainings, HttpHeaderParser.parseCacheHeaders(response));
                }
                else if (returnType.equals("multiTraining")) {
                    T syncedTrainings = new Gson().fromJson(data, new TypeToken<List<SyncedTrainingDTO>>() {
                    }.getType());
                    return Response.success(syncedTrainings, HttpHeaderParser.parseCacheHeaders(response));
                }
                else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String>  params = new HashMap<String, String>();
            params.put("Content-Type", "application/json; charset=utf-8");
            return params;
        }

        @Override
        public byte[] getBody() {
            try {
                return requestBody.getBytes("utf-8");
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        requestBody, "utf-8");
                return null;
            }
        }
    }
}
