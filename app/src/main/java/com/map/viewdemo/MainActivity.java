package com.map.viewdemo;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.map.viewdemo.views.LongClickProgressView;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.tv_progress);

        LongClickProgressView longClickProgressView = findViewById(R.id.btn_long_click_finish);
        longClickProgressView.setRingColor(Color.parseColor("#000000"));
        longClickProgressView.setCenterColor(Color.parseColor("#000000"));
        longClickProgressView.setOnLongClickStateListener(new LongClickProgressView.OnLongClickStateListener() {
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Finish!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(float progress) {
                textView.setText(progress + "%");
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Cancel!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}