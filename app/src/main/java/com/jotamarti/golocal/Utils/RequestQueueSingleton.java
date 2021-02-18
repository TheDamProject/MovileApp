package com.jotamarti.golocal.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jotamarti.golocal.App;

/**
 * Singleton class to manage the request queue
 */
public class RequestQueueSingleton {

    /* We need to take care of using application context and not activity context
       because it could produce a memory leak, we do this getting the context from
       the app and not letting the developer to pass the context directly to this class.

       Additionally we put the SupressLint anotation to avoid a warning from Android Studio.
    */

    @SuppressLint("StaticFieldLeak")
    private static RequestQueueSingleton instance;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private RequestQueue requestQueue;


    private RequestQueueSingleton() {
        context = App.getContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(){
        if (instance == null) {
            instance = new RequestQueueSingleton();
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    /**
     * Method to add a request to the queue.
     * @param req The request to add
     */
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
