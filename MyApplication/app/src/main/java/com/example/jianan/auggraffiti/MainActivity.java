package com.example.jianan.auggraffiti;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Map;
/*
* This activity is used to log in the google service and jump to Googlemap Activity automatically
* Post request to the server to log in.
* */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView loginTextView ;
    private static final int REQUEST_CODE = 100;
    private SignInButton login;
    private TextView name;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions signInOptions;
    private static final String TAG = "MyActivity";
    private String personEmail = "jianan205@gmail.com";

    public final static String EXTRA_MESSAGE = "com.example.jianan.auggraffiti.MainActivity.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        login = (SignInButton) findViewById(R.id.login);
        loginTextView = (TextView) findViewById(R.id.state_of_login);
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

    /*
    * Called when get the signin result from the google service.
    * If successfully sign in the google service, send the user email to the server.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //to make sure continue to the next screen if the sign failed for unknown reason
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                personEmail = account.getEmail();
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://roblkw.com/msa/login.php";
                final Map<String, String> params = new HashMap<String, String>();
                params.put("email", personEmail);
                // Request a string response from the provided URL.
                StringRequest stringRequest = postStringRequest(params, url);
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                Log.v(TAG, personEmail);
            }
            else{
                Status status= result.getStatus();
                Toast.makeText(this,status.toString(), Toast.LENGTH_LONG).show();
            }

        }
    }

    @NonNull
    //post request to log in
    private StringRequest postStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("0")) // when get 0, log in successfully
                            sendMessage(personEmail);
                        //fail to log in.
                        else
                            loginTextView.setText("Fail to sign in");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginTextView.setText("That didn't work!");
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
// send a message from the main activity to the google map activity
    public void sendMessage(String message) {
        Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}