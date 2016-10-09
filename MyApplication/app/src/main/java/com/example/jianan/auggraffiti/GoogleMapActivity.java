package com.example.jianan.auggraffiti;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClinet;
    final String tag ="GoogleMapActivity";
    private LocationRequest mLocationRequest;
    private List<Tag> tagList = null;
    public final static String EXTRA_MESSAGE = "com.example.jianan.auggraffiti.MainActivity.MESSAGE";
    private  Marker placeMarker;
    private Double lat =0.0;
    private Double lng =0.0;
    private TextView score = null;

    final int MY_PERMISSION_CODE = 1;
    String personEmail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(googleServiceAvailable()){
            // Toast.makeText(this,"Perfect", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_google_map);
            initMap();
        }else{
            // No Google map layout;
        }
        //set score text view
        score = (TextView) findViewById(R.id.score);

        Button buttonGallery = (Button) findViewById(R.id.gallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When gallery button is clicked, we will switch to another screen
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSignOut = (Button) findViewById(R.id.sign_out);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        //get user's email from main activity
        personEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //send request to get score
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://roblkw.com/msa/getscore.php";

        final Map<String, String> params = new HashMap<String, String>();

        params.put("email", personEmail);
        StringRequest stringRequest = postScoreStringRequest(params, url);
        queue.add(stringRequest);
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    // Check if the google service is available
    public boolean googleServiceAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else{
            Toast.makeText(this,"Cant connect to google play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //goToLocationZoom(33.4363619,-111.927875,30);
//       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//           Log.v(tag,"before if");
//           if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//               Log.v(tag,"not get permission");
//               return;
//           }
//           Log.v(tag,"get permission");
//        }
//        mGoogleMap.setMyLocationEnabled(true);

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSION_CODE);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
        /*use google api client to simplify the permission authorization*/
        mGoogleApiClinet = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClinet.connect();
        mGoogleMap.setOnMarkerClickListener(this);
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSION_CODE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    mGoogleApiClinet = new GoogleApiClient.Builder(this)
//                            .addApi(LocationServices.API)
//                            .addConnectionCallbacks(this)
//                            .addOnConnectionFailedListener(this)
//                            .build();
//                    mGoogleApiClinet.connect();
//                    mGoogleMap.setOnMarkerClickListener(this);
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(this, "The application can't use your location, please set it", Toast.LENGTH_SHORT).show();                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

//    private void goToLocation(double lat, double lng) {
//        LatLng ll = new LatLng(lat,lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
//        mGoogleMap.moveCamera(update);
//    }
//
//    private void goToLocationZoom(double lat, double lng, int zoom) {
//        LatLng ll = new LatLng(lat,lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
//        mGoogleMap.moveCamera(update);
//    }

//    public void geoLocate(View view) throws IOException {
//        EditText et = (EditText) findViewById(R.id.editText);
//        String location = et.getText().toString();
//        Geocoder gc = new Geocoder(this);
//        List<Address> list = gc.getFromLocationName(location,1);
//        Address address = list.get(0);
//        String locality = address.getLocality();
//        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
//        double lat = address.getLatitude();
//        double lng = address.getLongitude();
//        goToLocationZoom(lat, lng,15);
//    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        switch( marker.getTitle()) {
            case "Collect":
                Toast.makeText(this, "Collect has been clicked ",
                    Toast.LENGTH_SHORT).show();
                sendMessage("Collect");
                break;
            case "Place":
                Toast.makeText(this, "Place has been clicked ",
                        Toast.LENGTH_SHORT).show();
                sendMessage("Place");
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){// to show the menu in setting
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//switch the type of the map
        switch(item.getItemId()){
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            Log.v(tag,"before if");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.v(tag,"not get permission");
                Toast.makeText(this,"Please allow permission to access your location in the setting", Toast.LENGTH_LONG).show();
                return;
            }
            Log.v(tag,"get permission");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClinet,mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {//when the location is changed
        if(location == null){
            Toast.makeText(this,"Cant get current location", Toast.LENGTH_LONG).show();
        }else{
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            if(Math.abs(this.lat-lat)>0.000001&&Math.abs(this.lng-lng)>0.000001) {// when both the latitude and longitude is changed more than 0.0000001
                                                                            // to limit the update of the marker
                this.lat = lat;
                this.lng = lng;
                //LatLng ll = new LatLng(lat, lng);
                lat = 33.4194278;
                lng = -111.9395818;
                LatLng ll = new LatLng(lat,lng);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);//use cameraupate to focus the screen to the current position
                mGoogleMap.animateCamera(update);
                setPlaceMarker(lat, lng);// we have place marker and collect mark.
                //get the tag information here
                RequestQueue queue = Volley.newRequestQueue(this);
                //post request to get the tags nearby
                String url = "http://roblkw.com/msa/neartags.php";
                final Map<String, String> params = new HashMap<String, String>();
                if(personEmail==null) {
                    Intent intent = getIntent();
                    personEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
                }
                params.put("email", personEmail);
                params.put("loc_long", lng.toString());
                params.put("loc_lat", lat.toString());
                // Request a string response from the provided URL.
                StringRequest stringRequest = postTagNearByRequest(params, url);
                queue.add(stringRequest);
            }
        }
    }
    private void setPlaceMarker(double lat, double lng) {
        if(placeMarker != null){
            placeMarker.remove();
        }
        MarkerOptions optionsPlace = new MarkerOptions()
                .title("Place")
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pm))
                .position(new LatLng(lat,lng))
                .snippet("Clicking me to place a tag");
        placeMarker = mGoogleMap.addMarker(optionsPlace);
    }
    private Marker setCollectMarker(LatLng ll/*,  Marker collectMarker*/) {
//        if(collectMarker != null){
//            collectMarker.remove();
//        }
        MarkerOptions optionsCollect = new MarkerOptions()
                .title("Collect")
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.c))
                .position(ll)
                .snippet("Clicking me to collect a tag");
        return mGoogleMap.addMarker(optionsCollect);
    }
//post request to get the tags nearby
    private StringRequest postTagNearByRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    // no idea why enter in this function 3 times when just sending GPS information just once.
                    public void onResponse(String response) {
                        //The format of the response is "tagId,Latitude,Longitude"
                        String[] tagLoc = response.trim().split("[,]+");
                        //First we assume the format of the response never changed and the response we get is always correct
                        //then we can get the numb int numTag = 0;
                        int numTag = tagLoc.length%3==0?tagLoc.length/3:-1;
                        Log.v("Collect tag","the response is "+response);
                        if(tagList == null){
                            tagList = new LinkedList<Tag>();
                        }
                        int i = numTag;
                        Log.v("Collect tag","the number of tag nearby is "+String.valueOf(i));
                        while(--i >=0) {//we have tags near by
                            //  double lat = Double.valueOf(tagLoc[numTag * 3 + 1]);
                            // LatLng ll = new LatLng(Double.valueOf(tagLoc[numTag * 3 + 1]), Double.valueOf(tagLoc[numTag * 3 + 2]));
                            tagList.add(new Tag(Integer.valueOf(tagLoc[i * 3]), new LatLng(Double.valueOf(tagLoc[i * 3 + 2]), Double.valueOf(tagLoc[i * 3 + 1]))));
                            setCollectMarker(tagList.get(numTag - i- 1).ll);
                        }
                        //how to check if the response is correct?
                        //First the number is in ascending order.followed by 2 float number.
                        //Here we assume the number of the location must be integer. And the location must be float.
                        //If not something is wrong we need to throw that location away.
                        //  Log.v(tag,"the length of the tagLoc is" + String.valueOf(tagLoc.length));

                        if(response==null){
                            Log.v(tag,"response is null");
                        }
                        else if(response.equals("0")) {
                            Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Log.v(tag,response);
                        }
                            //mTextView.setText("Fail to sign in");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }
//post request to get score
    private StringRequest postScoreStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    // no idea why enter in this function 3 times when just sending GPS information just once.
                    public void onResponse(String response) {
                        Log.v(tag,response);
                        if(score == null){
                            score = (TextView) findViewById(R.id.score);
                        }
                        score.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }
    public void sendMessage(String activity) {
        Intent intent = null;
        switch (activity){
            case "Collect":
                 intent = new Intent(getApplicationContext(), CollectActivity.class);
                break;
            case "Place":
                intent = new Intent(getApplicationContext(), PlaceActivity.class);
                break;
        }
        if(intent != null) {
            //EditText editText = (EditText) findViewById(R.id.edit_message);
            intent.putExtra(EXTRA_MESSAGE, "");
            startActivity(intent);
        }

    }
}
