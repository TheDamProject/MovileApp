package com.jotamarti.golocal.UseCases.Posts;

import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONObject;

public class PostCallbacks {

    public interface onResponseCreatePostInBackend {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }
}
