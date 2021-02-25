package com.jotamarti.golocal.Models;

import android.graphics.Bitmap;

public class Post {

    private Bitmap image;
    private String header;
    private String message;
    private String companyId;
    private String postId;



    public Post(Bitmap image, String header, String message, String companyId, String postId) {
        this.image = image;
        this.header = header;
        this.message = message;
        this.companyId = companyId;
        this.postId = postId;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
