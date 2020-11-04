package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeesModel {

    String type=null;

    @SerializedName("basefee")
    @Expose
    private Long basefee;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("per_km")
    @Expose
    private Long perKm;
    @SerializedName("status")
    @Expose
    private String status;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPerKm() {
        return perKm;
    }

    public void setPerKm(Long perKm) {
        this.perKm = perKm;
    }

    public Long getBasefee() {
        return basefee;
    }

    public void setBasefee(Long basefee) {
        this.basefee = basefee;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FeesModel() {
    }
}