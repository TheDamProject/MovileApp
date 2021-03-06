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
        for (int i = 0; i < jsonPostsListObject.length(); i++) {
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
        String companyUid = jsonPostObject.getString("UIDshop");
        post.setHeader(header);
        post.setMessage(message);
        post.setImageUrl(imageUrl);
        post.setPostId(postId);
        post.setCompanyUid(companyUid);
        return post;
    }

    public static JSONObject serializePost(Post post){
        JSONObject params = new JSONObject();
        try {
            params.put("title", post.getHeader());
            params.put("content", post.getMessage());
            params.put("image", post.getImageUrl());
            params.put("shopUid", post.getCompanyUid());
            params.put("typeValue", "standar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
