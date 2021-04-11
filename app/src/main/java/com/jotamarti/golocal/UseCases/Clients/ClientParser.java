package com.jotamarti.golocal.UseCases.Clients;

import com.jotamarti.golocal.Models.Client;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientParser {

    public static Client parseClientFromJsonObject(JSONObject jsonObject) throws JSONException {
        Client client = new Client();
        String uid = jsonObject.getString("uid");
        String nickName = jsonObject.getString("nick");
        String url = jsonObject.getString("avatar");
        client.setUserUid(uid);
        client.setNickName(nickName);
        client.setAvatar(url);
        return client;
    }
}
