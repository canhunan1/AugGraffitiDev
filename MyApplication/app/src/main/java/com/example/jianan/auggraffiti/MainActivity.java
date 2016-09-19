package com.example.jianan.auggraffiti;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView ;
    private static final int REQUEST_CODE = 100;
    private SignInButton login;
    private TextView name;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions signInOptions;
    private static final String TAG = "MyActivity";
    private String personEmail;
    public final static String EXTRA_MESSAGE = "com.example.jianan.auggraffiti.MainActivity.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        login = (SignInButton) findViewById(R.id.login);
       // name = (TextView) findViewById(R.id.name);
        mTextView = (TextView) findViewById(R.id.text);
        login.setSize(SignInButton.SIZE_WIDE);
        login.setScopes(signInOptions.getScopeArray());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, REQUEST_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();

          //  name.setText(account.getDisplayName());

            personEmail = account.getEmail();
//            final Map<String,String> params = new HashMap<String,String>();
//            params.put("email", personEmail);
//            new StringPost("/login.php",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            if(response.equals("0")) {
//                                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
//                                startActivity(intent);
//                            }
//                            else
//                                mTextView.setText("Fail to sign in");
//                        }},
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            mTextView.setText("That didn't work!");
//                        }},
//                    params)
//                    .sendRequest(this);
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://roblkw.com/msa/login.php";
            final Map<String,String> params = new HashMap<String,String>();
            params.put("email", personEmail);
            // Request a string response from the provided URL.
            StringRequest stringRequest = postStringRequest(params, url);
            //stringRequest.getParams();
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            Log.v(TAG,personEmail);
        }

    }

    @NonNull
    private StringRequest postStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        if(response.equals("0")) {
                            /*Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                            startActivity(intent);*/
                            sendMessage(personEmail);

                        }
                        else
                            mTextView.setText("Fail to sign in");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sendMessage(String message) {
        Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}