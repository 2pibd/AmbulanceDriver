package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatusModel {

@SerializedName("isRidingNow")
@Expose
private Integer isRidingNow;
@SerializedName("passengerID")
@Expose
private String passengerID;
@SerializedName("rideID")
@Expose
private String rideID;

public Integer getIsRidingNow() {
return isRidingNow;
}

public void setIsRidingNow(Integer isRidingNow) {
this.isRidingNow = isRidingNow;
}

public String getPassengerID() {
return passengerID;
}

public void setPassengerID(String passengerID) {
this.passengerID = passengerID;
}

public String getRideID() {
return rideID;
}

public void setRideID(String rideID) {
this.rideID = rideID;
}

    public UserStatusModel() {
    }
}