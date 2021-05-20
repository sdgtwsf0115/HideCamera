package com.wsf.hidecamera;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

/**
 * @author wsf
 * @time 2021/5/20  15:35
 * @des
 */
public class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final String TAG = AutoFocusCallback.class.getName();
    private static final long AUTO_FOCUS_INTERVAL_MS = 1300L; //自动对焦时间

    private Handler mAutoFocusHandler;
    private int mAutoFocusMessage;

    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.mAutoFocusHandler = autoFocusHandler;
        this.mAutoFocusMessage = autoFocusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.e(TAG, "autof focus "+success);
//        if (mAutoFocusHandler != null) {
//            mAutoFocusHandler.sendEmptyMessageDelayed(mAutoFocusMessage,AUTO_FOCUS_INTERVAL_MS);
////            mAutoFocusHandler = null;
//        } else {
//            Log.e(TAG, "Got auto-focus callback, but no handler for it");
//        }
    }
}
