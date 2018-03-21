//Jack Daniel Kinne.  Project 4.  Mobile Apps CS 480.
//'LabelDetection.java' uses code adapted from Google.
//API call was adapted from Sam Alston and Jared Conn.

package com.kinne.jack.jkp3v2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback{

    //button for taking pictures
    Button btnpic;
    //button for google vision
    Button btndetect;
    //image we will be messing with
    ImageView imgTakenPic;
    Bitmap bitmap;

    //camera
    private static final int CAM_REQUEST=1313;
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    //running a thread
    private Handler mCloudHandler;
    private HandlerThread mCloudThread;

    //tag and progressbar
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar progressBar;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //taking a picture: button and event
        btnpic = (Button) findViewById(R.id.takePhotoButton);
        imgTakenPic = (ImageView)findViewById(R.id.imageView);
        btnpic.setOnClickListener(new btnTakePhotoClicker());

        //button to make a request of google vision
        btndetect = (Button) findViewById(R.id.button_detect);
        btndetect.setOnClickListener(new btnMakeRequest());

        //progress bar: default is not visible
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //thread and handler
        mCloudThread = new HandlerThread("CloudThread");
        mCloudThread.start();
        mCloudHandler = new Handler(mCloudThread.getLooper());

    }

    //convert a drawable resource to a bitmap -- for default
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmaps = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmaps);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmaps;
    }

    //when camera has taken a picture.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAM_REQUEST) {

                // Get Extra from the intent
                Bundle extras = data.getExtras();
                // Get the returned image from extra
                Bitmap bmp = (Bitmap) extras.get("data");
                // set image
                imgTakenPic.setImageBitmap(bmp);
                bitmap = bmp;

                //check for failure of camera
                if (bmp == null){
                    Toast.makeText(getApplicationContext(), "camera crashed! oh no!", Toast.LENGTH_LONG).show();
                }
            }
    }

    //set up our button to take a photo
    class btnTakePhotoClicker implements  Button.OnClickListener{

        @Override
        public void onClick(View view) {
            //I WILL TRY.....
            try {
                showCameraPreview();
            }
            //...TO CATCH YOU..
            catch (Exception e){
                int foo = 5;
            }
        }
    }

    //handle a request for camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera();
            } else {
                // Permission request was denied.
                Toast.makeText(getApplicationContext(), "permission for camera was denied.", Toast.LENGTH_LONG).show();
            }
        }

    }

    //before we launch the camera, lets make sure we have permission.
    private void showCameraPreview() {
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            startCamera();
        }
        else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }

    }

    //Permission request
    private void requestCameraPermission() {
        // if permission not granted, show error.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(getApplicationContext(), "permission not granted for camera!", Toast.LENGTH_LONG).show();

        }
        else {
            // Request the permission. Result received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    //start the camera!
    private void startCamera() {

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAM_REQUEST);
    }


    //make request to Google for label detection: call LabelDetection.annotateImage
    class btnMakeRequest implements  Button.OnClickListener{
        @Override
        public void onClick(View view) {

            if(bitmap!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] photoData = stream.toByteArray();

                mCloudHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "sending image to cloud vision");
                        // annotate image by uploading to Cloud Vision API
                        try {

                            Map<String, Float> annotations = LabelDetection.annotateImage(photoData);
                            Log.d(TAG, "cloud vision annotations:" + annotations);
                            if (annotations != null) {
                                //breaking here, because we are attempting to print to the ui from a thread!
                                printMap(annotations);
                            }
                        } catch (IOException e) {

                            Log.e(TAG, "Cloud Vison API error: ", e);
                        }
                    }
                });
                //show the progressbar!
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "didn't select an image to annotate!", Toast.LENGTH_LONG).show();
            }
        }
    }


    //toast results of Google Vision
    public void printMap(Map<String, Float> mp) {
        List<Map.Entry<String,Float>> entries = new ArrayList<Map.Entry<String,Float>>(
                mp.entrySet()
        );
        Collections.sort(
                entries,
                new Comparator<Map.Entry<String,Float>>() {
                    public int compare(Map.Entry<String,Float> a, Map.Entry<String,Float> b) {
                        return Float.compare(b.getValue(), a.getValue());
                    }
                }
        );
        for (Map.Entry<String,Float> pair : entries) {
            // This loop prints entries. You can use the same loop
            // to get the keys from entries, and add it to your target list.
            //System.out.println(e.getKey()+":"+e.getValue());
            Toast.makeText(getApplicationContext(),pair.getKey() + " " + pair.getValue() + "% certain",Toast.LENGTH_SHORT).show();

        }

        //vanish the progressbar.  Poof!
        //progressBar.setVisibility(View.GONE);

    }



    //cleanup threads
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCloudThread.quit();
    }
}