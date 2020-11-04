package com.twopibd.dactarbari.ambulance.drivers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

@SerializedName("certificate_fitness")
@Expose
private String certificateFitness;
@SerializedName("registration")
@Expose
private String registration;
@SerializedName("tax_token")
@Expose
private String taxToken;

public String getCertificateFitness() {
return certificateFitness;
}

public void setCertificateFitness(String certificateFitness) {
this.certificateFitness = certificateFitness;
}

public String getRegistration() {
return registration;
}

public void setRegistration(String registration) {
this.registration = registration;
}

public String getTaxToken() {
return taxToken;
}

public void setTaxToken(String taxToken) {
this.taxToken = taxToken;
}

}