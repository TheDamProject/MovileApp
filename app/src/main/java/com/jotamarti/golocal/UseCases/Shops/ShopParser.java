package com.jotamarti.golocal.UseCases.Shops;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.UseCases.Posts.PostParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ShopParser {

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

    /*public JSONObject serializeShop(Shop shop){

    }*/
}
