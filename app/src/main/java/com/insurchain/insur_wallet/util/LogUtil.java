package com.insurchain.insur_wallet.util;

import android.util.Log;

import com.insurchain.insur_wallet.BuildConfig;


/**
 * Created by huangshan
 */
public class LogUtil {

    public static void e(String clazz, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.e("jiaoyin", log(clazz + "--->" + msg));
    }

    public static void i(String clazz, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.i("jiaoyin", log(clazz + "--->" + msg));
    }

    /**
     * 打印 Log 行数位置
     */
    private static String log(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement;
        if(stackTrace.length > 5){
             targetElement = stackTrace[5];

        }else {
             targetElement = stackTrace[4];

        }
        String className = targetElement.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1) + ".java";
        int lineNumber = targetElement.getLineNumber();
        if (lineNumber < 0) lineNumber = 0;
        return "(" + className + ":" + lineNumber + ") " + message;
    }
}
