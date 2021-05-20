package com.wsf.hidecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    private Intent intent;
    private Context mContext;
    private RelativeLayout content;
    private View btnStart;
    private View btnStop;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        registerReceiver(broadcastReceiver, new IntentFilter(
//                "click"));
        mContext = MainActivity.this;
//        surfaceview = findViewById(R.id.sview);
        content = findViewById(R.id.content);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        floatingWindow();
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void floatingWindow() {
        intent = new Intent(mContext, FloatingWindowService.class);
        if (!Settings.canDrawOverlays(mContext)) {
            Toast.makeText(mContext, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName())), 0);
        } else {

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(mContext)) {
                Toast.makeText(mContext, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
                startService(intent);
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if (!FloatingWindowService.isStarted){
                    startService(intent);
                }
                break;
            case R.id.btn_stop:
                stopService(intent);
                break;
        }
    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            takephoto();
//        }
//    };
}
