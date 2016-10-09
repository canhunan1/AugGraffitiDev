package com.example.jianan.auggraffiti;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CollectActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private TextView hello;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        //hello = (TextView) findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.tag_image);

       setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not
        // available at all
        camera = CameraHelper.getCameraInstance();
        if (CameraHelper.cameraAvailable(camera)) {
            initCameraPreview();
        } else {
            finish();
        }
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

        params.put("tag_id", "1");
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

//

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
}
