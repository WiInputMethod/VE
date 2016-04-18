package com.example.daofa.test;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.channels.Selector;

public class MainActivity extends AppCompatActivity {

    private Button bt;
    int color= Color.RED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=(Button)findViewById(R.id.bt_test);
        bt.setBackgroundResource(R.drawable.bt_bg2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GradientDrawable myGrad = (GradientDrawable)bt.getBackground();
                myGrad.setColor(color);
                //bt.setBackgroundColor(color);
            }
        });
    }
}
