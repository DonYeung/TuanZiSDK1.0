package com.loanhome.lib.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * @Description Created by Don on 2019/7/3
 */
public class CameraHandlerThread extends HandlerThread {
    private static final String TAG = "CameraHandlerThread";
    public Handler mHandler;
    private ICamera mICamera;
    private Context mContext;



    public CameraHandlerThread(String name, Context context) {
        super(name);
        start();
        mContext =context;
        mHandler = new Handler(getLooper());
    }

    synchronized void notifyCameraOpened() {
        notify();
    }

    public ICamera openCamera() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mICamera = new ICamera(false);
                mICamera.openCamera(mContext);
                Log.i(TAG, "run: "+Thread.currentThread().getName());
                notifyCameraOpened();
            }
        });
        try {
            wait();
        } catch (InterruptedException e) {
            Log.i(TAG, "wait was interrupted");
        }
        return mICamera;
    }

}
