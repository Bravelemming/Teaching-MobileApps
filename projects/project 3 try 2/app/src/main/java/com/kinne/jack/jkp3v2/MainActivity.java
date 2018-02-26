package com.kinne.jack.jkp3v2;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.ImageReader;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // link Handler for running Cloud tasks in the background
    private Handler mCloudHandler;
    // thread for running Cloud tasks that shouldn't block the UI
    private HandlerThread mCloudThread;

    private FirebaseDatabase mDatabase;
    mDatabase = FirebaseDatabase.getInstance();

    //begin the thread-en-ing.
    mCloudThread = new HandlerThread("CloudThread");
    mCloudThread.start();
    mCloudHandler = new Handler(mCloudThread.getLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            final DatabaseReference log = mDatabase.getReference("logs").push();
            String imageStr = Base64.encodeToString(imageBytes, Base64.NO_WRAP | Base64.URL_SAFE);
            // upload image to firebase
            log.child("timestamp").setValue(ServerValue.TIMESTAMP);
            log.child("image").setValue(imageStr);

            mCloudHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "sending image to cloud vision");
                    // annotate image by uploading to Cloud Vision API
                    try {
                        Map<String, Float> annotations = CloudVisionUtils.annotateImage(imageBytes);
                        Log.d(TAG, "cloud vision annotations:" + annotations);
                        if (annotations != null) {
                            log.child("annotations").setValue(annotations);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Cloud Vison API error: ", e);
                    }
                }
            });



}
