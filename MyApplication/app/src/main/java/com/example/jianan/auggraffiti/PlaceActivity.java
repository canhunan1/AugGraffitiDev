package com.example.jianan.auggraffiti;

import android.content.Intent;
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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * When user clicked on the P on the screen of GoogleMapActivity, they can be directed to this activity.
 * This activity is used to draw tag with camera is on.
 * Compress the drawing tag into Base64 and send it to the server.
 * @author Jianan
 */
public class PlaceActivity extends AppCompatActivity implements PictureCallback,LocationListener, SensorEventListener {
    private Camera camera;
    private CameraPreview cameraPreview;
    private GraphView graph;
    Button btnSave;
    double lng;
    double lat;
    double altitude;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gData = new float[3]; // accelerometer
    private float[] mData = new float[3]; // magnetometer
    private float[] rMat = new float[9];
    private float[] iMat = new float[9];
    private float[] orientation = new float[3];
    private int azimuth;
    String personEmail = null;
    /**
    * Constructor
    *Setup the activity; Check if the camera is available.
    *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Intent intent = getIntent();
        personEmail = intent.getStringExtra(GoogleMapActivity.PERSONAL_EMAIL);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        camera = CameraHelper.getCameraInstance();
        if (CameraHelper.cameraAvailable(camera))
            initCameraPreview();
        else
            finish();
        graph = (GraphView) findViewById(R.id.graph);
        btnSave = (Button) findViewById(R.id.button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setTextColor(Color.RED);
                sendImageToServer(graph.saveCanvasToBitmap());
            }
        });

    }
    /*
    * Register the accelerometer and magnetometer in the onResume as the documentation in android developer
    * */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
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

    /*
    * Release the camera when you are finished
    * Unregister the camera when the activity is paused
    * */
    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        sensorManager.unregisterListener(this);
    }


    public boolean sendImageToServer(String imgString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://roblkw.com/msa/placetag.php";
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", personEmail);
        params.put("tag_img", imgString);
        params.put("loc_long", String.valueOf(lng));
        params.put("loc_lat", String.valueOf(lat));
        params.put("orient_azimuth", String.valueOf(azimuth));
        params.put("orient_altitude", String.valueOf(altitude));
        new StringPost(this, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                    }
                }, "Send Screen to server error",
                params);
       /* StringRequest stringRequest = postPlaceStringRequest(params, url);
        queue.add(stringRequest);*/
        Toast.makeText(this,"The tag is sent to the server", Toast.LENGTH_SHORT).show();
        return true;
    }
    
    /*
    * Called when any sensor is changed.
    * Get the data from accelerometer and the magnetometer
    * */
    @Override
    public void onSensorChanged(SensorEvent event) {
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
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    /*
        * Called when the location is changed.
        * Get the location from the android location sensor.
        * */
    @Override
    public void onLocationChanged(Location location) {
        lng = location.getLongitude();
        lat = location.getLatitude();
        altitude = location.getAltitude();
        Log.v("longitude", String.valueOf(lng));
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
}

