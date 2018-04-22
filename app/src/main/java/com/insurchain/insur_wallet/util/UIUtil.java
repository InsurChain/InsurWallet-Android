package com.insurchain.insur_wallet.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;


import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.XApplication;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangshan 2016/8/31
 */
public class UIUtil {

    private static final float AMAYA_WIDTH = 720;
    private static final float AMAYA_HEIGHT = 1280;
    public static int amayaWidth = (int) AMAYA_WIDTH, amayaHeight = (int) AMAYA_HEIGHT;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static float scale;
    private static float fontScale;


    public static int dip2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2sp(float dipValue) {
        return (int) (dip2px(dipValue) / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    public static void initSystemParam(float density, float scaledDensity) {
        scale = density;
        fontScale = scaledDensity;
    }

    public static void initAmayaParams(int width, int height) {
        if (width > height) {
            amayaWidth = height;
            amayaHeight = width;
        } else {
            amayaWidth = width;
            amayaHeight = height;
        }
    }

    public static int getHeight(int height) {
        return (int) (AMAYA_HEIGHT / amayaHeight * height);
    }

    public static int getCommonWidth(int width) {
        return dip2px(AMAYA_WIDTH / amayaWidth * width);
    }

    public static int getCommonHeight(int height) {
        if (AMAYA_HEIGHT > amayaHeight) {
            return dip2px(amayaHeight / AMAYA_HEIGHT * height);
        } else {

            return dip2px(AMAYA_HEIGHT / amayaHeight * height);
        }
    }


    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 设置Selector。
     */
    public static StateListDrawable generateSelector(Context context, int idNormal, int idPressed, int idFocused,
                                                     int idUnable) {

        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : ContextCompat.getDrawable(context, idNormal);
        Drawable pressed = idPressed == -1 ? null : ContextCompat.getDrawable(context, idPressed);
        Drawable focused = idFocused == -1 ? null : ContextCompat.getDrawable(context, idFocused);
        Drawable unable = idUnable == -1 ? null : ContextCompat.getDrawable(context, idUnable);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }

    public static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }


    public static Drawable getDrawable(int drawable) {
        return XApplication.getInstance().getResources().getDrawable(drawable);
    }

    public static String getString(int stringRes, int text) {
        return XApplication.getContext().getResources().getString(stringRes, text);
    }

    public static String getString(int stringRes, String text) {
        return XApplication.getContext().getResources().getString(stringRes, text);
    }

    public static boolean checkPhoneNum(String phone, boolean showToast) {
        if (TextUtils.isEmpty(phone)) {
            if (showToast)
                UIUtil.showToastAtCenter(XApplication.getContext(), XApplication.getContext().getString(R.string.phone_number_empty));
            return false;
        } else if (phone.length() != 11) {
            if (showToast)
                UIUtil.showToastAtCenter(XApplication.getContext(), XApplication.getContext().getString(R.string.phone_number_length_error));
            return false;
        } else {
            String regex = "^((1[0-9][0-9]))\\d{8}$";//  ^((13[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\d{8}$
            Pattern p = Pattern.compile(regex);
//            Pattern p = Pattern.compile(("^1[0-9]{10}$"));
            Matcher m = p.matcher(phone);
            boolean matches = m.matches();
            if (!matches && showToast)
                UIUtil.showToastAtCenter(XApplication.getContext(), XApplication.getContext().getString(R.string.phone_number_error));
            return matches;
        }
    }

    /**
     * 验证是否为邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String regex = "[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9]+(\\.(com|cn|org|edu|hk))";//[a-zA-Z0-9_\\-\\.]+@(sina|qq|163)+(\\.(com|cn|org|edu|hk))
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean matches = m.matches();//android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!matches) UIUtil.showToastAtCenter(XApplication.getContext(), "邮箱格式不正确");
        return matches;
    }

    /**
     * 检查网络状态，弹出toast    false弹出   true不操作
     *
     * @return
     */
    public static boolean checkNetworkOrToast() {
        boolean available = NetUtil.isNetworkAvailable();
        if (!available)
            UIUtil.showToastAtCenter(XApplication.getContext(), XApplication.getContext().getString(R.string.network_error));
        return available;
    }

    public static void showEditToast() {
        UIUtil.showToastAtCenter(XApplication.getContext(), "请填写分享的内容");
    }

    private static Toast toast = null;

    public static synchronized Toast showToastAtCenter(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    // a integer to xx:xx:xx
    public static String secToTime(long time) {
        String timeStr;
        long hour;
        long minute;
        long second;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 转化成指定时间格式
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        return format.format(date);
    }

    public static String parseDateyyyyMMdd(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return format.format(date);
    }

    public static String parseDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(date);
    }

    public static String parseDateyyyyMM(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(date);
    }

    /**
     * 得到三个数的最大值
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static int getMax(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

    /**
     * 根据Uri获取bitmap
     *
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            return MediaStore.Images.Media.getBitmap(XApplication.getContext().getContentResolver(), uri);
        } catch (Exception e) {
            LogUtil.e("[Android]", e.getMessage());
            LogUtil.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmapByResource(Context context, int resId) {
        if (context == null) {
            context = XApplication.getInstance();
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 保存头像
     */
    public static void saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int JELLY_BEAN_MR1 = 17;
    public static boolean PRE_JELLY_BEAN_MR1 = android.os.Build.VERSION.SDK_INT < JELLY_BEAN_MR1;

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static String openAssets(String name) {
        try {
            InputStream open = XApplication.getContext().getAssets().open(name);
            return getString(open);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(InputStream open) {
        StringBuilder sb;
        try {
            int len;
            int BUF_LENGTH = 459800;
            byte[] amayaBuf = new byte[BUF_LENGTH];
            sb = new StringBuilder();
            int CODE_NUMBER = 3;                    //默认UTF编码,一个中文对应的bytes.length = 3;
            while ((len = open.read(amayaBuf)) != -1) {

                /**
                 * 计算最后一位是否是中文字节 并判断是否是最后一次读取(是则直接append)
                 */
                if (amayaBuf[amayaBuf.length - 1] < 0 && open.available() != 0) {
                    //最后一位中文字符编码的偏移量初始值
                    int offset = 0;
                    //buf索引
                    int index = amayaBuf.length - 1;
                    //循环往前遍历直到遇到第一个非中文字符
                    while (amayaBuf[index] < 0) {
                        index--;           //buf索引往前移动一位
                        len--;              //往sb种append的位数缩减一位;缩减的byte将同后面未读入的中文字节一块儿append
                        offset++;          //偏移量
                    }
                    sb.append(new String(amayaBuf, 0, len));
                    int tl = offset + CODE_NUMBER - offset % CODE_NUMBER;
                    byte[] chineseBuf = new byte[tl];
                    for (int i = 0; i < offset; i++) {
                        chineseBuf[i] = amayaBuf[len + i];
                    }
                    int l = tl - offset;
                    open.read(chineseBuf, offset, l);
                    sb.append(new String(chineseBuf));
                } else {
                    sb.append(new String(amayaBuf, 0, len));
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long lastClickTime;

    /**
     * 防止重复点击
     *
     * @return 是否重复点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
