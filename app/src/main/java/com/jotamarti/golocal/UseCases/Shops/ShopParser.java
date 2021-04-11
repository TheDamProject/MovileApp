package com.jotamarti.golocal.UseCases.Shops;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.UseCases.Posts.PostParser;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopParser {

    private static final String TAG = "ShopParser";

    public static ArrayList<Shop> parseShopsFromJsonArray(JSONArray jsonArray){
        ArrayList<Shop> shopList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonShopObject = jsonArray.getJSONObject(i);
                Shop shop = parseShopFromJsonObject(jsonShopObject);
                shopList.add(shop);
            }
        } catch (JSONException jsonException) {
            Log.d(TAG, "Problem parsing Shops");
        }
        return shopList;
    }

    public static Shop parseShopFromJsonObject(JSONObject jsonShopObject){
        Shop shop = new Shop();
        try {
            String uid = jsonShopObject.getString("uid");
            String name = jsonShopObject.getString("name");
            JSONObject jsonLocationObject = jsonShopObject.getJSONObject("location");
            double latitude = jsonLocationObject.getDouble("latitude");
            double longitude = jsonLocationObject.getDouble("longitude");
            String address = jsonLocationObject.getString("address");
            JSONObject jsonShopDataObject = jsonShopObject.getJSONObject("shopData");
            String description = jsonShopDataObject.getString("description");
            String phone = jsonShopDataObject.getString("phone");
            Boolean isWhatsapp = jsonShopDataObject.getBoolean("isWhatsapp");
            String logo = jsonShopDataObject.getString("logo");
            JSONArray posts = jsonShopObject.getJSONArray("posts");
            List<Post> postsList = PostParser.parsePostsFromJsonArray(posts);
            shop.setUserUid(uid);
            shop.setShopName(name);
            shop.setAddress(address);
            shop.setCoordinates(new LatLng(latitude, longitude));
            shop.setDescription(description);
            shop.setTelNumber(phone);
            shop.setWhatsapp(isWhatsapp);
            shop.setAvatar(logo);
            shop.setShopPosts(postsList);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
        return shop;
    }

    public static JSONObject serializeShop(Shop shop){
        JSONObject params = new JSONObject();
        try {
            params.put("uid", shop.getUserUid());
            params.put("name", shop.getShopName());
            params.put("latitude", shop.getCoordinates().latitude);
            params.put("longitude", shop.getCoordinates().longitude);
            params.put("address", shop.getAddress());
            params.put("phone", String.valueOf(shop.getTelNumber()));
            params.put("isWhatsapp", shop.isWhatsapp() ? 1 : 0);
            params.put("description", shop.getDescription());
            params.put("category", "testing category");
            params.put("logo", shop.getAvatar());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;

    }
}
