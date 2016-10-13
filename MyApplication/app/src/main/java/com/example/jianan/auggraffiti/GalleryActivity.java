package com.example.jianan.auggraffiti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;
/*
* Used to show a list of urls when the gallery button in clicked in the GoogleMapActivity
* */
public class GalleryActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jianan.auggraffiti.Gallery.MESSAGE";
    ListView listView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String personalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        personalEmail = intent.getStringExtra(GoogleMapActivity.PERSONAL_EMAIL);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               TextView textView = (TextView) view;
                showImage(textView.getText().toString());
            }
        });
        getGallery();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void showImage(String imgUrl){
        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, imgUrl);
        startActivity(intent);
    }
    //get the Gallery information from the server
    public boolean getGallery() {
        String url = "http://roblkw.com/msa/getgallery.php";
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", personalEmail);
        new StringPost(this, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        String[] imgUrl = response.trim().split("[,]+");
                        setUpList(imgUrl);
                    }
                }, "Send Screen to server error",
                params);
        return true;
    }

    private void setUpList(String[] imgUrl){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.test,imgUrl);

        listView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Gallery Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.jianan.auggraffiti/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Gallery Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.jianan.auggraffiti/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
