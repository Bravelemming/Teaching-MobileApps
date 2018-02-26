//Jack Daniel Kinne.  Project 3v2.  Mobile Apps CS 480.
package com.kinne.jack.jkp3v2;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.things.contrib.driver.button.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static android.media.MediaScannerConnection.scanFile;

public class Activity2 extends AppCompatActivity {

    private static final String TAG = Activity2.class.getSimpleName();

    //link Handler for running Cloud in bkgd, plus thread so it doesn't block the UI
    private Handler mCloudHandler;
    private HandlerThread mCloudThread;

    private FirebaseDatabase mDatabase;
    mDatabase = FirebaseDatabase.getInstance();

    //image we will be messing with
    ImageView savedPic;
    Bitmap bitmap;

    //button for saving file
    Button saveButton;

    //begin the thread-en-ing.
    mCloudThread = new HandlerThread("CloudThread");
    mCloudThread.start();
    mCloudHandler = new Handler(mCloudThread.getLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        Log.d(TAG, "Doorbell Activity created.");

        //restoring bitmap image in activity!
        bitmap = getIntent().getParcelableExtra("bitmap");
        savedPic = (ImageView) findViewById(R.id.imageView2);
        savedPic.setImageBitmap(bitmap);

        //save a file
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new Activity2.saveAPic());


    }

    class saveAPic implements  Button.OnClickListener{
        @Override
        public void onClick(View view) {
            //TODO: SAVE A FILE!
            Bitmap saveMe = savedPic.getDrawingCache();
            saveImage(saveMe);
            Toast.makeText(getApplicationContext(), "Picture Saved!", Toast.LENGTH_SHORT).show();

        }
    }

    private void saveImage(saveMe){
        //save the image there
        File newFile = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            saveMe.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            scanFile(context, Uri.fromFile(newFile));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //imported class from https://github.com/androidthings/doorbell/blob/master/app/src/main/java/com/example/androidthings/doorbell/DoorbellActivity.java
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

    }

}
