package com.example.jianan.auggraffiti;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//mport com.blundell.tut.cameraoverlay.FromXML;

/**
 * Takes a photo saves it to the SD card and returns the path of this photo to
 * the calling Activity
 *
 * @author paul.blundell
 */
public class PlaceActivity extends Activity implements PictureCallback, LocationListener, SensorEventListener {

    protected static final String EXTRA_IMAGE_PATH = "com.example.jianan.auggraffiti.CameraActivity.EXTRA_IMAGE_PATH";

    private Camera camera;
    private CameraPreview cameraPreview;
    Button btn;
    private Graphique graph;
    TextView txt;
    Calendar cal;
    RatingBar rt;
    Boolean visible = false;
    EditText edi;
    Button btnSave;
    double lng;
    double lat;
    double altitude;
    private LocationManager locationManager;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] gData = new float[3]; // accelerometer
    private float[] mData = new float[3]; // magnetometer
    private float[] rMat = new float[9];
    private float[] iMat = new float[9];
    private float[] orientation = new float[3];
    private int mAzimuth;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not
        // available at all
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        camera = CameraHelper.getCameraInstance();
        if (CameraHelper.cameraAvailable(camera)) {
            initCameraPreview();
        } else {
            finish();
        }

        // btn = (Button) findViewById(R.id.button1);
        graph = (Graphique) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);
        btnSave = (Button) findViewById(R.id.button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setTextColor(Color.RED);
                sendImageToServer(graph.saveCanvasToBitmap());
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", (long) 5000, (float) 0.0, this);
    }


    // Show the camera view on the activity
    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
    }

    // ALWAYS remember to release the camera when you are finished
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        mSensorManager.unregisterListener(this);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public boolean sendImageToServer(String imgString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://roblkw.com/msa/placetag.php";
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", "jianan205@gmail.com");
        params.put("tag_img", imgString);
        params.put("loc_long", String.valueOf(lng));
        params.put("loc_lat", String.valueOf(lat));
        params.put("orient_azimuth", String.valueOf(mAzimuth));
        params.put("orient_altitude", String.valueOf(altitude));
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
                        /*if (response.equals("0")) // when get 0, log in successfully
                            //sendMessage(personEmail);
                            //fail to log in.
                            Log.v("Successfully post image");
                        else
                            Log.v("Error happens when posting the img");*/
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

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Place Page", // TODO: Define a title for the content shown.
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
                "Place Page", // TODO: Define a title for the content shown.
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

    @Override
    public void onLocationChanged(Location location) {
        lng = location.getLongitude();
        lat = location.getLatitude();
        altitude = location.getAltitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] data;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData = event.values.clone();
                break;
            default:
                return;
        }

        if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

