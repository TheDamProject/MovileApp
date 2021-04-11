package com.jotamarti.golocal.UseCases.Posts;

import com.jotamarti.golocal.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostParser {

    public static List<Post> parsePostsFromJsonArray(JSONArray jsonPostsListObject) throws JSONException {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i < jsonPostsListObject.length(); i++) {
            Post post = parsePostFromJsonObject(jsonPostsListObject.getJSONObject(i));
            postList.add(post);
        }
        return postList;
    }

    public static Post parsePostFromJsonObject(JSONObject jsonPostObject) throws JSONException {
        Post post = new Post();
        String header = jsonPostObject.getString("title");
        String message = jsonPostObject.getString("content");
        String imageUrl = jsonPostObject.getString("image");
        String postId = String.valueOf(jsonPostObject.getInt("id"));
        //String companyUid = jsonPostObject.getString("shopUid");
        post.setHeader(header);
        post.setMessage(message);
        post.setImageUrl(imageUrl);
        post.setPostId(postId);
        return post;
    }
}
