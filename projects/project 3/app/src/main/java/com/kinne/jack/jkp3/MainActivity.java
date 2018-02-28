//Project 3, Jack Daniel Kinne, CS 480
// This project is intended as an example of an API call to google's cloud vision
// currently, the thought is to connect directly.  firebase is the secondary option.

package com.kinne.jack.jkp3;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;



public class MainActivity extends AppCompatActivity {

    private Button btnpic;
    private Button btngal;
    private Button btndetect;
    private ImageView imgTakenPic;
    private Bitmap bitmap;

    //camera
    private static final int CAM_REQUEST = 1313;
    //gallery
    public static final int PICK_IMAGE = 1;

    //running a thread
    private Handler mCloudHandler;
    private Handler progressHandler;
    private Runnable runnable;
    private HandlerThread mCloudThread;

    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCurrentPhotoPath;
    private ProgressBar progressBar;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgTakenPic = (ImageView)findViewById(R.id.imageView);

        btnpic = (Button) findViewById(R.id.button_camera);
        btnpic.setOnClickListener(new btnTakePhotoClicker());

        btngal = (Button) findViewById(R.id.button_gallery);
        btngal.setOnClickListener(new btnOpenGalleryClicker());

        btndetect = (Button) findViewById(R.id.button_detect);
        btndetect.setOnClickListener(new btnMakeRequest());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //thread and handler
        mCloudThread = new HandlerThread("CloudThread");
        mCloudThread.start();
        mCloudHandler = new Handler(mCloudThread.getLooper());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            super.onActivityResult(requestCode, resultCode, data);
            //if cam request, get image from camera
            if(requestCode == CAM_REQUEST && resultCode == RESULT_OK){
                //cameraResult(data);
                if(mCurrentPhotoPath!=null && mCurrentPhotoPath!="")
                {
                    File f = new File(mCurrentPhotoPath);
                    if(f.exists())
                    {
                        Drawable d = Drawable.createFromPath(mCurrentPhotoPath);
                        imgTakenPic.setImageDrawable(d);
                        bitmap = getBitmap(mCurrentPhotoPath);
                        //compress bitmap for speed
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    }
                }
            }

            //if picking image from gallery we have to convert URI to data via inputStream
            if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                try {
                    galleryResult(data);
                } catch(FileNotFoundException e) {

                }
            }
        } catch(Exception e) {
            //don't break if the user sends no image
            Toast.makeText(getApplicationContext(),"You didn't send an image.",Toast.LENGTH_LONG).show();
        }
    }

    private void galleryResult(Intent data) throws FileNotFoundException {
        //handle the response from gallery picture selection
        Uri imageUri = data.getData();
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        bitmap = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //try to make it smaller since the response time is long with HQ images
        bitmap.compress(Bitmap.CompressFormat.PNG, 25, stream);

        imgTakenPic.setImageBitmap(bitmap);
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 600000; // .6MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    class btnTakePhotoClicker implements Button.OnClickListener{
        //set Open camera button intent
        @Override
        public void onClick(View view) {
            dispatchTakePictureIntent();
        }
    }

    class btnOpenGalleryClicker implements  Button.OnClickListener{
        //set Open Gallery button intent
        @Override
        public void onClick(View view) {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_IMAGE);
        }
    }
    //credit to Google for the request
    class btnMakeRequest implements  Button.OnClickListener{
        //make request to Google for label detection, call LabelDetection.annotateImage
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
                Toast.makeText(getApplicationContext(), "didn't select an image to edit!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "PA3",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAM_REQUEST);
            }
        }
    }

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

        //vanish the progressbar!
        progressBar.setVisibility(View.GONE);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCloudThread.quit();
    }
}