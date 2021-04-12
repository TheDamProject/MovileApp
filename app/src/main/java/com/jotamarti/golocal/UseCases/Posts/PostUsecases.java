package com.jotamarti.golocal.UseCases.Posts;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.BuildConfig;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.UseCases.Shops.ShopParser;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostUsecases implements PostApi {
    @Override
    public void createPostInBackend(Post post, PostCallbacks.onResponseCreatePostInBackend onResponseCreatePostInBackend) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/shop/add";

        JSONObject params = PostParser.serializePost(post);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseCreatePostInBackend.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    onResponseCreatePostInBackend.onErrorResponse(BackendErrors.SERVER_ERROR);
                } else {
                    BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                    onResponseCreatePostInBackend.onErrorResponse(httpNetworkError);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("X-AUTH-TOKEN", BuildConfig.BACKEND_API_KEY);
                return params;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, 5f));
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
