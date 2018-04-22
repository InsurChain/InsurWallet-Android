package com.insurchain.insur_wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.util.UIUtil;

import java.lang.ref.WeakReference;

/**
 * 闪屏页
 */
public class SplashActivity extends Activity {

    private ImageView img;

    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        img = new ImageView(this);
        setContentView(img);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(R.drawable.splash)
                .centerCrop()
                .error(R.drawable.splash)
                .crossFade()
                .into(new GlideDrawableImageViewTarget(img) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (mHandler != null)
                            mHandler.sendEmptyMessageDelayed(0, 2000);
                    }
                });
    }

    private class SwitchHandler extends Handler {
        private WeakReference<SplashActivity> mWeakReference;

        SwitchHandler(Looper mLooper, SplashActivity activity) {
            super(mLooper);
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(mWeakReference.get(), MainActivity.class);
            UIUtil.startActivity(mWeakReference.get(), intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
