package com.jotamarti.golocal.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

public class Post implements Parcelable {

    private String imageUrl;
    private String header;
    private String message;
    private String companyUid;
    private String postId;



    public Post(String imageUrl, String header, String message, String companyUid, String postId) {
        this.imageUrl = imageUrl;
        this.header = header;
        this.message = message;
        this.companyUid = companyUid;
        this.postId = postId;
    }

    public Post(Parcel parcel){
        this.header = parcel.readString();
        this.message = parcel.readString();
        this.companyUid = parcel.readString();
        this.postId = parcel.readString();
        this.imageUrl = parcel.readString();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompanyUid() {
        return companyUid;
    }

    public void setCompanyUid(String companyUid) {
        this.companyUid = companyUid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeString(message);
        dest.writeString(companyUid);
        dest.writeString(postId);
        dest.writeString(imageUrl);

    }

    public static final Parcelable.Creator<Post> CREATOR
            = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
