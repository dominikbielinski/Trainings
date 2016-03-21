package com.example.dominik.trainings.DTO;

/**
 * Created by Dominik on 2016-02-09.
 */
public class SyncedTrainingDTO {

    int localId;
    int remoteId;

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public SyncedTrainingDTO(int localId, int remoteId) {
        this.localId = localId;
        this.remoteId = remoteId;
    }
}
