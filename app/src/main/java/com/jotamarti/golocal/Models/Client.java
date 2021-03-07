package com.jotamarti.golocal.Models;

import java.net.URL;

public class Client extends User  {

    private String userName;

    public Client(URL avatar, String email, String userId, String userName){
        super(avatar, email, userId);
        this.userName = userName;
    }

    public Client(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
