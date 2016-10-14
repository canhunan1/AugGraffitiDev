package com.example.jianan.auggraffiti;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by Jianan on 9/12/2016.
 * This class is used to send the string post to the php server
 */
public class StringPost {
    // Request a string response from the provided URL.
   // private final Map<String,String> params = new HashMap<String,String>();
    private Response.Listener<String> responseListener;
    private final String ERROR_MESSAGE = "RESPONSE ERROR";
    private String errorMessage;

    public StringPost(Context context, String url,
                      Response.Listener<String> responseListener,
                      String errorMessage,
                      final Map<String,String> params){
        this.responseListener = responseListener;
        this.errorMessage = errorMessage;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = postScreenStringRequest(params, url);
        queue.add(stringRequest);
    };

    /*
    * @param Map params  the parameters posted to the server
    * @param String url is url to post the request to
    * @return StringRequest is the request string
    * */
    private StringRequest postScreenStringRequest(final Map<String, String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,
                responseListener, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(ERROR_MESSAGE, errorMessage);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                return params;
            }
        };
    }

}


