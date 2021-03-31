package com.jotamarti.golocal.Models;

import java.io.Serializable;
import java.net.URL;

public abstract class User implements Serializable {

    protected URL avatar;
    protected String userUid;

    public User(URL avatar, String email, String userUid) {
        this.avatar = avatar;
        this.userUid = userUid;
    }

    public User() {

    }

    public URL getAvatar() {
        return avatar;
    }

    public void setAvatar(URL avatar) {
        this.avatar = avatar;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

}
