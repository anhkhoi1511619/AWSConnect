package com.app.awsconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.awsconnect.manager.ActivityManager;
import com.app.awsconnect.manager.AwsManager;

public class MainActivity extends AppCompatActivity {
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManager.setMainActivity(this);
        initUI();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initUI() {
        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AwsManager manager = new AwsManager(ActivityManager.getMainActivity());
            }
        });
    }
}