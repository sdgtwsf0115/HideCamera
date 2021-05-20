package com.wsf.hidecamera;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * @Time 2020/4/6 18:33
 */
public class FloatingWindowService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private Camera camera = null;
    private View view;
    private SurfaceView surfaceview;
    private SurfaceHolder holder;
    private AutoFocusCallback autoFocusCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("FloatingWindowService", "onCreate");
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = dp2px(50);
        layoutParams.height = dp2px(50);
        view = LayoutInflater.from(this).inflate(R.layout.surfaceview, null);
        surfaceview = view.findViewById(R.id.sview);
        ImageView button = view.findViewById(R.id.button);
        view.setOnTouchListener(new FloatingOnTouchListener());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takephoto();
            }
        });
        initCamera();
    }

    /**
     * 初始化相机相关
     */
    @SuppressLint("HandlerLeak")
    private void initCamera() {
        if (camera == null)
            camera = getCameraInstance();
        if (camera != null) {
            holder = surfaceview.getHolder();
            autoFocusCallback = new AutoFocusCallback();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("FloatingWindowService", "onStartCommand");
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 添加视图
     */
    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                windowManager.addView(view, layoutParams);
            }
        }
    }

    /**
     * 触摸移动
     */
    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
        //销毁时放弃对相机的持有
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        windowManager.removeViewImmediate(view);
    }

    /**
     * 拍照
     */
    public void takephoto() {
        if (camera != null) {
            try {
                //设置预览
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                //聚焦,不然相机只会聚焦一次,拍出的照片模糊
                camera.autoFocus(autoFocusCallback);
                final PhotoHandler photoHandler = new PhotoHandler(getApplicationContext(), new PhotoHandler.TakeResultListener() {
                    @Override
                    public void takeResult(String result) {
                        Log.e("FloatingWindowService", "图片保存完成");
                        Toast.makeText(FloatingWindowService.this, "success",
                                Toast.LENGTH_LONG).show();
                        camera.stopPreview();
                    }
                });
                camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        camera.takePicture(null, null, photoHandler);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取相机
     *
     * @return
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    public int dp2px(float dipValue) {
        float m = getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }
}
