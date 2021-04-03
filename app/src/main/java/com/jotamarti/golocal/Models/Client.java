package com.jotamarti.golocal.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Client extends User implements Parcelable {

    private String nickName;

    public Client(String avatar, String userUid, String nickName){
        super(avatar, userUid);
        this.nickName = nickName;
    }

    public Client(Parcel parcel){
        super(parcel.readString(), parcel.readString());
        this.nickName = parcel.readString();
    }

    public Client(){

    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(userUid);
        dest.writeString(nickName);
    }

    public static final Parcelable.Creator<Client> CREATOR
            = new Parcelable.Creator<Client>() {
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
