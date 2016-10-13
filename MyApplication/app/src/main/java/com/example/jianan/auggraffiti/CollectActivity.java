package com.example.jianan.auggraffiti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
/*
* This activity is used to collect the tag.
* Similar as the place activity using accelerometer and magnetometer
* */
public class CollectActivity extends AppCompatActivity implements SensorEventListener{
    private Camera camera;
    private CameraPreview cameraPreview;
    private ImageView imageView = null;
    private Integer orientation_azimuth = 0 ;
    private Integer orientation_altitude;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private int screenWidth;
    private int screenHeight;
    private String tagId;
    private float[] gData = new float[3]; // accelerometer
    private float[] mData = new float[3]; // magnetometer
    private float[] rMat = new float[9];
    private float[] iMat = new float[9];
    private float[] orientation = new float[3];
    private int azimuth;
    private Button buttonCollect;
    private String personEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        buttonCollect = (Button) findViewById(R.id.button_collect);
        buttonCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCollect.setTextColor(Color.RED);
                sendScreenToServer(saveScreenToBitmap());
            }
        });
        Intent intent = getIntent();
        tagId = intent.getStringExtra(GoogleMapActivity.TAGID_MESSAGE);
        personEmail = intent.getStringExtra(GoogleMapActivity.PERSONAL_EMAIL);

        camera = CameraHelper.getCameraInstance();
        if (CameraHelper.cameraAvailable(camera)) {
            initCameraPreview();
        } else {
            finish();
        }
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        initScreenSize();

    }
    /*
       * To get the size of the screen
       * */
    private void initScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
        findImgFromServer();
    }

    private void loadImg(String url){
        Picasso.with(this).load(url).into(imageView);
    }

    /*
   * Find the tags
   * */
    private boolean findImgFromServer(){
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://roblkw.com/msa/findtag.php";

        final Map<String, String> params = new HashMap<String, String>();

        params.put("tag_id", tagId);
        StringRequest stringRequest = postFindStringRequest(params, url);
        queue.add(stringRequest);
        return true;
    }

    private StringRequest postFindStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    // no idea why enter in this function 3 times when just sending GPS information just once.
                    public void onResponse(String response) {
                        if(response!=null){
                            String[] tagInfo = response.trim().split("[,]+");
                            loadImg(tagInfo[0]);
                            orientation_azimuth = Integer.valueOf(tagInfo[1]);
                            orientation_altitude = Integer.valueOf(tagInfo[2]);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }

    /*
    * Place the tag on the screen.
    * Put the tag image to the right place
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
        if(imageView == null){
            imageView = (ImageView)findViewById(R.id.tag_image);
        }
        ViewGroup.MarginLayoutParams imageMargin = new ViewGroup.MarginLayoutParams(
                imageView.getLayoutParams());
        float diff = (float)Math.abs(orientation_azimuth-azimuth);
        float factor;
        if(diff>40)
            factor = -(float)Math.exp(-diff/1000.0)+1;
        else
            factor = (float)0.5;
        RelativeLayout.LayoutParams imageLayout = new RelativeLayout.LayoutParams(imageMargin);
        imageLayout.height = (int)(screenHeight*factor);
        imageLayout.width =  (int)(screenWidth*factor);
        imageView.setLayoutParams(imageLayout);

//        if(Math.abs(event.values[0] - orientation_azimuth)<1){
//            imageView.set;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
    * Send the screen shot to the server
    * @prama String imgString  the string after base64 compressed
    * */
    public void sendScreenToServer(String imgString) {
        String url = "http://roblkw.com/msa/collecttag.php";

        final Map<String, String> params = new HashMap<String, String>();

        params.put("email", personEmail);
        params.put("tag_id", tagId);
        params.put("collect_img", imgString);
        new StringPost(this, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        if(response.equals('0')) {
                            Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                            startActivity(intent);
                        }
                    }
                }, "Send Screen to server error",
        params);
        Toast.makeText(this,"The tag is collected", Toast.LENGTH_SHORT).show();
    }
    /*
    * Convert the screen shot in the cache to the bitmap
    * */
    private String saveScreenToBitmap(){
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap == null){
            return null;
        }
        else{
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            return base64;
        }
    }


}
