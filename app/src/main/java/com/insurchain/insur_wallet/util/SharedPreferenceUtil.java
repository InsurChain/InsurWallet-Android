package com.insurchain.insur_wallet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.insurchain.insur_wallet.XApplication;

/**
 * Created by huangshan 2016/8/31
 */
public class SharedPreferenceUtil {

    public static final String KEY_OBJECT_ID = "object_id";
    public static final String KEY_SESSION_TOKEN = "sessionToken";
    public static final String KEY_CITY = "city";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_ICON_URL = "iconUrl";
    public static final String KEY_MOBILE__IS_BIND = "mobileIsBind";
    public static final String KEY_PHONE_NUMBER = "mobilePhoneNumber";
    public static final String KEY_USER_NAME = "username";
    private static final String KEY_NICK_NAME = "nickname";

    public static SharedPreferences sp;

    public static synchronized void initSP(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void checkSP() {
        if (sp == null) {
            initSP(XApplication.getContext());
        }
    }

    public static boolean getBoolean(String key, boolean value) {
        checkSP();
        return sp.getBoolean(key, value);
    }

    public static int getInt(String key, int defValue) {
        checkSP();
        return sp.getInt(key, defValue);
    }

    public static String getString(String key, String defValue) {
        checkSP();
        return sp.getString(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        checkSP();
        return sp.getLong(key, defValue);
    }

    public static boolean save(String key, boolean value) {
        checkSP();
        return sp.edit().putBoolean(key, value).commit();
    }

    public static boolean save(String key, int value) {
        checkSP();
        return sp.edit().putInt(key, value).commit();
    }

    public static boolean save(String key, long value) {
        checkSP();
        return sp.edit().putLong(key, value).commit();
    }

    public static boolean save(String key, float value) {
        checkSP();
        return sp.edit().putFloat(key, value).commit();
    }

    public static boolean save(String key, String value) {
        checkSP();
        return sp.edit().putString(key, value).commit();
    }

    public static boolean remove(String key) {
        checkSP();
        return sp.edit().remove(key).commit();
    }

    public static boolean checkUserId() {
        checkSP();
        String uid = sp.getString("matrix_user_id", "");
        return !TextUtils.isEmpty(uid) && checkUserToken();

    }

    public static boolean checkUserToken() {
        checkSP();
        String token = sp.getString("matrix_user_token", "");
        return !TextUtils.isEmpty(token);
    }

    public static void saveLoginType(Integer loginType) {
        checkSP();
        sp.edit().putInt("matrix_user_logintype", loginType).apply();
    }

    public static boolean saveTheme(int theme) {
        return save("amaya_theme", theme);
    }

    public static int getTheme() {
        return getInt("amaya_theme", 0);
    }

    public static void saveUserToken(String userToken) {
        checkSP();
        sp.edit().putString("matrix_user_token", userToken).apply();
    }

    public static String getUserToken() {
        checkSP();
        return sp.getString("matrix_user_token", null);
    }

    public static float getFloat(String key) {
        checkSP();
        return sp.getFloat(key, 0f);
    }

//    public static void saveImei(String imei) {
//        checkSP();
//        save(KEY_DEVICE_IMEI, imei);
//    }
//
//    public static String getImei() {
//        checkSP();
//        return sp.getString(KEY_DEVICE_IMEI, null);
//    }
//
//    public static void saveQiNiuToken(String token) {
//        checkSP();
//        save(KEY_QINIU_TOKEN, token);
//    }
//
//    public static String getQiNiuToken() {
//        checkSP();
//        return sp.getString(KEY_QINIU_TOKEN, null);
//    }

//    public static void saveQiNiuDomain(String domain) {
//        checkSP();
//        save(KEY_QINIU_DOMAIN, domain);
//    }
//
//    public static String getKeyQiniuDomain() {
//        checkSP();
//        return sp.getString(KEY_QINIU_DOMAIN, null);
//    }

//    public static void saveUser(User user) {
//        checkSP();
//        save(KEY_OBJECT_ID, user.getObjectId());
//        save(KEY_SESSION_TOKEN, user.getSessionToken());
//        save(KEY_CITY, user.getCity());
//        save(KEY_GENDER, user.getGender());
//        save(KEY_ICON_URL, user.getIconUrl());
//        save(KEY_USER_NAME, user.getUsername());
//        save(KEY_NICK_NAME, user.getNickname());
//        save(KEY_PHONE_NUMBER, user.getMobilePhoneNumber());
//        XApplication.user = user;
//    }
//
//    public static void saveModifyUser(User user) {
//        checkSP();
//        save(KEY_OBJECT_ID, user.getObjectId());
//        save(KEY_CITY, user.getCity());
//        save(KEY_GENDER, user.getGender());
//        save(KEY_ICON_URL, user.getIconUrl());
//        save(KEY_USER_NAME, user.getUsername());
//        save(KEY_NICK_NAME, user.getNickname());
//        save(KEY_PHONE_NUMBER, user.getMobilePhoneNumber());
//        User user1 = new User();
//        user1.setObjectId(user.getObjectId());
//        user1.setCity(user.getCity());
//        user1.setIconUrl(user.getIconUrl());
//        user1.setGender(user.getGender());
//        user1.setUsername(user.getUsername());
//        user1.setNickname(user.getNickname());
//        user1.setMobilePhoneNumber(user.getMobilePhoneNumber());
//        user1.setSessionToken(getString(KEY_SESSION_TOKEN, null));
//        XApplication.user = user1;
//    }
//
//
//    public static User getUser() {
//        User user = new User();
//        user.setObjectId(getString(KEY_OBJECT_ID, null));
//        user.setSessionToken(getString(KEY_SESSION_TOKEN, null));
//        user.setCity(getString(KEY_CITY, null));
//        user.setGender(getInt(KEY_GENDER, -1));
//        user.setIconUrl(getString(KEY_ICON_URL, null));
//        user.setUsername(getString(KEY_USER_NAME, null));
//        user.setNickname(getString(KEY_NICK_NAME, null));
//        user.setMobilePhoneNumber(getString(KEY_PHONE_NUMBER, null));
//        return user;
//    }
//
//    public static void clearUser() {
//        checkSP();
//        sp.edit().remove(KEY_OBJECT_ID)
//                .remove(KEY_SESSION_TOKEN)
//                .remove(KEY_CITY)
//                .remove(KEY_GENDER)
//                .remove(KEY_ICON_URL)
//                .remove(KEY_USER_NAME)
//                .remove(KEY_NICK_NAME)
//                .remove(KEY_PHONE_NUMBER)
//                .apply();
//        XApplication.user = new User();
//    }
}
