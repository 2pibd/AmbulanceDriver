package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmbulanceModel {

    @SerializedName("isOnService")
    @Expose
    private Long isOnService;
    @SerializedName("ambulanceID")
    @Expose
    private String ambulanceID;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("licenseNumber")
    @Expose
    private String licenseNumber;
    @SerializedName("modelName")
    @Expose
    private String modelName;
    @SerializedName("modelyear")
    @Expose
    private String modelyear;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("select_vehicle_type")
    @Expose
    private String selectVehicleType;
    @SerializedName("vehicle")
    @Expose
    private Vehicle vehicle;
    @SerializedName("photo")
    @Expose
    private String photo;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelyear() {
        return modelyear;
    }

    public void setModelyear(String modelyear) {
        this.modelyear = modelyear;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSelectVehicleType() {
        return selectVehicleType;
    }

    public void setSelectVehicleType(String selectVehicleType) {
        this.selectVehicleType = selectVehicleType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public AmbulanceModel() {
    }

    public Long getIsOnService() {
        return isOnService;
    }

    public void setIsOnService(Long isOnService) {
        this.isOnService = isOnService;
    }

    public String getAmbulanceID() {
        return ambulanceID;
    }

    public void setAmbulanceID(String ambulanceID) {
        this.ambulanceID = ambulanceID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}