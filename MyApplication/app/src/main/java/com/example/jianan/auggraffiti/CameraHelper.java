package com.example.jianan.auggraffiti;

import android.hardware.Camera;

/**
 * Created by Jianan on 9/22/2016.
 * To check if the camera is available in the device.
 */
public class CameraHelper {
    public static boolean cameraAvailable(Camera camera) {
        return camera != null;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available or doesn't exist
        }
        return c;
    }

}
