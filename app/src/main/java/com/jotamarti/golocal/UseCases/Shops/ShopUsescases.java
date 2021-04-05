package com.jotamarti.golocal.UseCases.Shops;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShopUsescases implements ShopApi{

    private final String TAG = "ShopUsecases";

    @Override
    public void getShop(String uid) {

    }

    @Override
    public void registerShopInBackend(Shop shop, ShopCallbacks.onResponseRegisterShopInBackend onResponseRegisterShopInBackend) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/shop/add";

        JSONObject params = new JSONObject();
        try {
            params.put("uid", shop.getUserUid());
            params.put("name", shop.getShopName());
            params.put("location_id",  null);
            params.put("latitude", String.valueOf(shop.getCoordinates().latitude));
            params.put("longitude", String.valueOf(shop.getCoordinates().longitude));
            params.put("address", shop.getAddress());
            params.put("id_google", "test");
            params.put("shopData_id", null);
            params.put("phone", Integer.valueOf(shop.getTelNumber()));
            params.put("isWhatsapp", shop.isWhatsapp());
            params.put("description", shop.getDescription());
            params.put("category_id", null);
            params.put("category", "test");
            params.put("logo", shop.getAvatar());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseRegisterShopInBackend.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    onResponseRegisterShopInBackend.onErrorResponse(BackendErrors.SERVER_ERROR);
                } else {
                    BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                    onResponseRegisterShopInBackend.onErrorResponse(httpNetworkError);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("X-AUTH-TOKEN", "TC9[L7<D4gd)5{6<!=H!jUYE7mum<H~NS4yJo/a+7(3v>f5n+_49u|_a4|7W");
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

    @Override
    public void modifyShop(String uid, JsonObject newValues) {

    }
}
