package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripHistory {
    public TripHistory() {
    }

    @SerializedName("data")
@Expose
private TripHistoryModel data;

public TripHistoryModel getData() {
return data;
}

public void setData(TripHistoryModel data) {
this.data = data;
}

}