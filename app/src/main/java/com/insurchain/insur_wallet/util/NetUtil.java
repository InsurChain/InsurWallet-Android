package com.insurchain.insur_wallet.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.insurchain.insur_wallet.XApplication;


/**
 * Created by huangshan 2016/8/31
 */
public class NetUtil {

    /**
     * true: net is connected; false net is unconnected
     *
     * @param context
     * @return
     */
    public static boolean checkNetStatus(Context context) {
        return null != getActiveNetwork(context);
    }

    /**
     * get Active NetworkInfo null: net is disable
     *
     * @param context
     * @return
     */
    public static NetworkInfo getActiveNetwork(Context context) {
        if (context == null)
            return null;
        ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr == null)
            return null;

        //mobile 3G Data Network
//        State mobile = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        //wifi
//        State wifi = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        return mConnMgr.getActiveNetworkInfo();
    }

    @SuppressLint("DefaultLocale")
    public static int getDetailNetType() {
        int WIFI = 1, CMWAP = 2, CMNET = 3, ETHERNET = 4;
        int netType = 0;
        ConnectivityManager mConnMgr = (ConnectivityManager) XApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = CMNET;
            } else {
                netType = CMWAP;
            }
//			NetworkInfo mobileInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//.getState();
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
//			NetworkInfo wifiInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//.getState();
            netType = WIFI;
        } else if (nType == ConnectivityManager.TYPE_ETHERNET) {
            netType = WIFI;
        }
//        netType = 2;
        return netType;
    }

    public static String getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) XApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] allNetworkInfo = cm.getAllNetworkInfo();
        String isOK = null;
        for (NetworkInfo ni : allNetworkInfo) {
            if (ni.getState() == State.CONNECTED) {
                isOK = ni.getTypeName();
                break;
            }
        }
        return isOK;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) XApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] allNetworkInfo = cm.getAllNetworkInfo();
        boolean isOK = false;
        for (NetworkInfo ni : allNetworkInfo) {
            if (ni.getState() == State.CONNECTED) {
                isOK = true;
                break;
            }
        }
        return isOK;
    }

//    public static boolean isUserAndNetBothOk() {
//        return !TextUtils.isEmpty(XApplication.user.getName()) && isNetworkAvailable();
//    }
}
