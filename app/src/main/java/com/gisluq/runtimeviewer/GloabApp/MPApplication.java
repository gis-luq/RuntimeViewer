package com.gisluq.runtimeviewer.GloabApp;

import android.app.Application;
import android.os.StrictMode;


/**
 * 主Application
 * Created by luq on 2017/4/8.
 */

public class MPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 解决Android系统7.0以上遇到exposed beyond app through ClipData.Item.getUri
         * https://blog.csdn.net/FrancisBingo/article/details/78248118
         */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }
}
