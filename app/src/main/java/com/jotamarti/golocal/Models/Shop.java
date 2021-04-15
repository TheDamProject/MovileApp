package com.jotamarti.golocal.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Shop extends User implements Parcelable {

    private transient LatLng coordinates;
    private String description;
    private String telNumber;
    private String shopName;
    private String address;
    private boolean isWhatsapp;
    private List<Post> shopPosts;

    public Shop(String avatar, String userUid, String telNumber, String description, boolean isWhatsapp, LatLng coordinates, List<Post> shopPosts, String address, String shopName){
        super(avatar, userUid);
        this.telNumber = telNumber;
        this.description = description;
        this.isWhatsapp = isWhatsapp;
        this.coordinates = coordinates;
        this.shopPosts = shopPosts;
        this.address = address;
        this.shopName = shopName;
    }

    public Shop(Parcel parcel){
        super(parcel.readString(), parcel.readString());
        this.telNumber = parcel.readString();
        this.description = parcel.readString();
        this.address = parcel.readString();
        this.shopName = parcel.readString();
        this.isWhatsapp = parcel.readInt() == 1;
        this.coordinates = parcel.readParcelable(LatLng.class.getClassLoader());
        shopPosts = new ArrayList<>();
        this.shopPosts = parcel.createTypedArrayList(Post.CREATOR);
    }

    public Shop(){

    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.userUid);
        dest.writeString(this.telNumber);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeString(this.shopName);
        dest.writeInt(this.isWhatsapp ? 1 : 0);
        dest.writeParcelable(this.coordinates, flags);
        dest.writeTypedList(this.shopPosts);
    }

    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    public static Shop getShopByUid(List<Shop> shops, String uid){
        Shop shop = null;
        for(int i = 0; i < shops.size(); i++){
            if(shops.get(i).getUserUid().equals(uid)){
                shop = shops.get(i);
                break;
            }
        }
        return shop;
    }
}
