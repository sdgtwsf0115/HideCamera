package com.wsf.hidecamera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wsf
 * @time 2021/5/19  15:13
 * @des
 */
public class PhotoHandler implements Camera.PictureCallback {
    private final Context context;
    private String FILE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/hideimage/";
    TakeResultListener takeResultListener;

    public PhotoHandler(Context context, TakeResultListener takeResultListener) {
        this.context = context;
        this.takeResultListener = takeResultListener;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFile = getOutputMediaFile();

        if (pictureFile == null) {

            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }
        Log.e("PhotoHandler", "文件路径: " + pictureFile.getPath());

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            takeResultListener.takeResult(pictureFile.getAbsolutePath());
            Log.e("PhotoHandler", "图片保存完成");
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File getOutputMediaFile() {
        Log.e("PhotoHandler", "要保存文件路径: " + FILE_PATH);
        File mediaStorageDir = new File(FILE_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("PhotoHandler", "failed to create directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(FILE_PATH,
                "IMG_" + timeStamp + ".jpg");
        try {
            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("PhotoHandler", "文件路径创建完成");
        return mediaFile;
    }

    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        void takeResult(String result);
    }

}
