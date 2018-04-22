package com.insurchain.insur_wallet.util;

import android.view.View;

/**
 * Created by huangshan on 16/9/4.
 * 点击限制
 */
public abstract class OnClickLimitListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onClickLimit(v);
        }else {
            UIUtil.showToastAtCenter(v.getContext(),"操作频繁");
        }
    }

    public abstract void onClickLimit(View v);

}
