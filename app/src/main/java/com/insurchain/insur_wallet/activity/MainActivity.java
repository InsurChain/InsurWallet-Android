package com.insurchain.insur_wallet.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.XApplication;
import com.insurchain.insur_wallet.fragment.DigFragment;
import com.insurchain.insur_wallet.fragment.ExchangeFragment;
import com.insurchain.insur_wallet.fragment.MineFragment;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.util.LogUtil;
import com.insurchain.insur_wallet.util.SharedPreferenceUtil;
import com.insurchain.insur_wallet.view.UnScrollViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends SupportActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.vp_content)
    UnScrollViewPager vpContent;
    @BindView(R.id.tab_rg_menu)
    RadioGroup radioGroup;
    @BindView(R.id.tab_rb_1)
    RadioButton tab_rb_1;
    @BindView(R.id.tab_rb_2)
    RadioButton tab_rb_2;
    @BindView(R.id.tab_rb_3)
    RadioButton tab_rb_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ButterKnife.bind(this);
        initView();
        if (SharedPreferenceUtil.getString("token", null) != null) {
            LogUtil.e("token", SharedPreferenceUtil.getString("token", null));
            String url = Global.BaseUrl + Global.activate;
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authentication", SharedPreferenceUtil.getString("token", null))
                    .method("POST", body)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.e("activate", response.code() + "");
                }
            });
        }
    }

    private void initView() {
        List<Fragment> fragments = initFragments();
        vpContent.setScrollable(false);
        ContentPagerAdapter mPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager(), fragments);
        vpContent.setAdapter(mPagerAdapter);
        vpContent.setOffscreenPageLimit(fragments.size());
        radioGroup.setOnCheckedChangeListener(this);
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private List<Fragment> initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        DigFragment fragment1 = new DigFragment();
        Fragment fragment2 = new ExchangeFragment();
        Fragment fragment3 = new MineFragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        return fragments;
    }

    private class ContentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        ContentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (XApplication.upgrade) {
            tab_rb_1.setChecked(true);
            tab_rb_2.setChecked(false);
            tab_rb_3.setChecked(false);
        } else {
            switch (i) {
                case R.id.tab_rb_1:
                    vpContent.setCurrentItem(0, false);
                    break;
                case R.id.tab_rb_2:
                    if (SharedPreferenceUtil.getString("token", null) == null) {
                        startActivity(new Intent(this, LoginActivity.class));
                        tab_rb_1.setChecked(true);
                    } else {
                        vpContent.setCurrentItem(1, false);
                    }
                    break;
                case R.id.tab_rb_3:
                    if (SharedPreferenceUtil.getString("token", null) == null) {
                        startActivity(new Intent(this, LoginActivity.class));
                        tab_rb_1.setChecked(true);
                    } else {
                        vpContent.setCurrentItem(2, false);
                    }
                    break;
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tab_rb_1.setChecked(true);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
