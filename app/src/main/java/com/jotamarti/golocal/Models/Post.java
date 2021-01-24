package com.jotamarti.golocal.Models;

import android.media.Image;

public class Post {

    private Image image;
    private String header;
    private String message;
    private String companyId;

    public Post(Image image, String header, String message, String companyId) {
        this.image = image;
        this.header = header;
        this.message = message;
        this.companyId = companyId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
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
}
