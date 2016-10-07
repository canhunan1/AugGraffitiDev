package com.example.jianan.auggraffiti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

//mport com.blundell.tut.cameraoverlay.FromXML;

/**
 * Takes a photo saves it to the SD card and returns the path of this photo to
 * the calling Activity
 *
 * @author paul.blundell
 *
 */
public class PlaceActivity extends Activity implements PictureCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not
        // available at all
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
                Log.v("Click the button");
                graph.saveCanvasToBitmap();
            }
        });
//        txt = (TextView) findViewById(R.id.date);
//
//        rt = (RatingBar) findViewById(R.id.ratingBar1);
//        edi = (EditText) findViewById(R.id.editText1);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (!visible) {
//
//                    graph.setVisibility(View.VISIBLE);
//                    rt.setVisibility(View.VISIBLE);
//                    edi.setVisibility(View.VISIBLE);
//                    txt.setVisibility(View.VISIBLE);
//                    txt.setText("Camera View :001 /10/04/2014 thread ");
//
//                    txt.setText("Camera View :001 /10/04/2014");
//                    btn.setText("Masquer Graphique");
//
//                } else {
//
//                    graph.setVisibility(View.INVISIBLE);
//                    txt.setVisibility(View.INVISIBLE);
//                    rt.setVisibility(View.INVISIBLE);
//                    edi.setVisibility(View.INVISIBLE);
//                    btn.setText("Afficher Graphique");
//                }
//                visible = !visible;
//            }
//        });

    }



    // Show the camera view on the activity
    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
    }

    public void onCaptureClick(View button) {
        // Take a picture with a callback when the photo has been created
        // Here you can add callbacks if you want to give feedback when the
        // picture is being taken
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d("Picture taken");
        String path = savePictureToFileSystem(data);
        setResult(path);
        finish();
    }

    private static String savePictureToFileSystem(byte[] data) {
        File file = MediaHelper.getOutputMediaFile();
        MediaHelper.saveToFile(data, file);
        return file.getAbsolutePath();
    }

    private void setResult(String path) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
    }

    // ALWAYS remember to release the camera when you are finished
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}

