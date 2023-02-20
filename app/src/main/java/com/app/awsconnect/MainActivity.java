package com.app.awsconnect;

import static com.app.awsconnect.utils.ConstantNumber.TAG;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.awsconnect.manager.ActivityManager;
import com.app.awsconnect.manager.AwsManager;
import com.app.awsconnect.view.LoadingDialogFragment;

public class MainActivity extends AppCompatActivity {
    private Button btnConnect;
    private TextView txtConnectStatus;
    private TextView txtConnectDetail;
    private LoadingDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManager.setMainActivity(this);
        initUI();

    }

    private void initUI() {
        txtConnectStatus = findViewById(R.id.txt_connect_status);
        txtConnectDetail = findViewById(R.id.txt_connect_times);
        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoadingDialog();
                changeTextInfo();
                AwsManager manager = new AwsManager(ActivityManager.getMainActivity());
            }
        });
    }
    /**
     * 概要：バックグラウンドから通知を受け取るためのThread
     */
    private void changeTextInfo() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @UiThread
            @Override
            public void handleMessage(Message msg) {
                Message message = new Message();
                message.copyFrom(msg);
                if (msg.what == 1234) {
                    //指定されたぐるぐるダイアログを削除する
                    txtConnectStatus.setText(msg.obj.toString().trim());
                    txtConnectDetail.setText("Schedule Reconnect attempt " + msg.arg1 + " of 10");
                }
            }
        };
        ActivityManager.setChangeInfoTextHandler(handler);

    }

    /**
     * 概要：ぐるぐるダイアログを削除するのを実行
     */
    public void removeLoadingDialog() {
        LoadingDialogFragment.dismiss(TAG);
    }
    /**
     * 概要：ぐるぐるダイアログを表示するのを実行
     */
    public void startLoadingDialog() {
        // ぐるぐるダイアログを表示する
        dialogFragment = new LoadingDialogFragment();
        ActivityManager.setDialogFragment(dialogFragment);
        dialogFragment.setContentView(R.layout.dialog_progress)
                .setNeedProgress(true)
                .show(getFragmentManager(), TAG);
    }
}