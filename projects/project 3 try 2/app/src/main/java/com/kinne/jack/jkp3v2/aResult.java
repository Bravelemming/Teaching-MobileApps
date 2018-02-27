package com.kinne.jack.jkp3v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;



public class aResult  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_result);
        TextView tv = (TextView) findViewById(R.id.tv);
        Intent intent = getIntent();
    }
}