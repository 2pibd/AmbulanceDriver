package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripHistoryModel {

    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("hasArrived")
    @Expose
    private Boolean hasArrived;
    @SerializedName("hasCancelled")
    @Expose
    private Boolean hasCancelled;
    @SerializedName("hasRideCompleated")
    @Expose
    private Boolean hasRideCompleated;
    @SerializedName("hasTripStarted")
    @Expose
    private Boolean hasTripStarted;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("trip_started_time")
    @Expose
    private String tripStartedTime = null;
    @SerializedName("user_ID")
    @Expose
    private String userID;
    Double estimated=null;

    public TripHistoryModel() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean getHasArrived() {
        return hasArrived;
    }

    public void setHasArrived(Boolean hasArrived) {
        this.hasArrived = hasArrived;
    }

    public Boolean getHasCancelled() {
        return hasCancelled;
    }

    public void setHasCancelled(Boolean hasCancelled) {
        this.hasCancelled = hasCancelled;
    }

    public Boolean getHasRideCompleated() {
        return hasRideCompleated;
    }

    public void setHasRideCompleated(Boolean hasRideCompleated) {
        this.hasRideCompleated = hasRideCompleated;
    }

    public Boolean getHasTripStarted() {
        return hasTripStarted;
    }

    public void setHasTripStarted(Boolean hasTripStarted) {
        this.hasTripStarted = hasTripStarted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTripStartedTime() {
        return tripStartedTime;
    }

    public void setTripStartedTime(String tripStartedTime) {
        this.tripStartedTime = tripStartedTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Double getEstimated() {
        return estimated;
    }

    public void setEstimated(Double estimated) {
        this.estimated = estimated;
    }
}