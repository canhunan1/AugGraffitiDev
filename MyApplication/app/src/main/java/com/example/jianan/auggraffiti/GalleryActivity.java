package com.example.jianan.auggraffiti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        listView = (ListView) findViewById(R.id.listView);
        getgallary();
    }
    public boolean getgallary() {
        // personEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //send request to get score
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://roblkw.com/msa/getgallery.php";

        final Map<String, String> params = new HashMap<String, String>();

        params.put("email", "jianan205@gmail.com");
        StringRequest stringRequest = postScoreStringRequest(params, url);
        queue.add(stringRequest);
        return true;
    }

    //post request to place
    private StringRequest postScoreStringRequest(final Map<String, String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    // no idea why enter in this function 3 times when just sending GPS information just once.
                    public void onResponse(String response) {
                        String[] imgUrl = response.trim().split("[,]+");
                        Log.v("imgurl"+response);
                       // listView.;
                        if (response.equals("0")) // when get 0, log in successfully
                            //sendMessage(personEmail);
                            //fail to log in.
                            Log.v("Successfully post image");
                        else
                            Log.v("Error happens when posting the img");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }
}
