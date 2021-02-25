package com.jotamarti.golocal.dummy;

import android.content.ClipData;
import android.graphics.BitmapFactory;

import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsDummy {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Post> ITEMS = new ArrayList<Post>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Post> ITEM_MAP = new HashMap<String, Post>();

    private static final int COUNT = 25;

    public static void generateContent() {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public static List<Post> getITems(){
        ITEMS.clear();
        generateContent();
        return ITEMS;
    }

    private static void addItem(Post item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getPostId(), item);
    }

    private static Post createDummyItem(int position) {
        return new Post(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.shop_header_mock), "Naranjas " + position, "buenas naranjas", String.valueOf((position+100)), String.valueOf(position));
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
