package com.insurchain.insur_wallet;

import android.app.Application;
import android.content.Context;

/**
 * Created by huangshan on 18/2/5.
 */

public class XApplication extends Application {

    private static XApplication instance;
    private static Context mContext;
    public static boolean upgrade = false;

    public static Context getContext() {
        return mContext;
    }

    public static XApplication getInstance() {
        if (instance == null) {
            instance = new XApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
