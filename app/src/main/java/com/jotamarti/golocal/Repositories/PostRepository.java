package com.jotamarti.golocal.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.UseCases.Posts.PostApi;
import com.jotamarti.golocal.UseCases.Posts.PostCallbacks;
import com.jotamarti.golocal.UseCases.Posts.PostParser;
import com.jotamarti.golocal.UseCases.Posts.PostRepositoryFactory;
import com.jotamarti.golocal.UseCases.Posts.PostUsecases;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class PostRepository implements PostRepositoryFactory {

    private final String TAG = "PostRepository";

    private PostApi postUsecases;

    public PostRepository(){
        postUsecases = new PostUsecases();
    }

    // Backend
    private MutableLiveData<Post> currentPost = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> haveHttpNetworkError = new MutableLiveData<>();


    @Override
    public LiveData<Post> createPostInBackend(Post post) {
        currentPost = new MutableLiveData<>();
        postUsecases.createPostInBackend(post, new PostCallbacks.onResponseCreatePostInBackend() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    Post post = PostParser.parsePostFromJsonObject(json);
                    currentPost.postValue(post);
                } catch (JSONException jsonException) {
                    haveHttpNetworkError.setValue(BackendErrors.CLIENT_ERROR);
                }
            }

            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return currentPost;
    }

    @Override
    public MutableLiveData<BackendErrors> getBackendError() {
        return haveHttpNetworkError;
    }
}
