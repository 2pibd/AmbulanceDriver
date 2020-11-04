package com.twopibd.dactarbari.ambulance.drivers.model;

public class RideRequestModel {
    String from,to,status,USER_ID;
    boolean hasCancelled=false;
    boolean hasTripStarted=false;
    boolean hasArrived=false;
    boolean hasRideCompleated=false;
    Float estimated;
    Double paid;
    int distance;



    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public RideRequestModel(String from, String to, String status, String USER_ID, boolean hasCancelled, boolean hasTripStarted, boolean hasArrived, boolean hasRideCompleated, Float estimated, Double paid, int distance) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.USER_ID = USER_ID;
        this.hasCancelled = hasCancelled;
        this.hasTripStarted = hasTripStarted;
        this.hasArrived = hasArrived;
        this.hasRideCompleated = hasRideCompleated;
        this.estimated = estimated;
        this.paid = paid;
        this.distance = distance;
    }

    public Float getEstimated() {
        return estimated;
    }

    public void setEstimated(Float estimated) {
        this.estimated = estimated;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public boolean isHasRideCompleated() {
        return hasRideCompleated;
    }

    public void setHasRideCompleated(boolean hasRideCompleated) {
        this.hasRideCompleated = hasRideCompleated;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public boolean isHasCancelled() {
        return hasCancelled;
    }

    public void setHasCancelled(boolean hasCancelled) {
        this.hasCancelled = hasCancelled;
    }

    public boolean isHasTripStarted() {
        return hasTripStarted;
    }

    public void setHasTripStarted(boolean hasTripStarted) {
        this.hasTripStarted = hasTripStarted;
    }

    public boolean isHasArrived() {
        return hasArrived;
    }

    public void setHasArrived(boolean hasArrived) {
        this.hasArrived = hasArrived;
    }

    public RideRequestModel() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
