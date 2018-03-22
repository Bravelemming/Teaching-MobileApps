//Jack Daniel Kinne.  Project 4.  Mobile Apps CS 480.
package com.kinne.jack.jkp3v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ListResults extends AppCompatActivity {

    //passed intent from previous activity
    Intent currentIntent;

    //annotation picture, labels and percentage certainty
    String[] labels;
    float[] percentage;
    Bitmap bitmap;

    //image we will be messing with
    ImageView imgTakenPic;

    //return button
    Button returnButton;


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

        //return button
        returnButton = (Button) findViewById(R.id.button_return);
        returnButton.setOnClickListener(new ListResults.ReturntoMain());
        //image view
        imgTakenPic = (ImageView)findViewById(R.id.imageView2);
        imgTakenPic.setImageBitmap(bitmap);

        //three textviews
        TextView textViewResult1 = (TextView)findViewById(R.id.textViewResult1);
        TextView textViewResult2 = (TextView)findViewById(R.id.textViewResult2);
        TextView textViewResult3 = (TextView)findViewById(R.id.textViewResult3);

        //hold concatenated text
        String result1 =labels[0] + " : " + percentage[0];
        String result2 =labels[1] + " : " + percentage[1];
        String result3 =labels[2] + " : " + percentage[2];

        //sets labels and percentages as viewable
        textViewResult1.setText( result1 );
        textViewResult2.setText( result2 );
        textViewResult3.setText( result3 );
    }


    //set up our button to return through an intent
    class ReturntoMain implements  Button.OnClickListener{

        @Override
        public void onClick(View view) {
            //return to MainActivity
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(i);

        }
    }

}
