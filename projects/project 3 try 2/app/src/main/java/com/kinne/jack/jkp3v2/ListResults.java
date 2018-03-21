//Jack Daniel Kinne.  Project 4.  Mobile Apps CS 480.
package com.kinne.jack.jkp3v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ListResults extends AppCompatActivity {

    //passed intent from previous activity
    Intent currentIntent;

    //annotation picture, labels and percentage certainty
    String[] labels;
    float[] percentage;
    Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_results);

        //capture results from passed intent
        currentIntent = getIntent();
        Bundle bundle = currentIntent.getExtras();

        //save results locally
        labels = bundle.getStringArray("labels");
        percentage = bundle.getFloatArray("percentage");
        bitmap = (Bitmap) bundle.get("picture");


    }
}

//TODO: make three text boxes that fade in over a number of seconds.
// a button that returns you to the first activity
// something else??  maybe a screen capture and copy to clipboard!
