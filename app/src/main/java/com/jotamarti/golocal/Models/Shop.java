package com.jotamarti.golocal.Models;

import java.net.URL;

public class Shop extends User {

    private String description;
    private String number;
    private boolean isWhatsapp;

    public Shop(URL avatar, String email, String userId, String number, String description, boolean isWhatsapp){
        super(avatar, email, userId);
        this.number = number;
        this.description = description;
        this.isWhatsapp = isWhatsapp;
    }

    public Shop(){

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
