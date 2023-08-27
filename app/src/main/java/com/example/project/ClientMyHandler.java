package com.example.project;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


class ClientMyHandler extends Handler {
    private final WeakReference<ClientMainActivity> mActivity;

    public ClientMyHandler(ClientMainActivity activity) {
        mActivity = new WeakReference<ClientMainActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        ClientMainActivity activity = mActivity.get();
        if (activity != null) {
            try {
                activity.mLastFrame = (Bitmap) msg.obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    }
}