package com.jotamarti.golocal.dummy;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShopsDummy {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Shop> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Shop> ITEM_MAP = new HashMap<String, Shop>();

    private static final int COUNT = 25;
    private static List<Post> posts = PostsDummy.getITems();

    public static void generateContent() {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public static List<Shop> getITems(){
        ITEMS.clear();
        generateContent();
        return ITEMS;
    }

    private static void addItem(Shop item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getUserUid(), item);
    }

    private static Shop createDummyItem(int position) {
        Random rand = new Random();
        URL url = null;
        try {
            url = new URL("https://assets.thehansindia.com/h-upload/feeds/2019/07/19/197487-1.jpg");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Shop shop = new Shop();
        if(position == 1) {
            shop.setUserUid("1234");
        } else {
            int uid = rand.nextInt(99999);
            shop.setUserUid(String.valueOf(uid));
        }

        shop.setAvatar(url);
        shop.setShopPosts(posts);
        int minLatNumber = 39000;
        int maxLatNumber = 52000;
        int minLangNumber = 35000;
        int maxLangNumber = 42000;


        int lat = (int) (Math.random() * (maxLatNumber - minLatNumber + 1) + minLatNumber);
        int lang = (int) (Math.random() * (maxLangNumber - minLangNumber + 1) + maxLangNumber);
        lat = lat / 100000;
        lang = lang / 10000;
        shop.setCordinates(new LatLng(lat, lang));


        int numberTel = rand.nextInt(9999999);
        shop.setTelNumber(String.valueOf(numberTel));
        shop.setWhatsapp(true);
        return shop;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
