package com.jotamarti.golocal.Models;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;

public class Shop extends User {

    private LatLng cordinates;
    private String description;
    private String telNumber;
    private boolean isWhatsapp;

    public Shop(URL avatar, String email, String userId, String telNumber, String description, boolean isWhatsapp, LatLng cordinates){
        super(avatar, email, userId);
        this.telNumber = telNumber;
        this.description = description;
        this.isWhatsapp = isWhatsapp;
        this.cordinates = cordinates;
    }

    public Shop(){

    }

    public LatLng getCordinates() {
        return cordinates;
    }

    public void setCordinates(LatLng cordinates) {
        this.cordinates = cordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWhatsapp() {
        return isWhatsapp;
    }

    public void setWhatsapp(boolean whatsapp) {
        isWhatsapp = whatsapp;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }
}
