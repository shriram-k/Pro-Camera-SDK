package com.github.shriram_k.procamerasdk;

import android.content.Context;
import android.content.Intent;

public class ProCameraSDK {
    private Context context;
    public static ProCameraSDK instance;

    public ProCameraSDK(Context context) {
        this.context = context;
    }

    public void startCamera() {
        Intent intent = new Intent(context, ProCameraSDKActivity.class);
        context.startActivity(intent);
    }
}
