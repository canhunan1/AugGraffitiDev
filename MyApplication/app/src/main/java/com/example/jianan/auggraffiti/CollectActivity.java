package com.example.jianan.auggraffiti;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CollectActivity extends AppCompatActivity implements SensorEventListener{
    private Camera camera;
    private CameraPreview cameraPreview;
    //private TextView hello;
    private ImageView imageView = null;
    private Integer orientation_azimuth = 0 ;
    private Integer orientation_altitude;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private int screenWidth;
    private int screenHeight;
    private String tagId;
    private float[] gData = new float[3]; // accelerometer
    private float[] mData = new float[3]; // magnetometer
    private float[] rMat = new float[9];
    private float[] iMat = new float[9];
    private float[] orientation = new float[3];
    private int mAzimuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        //hello = (TextView) findViewById(R.id.textView);

       setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not
        // available at all
        Intent intent = getIntent();
        tagId = intent.getStringExtra(GoogleMapActivity.TAGID_MESSAGE);
        Log.v("The collect ID is "+String.valueOf(tagId));
        camera = CameraHelper.getCameraInstance();
        if (CameraHelper.cameraAvailable(camera)) {
            initCameraPreview();
        } else {
            finish();
        }
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
        findImgFromServer();
    }
    private boolean findImgFromServer(){
        // personEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //send request to get score
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://roblkw.com/msa/findtag.php";

        final Map<String, String> params = new HashMap<String, String>();

        params.put("tag_id", tagId);
        StringRequest stringRequest = postScoreStringRequest(params, url);
        queue.add(stringRequest);
        return true;
    }
    private void loadImg(String url){
        Picasso.with(this).load(url).into(imageView);
    }



    private StringRequest postScoreStringRequest(final Map<String,String> params, final String url) {
        return new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    // no idea why enter in this function 3 times when just sending GPS information just once.
                    public void onResponse(String response) {
                        if(response!=null){
                            String[] tagInfo = response.trim().split("[,]+");
                            loadImg(tagInfo[0]);
                            orientation_azimuth = Integer.valueOf(tagInfo[1]);
                            Log.v("oriazimuth is" + orientation_azimuth);
                            orientation_altitude = Integer.valueOf(tagInfo[2]);
/*
//                            try {
//                                URL url = new URL(tagInfo[0]);
//                                Log.v("The image url is: "+url.toString());
//
//
////                                try {
////                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
////                                    imageView.setImageBitmap(bmp);
////                                } catch (IOException e) {
////                                    e.printStackTrace();
////                                }
//                            }catch (NetworkOnMainThreadException e){
//                                Log.v("networkOnMainThreadException");
//                            }catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            }

//*/

                        }
                        else{
                            Log.v("the response is null");
                        }
//                        android.util.Log.v(tag,response);
//                        if(score == null){
//                            score = (TextView) findViewById(R.id.score);
//                        }
//                        score.setText(response);
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
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
        if(imageView == null){
            imageView = (ImageView)findViewById(R.id.tag_image);
        }
        ViewGroup.MarginLayoutParams imageMargin = new ViewGroup.MarginLayoutParams(
                imageView.getLayoutParams());
        int size;
        Log.v("the orientation is "+Math.abs(orientation_azimuth-mAzimuth));

        float diff = (float)Math.abs(orientation_azimuth-mAzimuth);
        float factor;
        if(diff>40)
            factor = -(float)Math.exp(-diff/1000.0)+1;
        else
            factor = (float)0.5;
        Log.v("factor  "+factor);
        Log.v("the ori is " + event.values[1]);
        //imageMargin.setMargins( 20, 10, 0, 0);
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
}
