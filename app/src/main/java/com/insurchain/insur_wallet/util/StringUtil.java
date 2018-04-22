package com.insurchain.insur_wallet.util;

import android.content.Context;

/**
 * Created by huangshan on 2016/11/23.
 */
public class StringUtil {

    /**
     * 字符串复制
     *
     * @param mContext context
     * @param stripped 字符串
     */
    public static void copy(Context mContext, String stripped) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("content", stripped);
        clipboard.setPrimaryClip(clip);
//        UIUtil.showToastAtCenter(mContext, "复制成功");
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     */
    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!(Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
