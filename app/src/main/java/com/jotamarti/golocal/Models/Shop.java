package com.jotamarti.golocal.Models;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class Shop extends User implements Serializable {

    private transient LatLng cordinates;
    private String description;
    private String telNumber;
    private boolean isWhatsapp;
    private List<Post> shopPosts;

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

    public List<Post> getShopPosts() {
        return shopPosts;
    }

    public void setShopPosts(List<Post> shopPosts) {
        this.shopPosts = shopPosts;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(cordinates.latitude);
        out.writeDouble(cordinates.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        cordinates = new LatLng(in.readDouble(), in.readDouble());
    }


}
