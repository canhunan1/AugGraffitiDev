package com.example.jianan.auggraffiti;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jianan on 9/12/2016.
 */
public class StringPost extends  StringRequest{
    // Request a string response from the provided URL.
    private Map<String,String> params = new HashMap<String,String>();
    //private Context context;
    //private static final String TAG = "StringPost";
    public StringPost(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String,String> params) {
        super("http://roblkw.com/msa" +url, listener, errorListener);

        this.params = params;
        };
        @Override
        protected Map<String, String> getParams() {
            return params;
        }

    public void sendRequest(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(this);
    }

}


