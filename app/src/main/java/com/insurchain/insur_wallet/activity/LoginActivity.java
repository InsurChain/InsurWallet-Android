package com.insurchain.insur_wallet.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.util.BaseEvent;
import com.insurchain.insur_wallet.util.LogUtil;
import com.insurchain.insur_wallet.util.NetUtil;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.util.OnClickLimitListener;
import com.insurchain.insur_wallet.util.SharedPreferenceUtil;
import com.insurchain.insur_wallet.util.UIUtil;
import com.insurchain.insur_wallet.XApplication;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_code)
    EditText edit_code;
    @BindView(R.id.btn_get_verify_code)
    Button btn_get_verify_code;
    @BindView(R.id.edit_img_code)
    EditText edit_img_code;
    @BindView(R.id.iv_get_imgcode)
    ImageView iv_get_imgcode;

    @BindString(R.string.network_error)
    String network_error;

    private CountDownTimer countDownTimer;

    private String phoneNumber;
    private String code;
    private String imgCode;

    private String imgCodeUrl;
    private String imgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getImgCode();

        iv_get_imgcode.setOnClickListener(new OnClickLimitListener() {
            @Override
            public void onClickLimit(View v) {
                //获取图片验证码
                getImgCode();
            }
        });
        btn_get_verify_code.setOnClickListener(new OnClickLimitListener() {
            @Override
            public void onClickLimit(View v) {
                //获取短信验证码
                getCode();
            }
        });
    }


    @OnClick({R.id.iv_close, R.id.text_register, R.id.btn_login})
    public void ClickEvent(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.text_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                //登录
                phoneNumber = edit_phone.getText().toString().trim();
                code = edit_code.getText().toString().trim();
                imgCode = edit_img_code.getText().toString().trim();
                if (NetUtil.isNetworkAvailable()) {
                    if (!UIUtil.checkPhoneNum(phoneNumber, true)) {
                        return;
                    } else {
                        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(imgCode)) {
                            String url = Global.BaseUrl + Global.login;
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody body = new FormBody.Builder()
                                    .add("telephone", phoneNumber)
                                    .add("smsCode", code)
                                    .add("imgCode",imgCode)
                                    .add("inviter_id", "")
                                    .build();

                            Request request = new Request.Builder()
                                    .url(url)
                                    .method("POST", body)
                                    .build();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    LogUtil.e("loginError", e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    int code = response.code();
                                    LogUtil.e("code", code + "");

                                    String body = response.body().string();
                                    LogUtil.e("login", body);
                                    try{
                                        JSONObject jsonObject = new JSONObject(body);
                                        int state = jsonObject.getInt("state");
                                        final String msg = jsonObject.getString("msg");
                                        if(state == 0){
                                            JSONObject data = jsonObject.getJSONObject("data");
                                            String token = data.getString("token");
                                            //登录成功保存，发通知与webview交互,关闭页面
                                            SharedPreferenceUtil.save("token",token);
                                            EventBus.getDefault().post(new BaseEvent.LoginEvent());
                                            finish();
                                        }else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    UIUtil.showToastAtCenter(LoginActivity.this,msg);
                                                }
                                            });
                                            getImgCode();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            UIUtil.showToastAtCenter(XApplication.getContext(), "输入信息不能为空");
                        }
                    }
                } else {
                    UIUtil.showToastAtCenter(XApplication.getContext(), network_error);
                }
                break;
        }
    }

    private void getImgCode(){
        String url = Global.BaseUrl + Global.getImgCode;
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("getImgCode", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                LogUtil.e("getImgCode", body);
                try{
                    JSONObject jsonObject = new JSONObject(body);
                    int state = jsonObject.getInt("state");
                    if(state == 0){
                        JSONObject data = jsonObject.getJSONObject("data");
                        imgCodeUrl = data.getString("img");
                        imgId = data.getString("imgId");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(LoginActivity.this).load(imgCodeUrl).crossFade().into(iv_get_imgcode);
                            }
                        });
                    }else {
                        imgId = "";
                        imgCodeUrl = "";
                        getImgCode();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCode(){
        phoneNumber = edit_phone.getText().toString().trim();
        imgCode = edit_img_code.getText().toString().trim();
        if (NetUtil.isNetworkAvailable()) {
            if (!UIUtil.checkPhoneNum(phoneNumber, true)) {
                return;
            } else {
                //如果图片验证码不为空,点击获取验证码
                if(!TextUtils.isEmpty(imgCode)){
                    String url = Global.BaseUrl + Global.getCode;
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("telephone", phoneNumber)
                            .add("imgCode",imgCode)
                            .add("imgId",imgId)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .method("POST", body)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("getCodeError", e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            int code = response.code();
                            LogUtil.e("code",code+" : "+body);
                            try{
                                JSONObject jsonObject = new JSONObject(body);
                                int state = jsonObject.getInt("state");
                                final String msg = jsonObject.getString("msg");
                                if(state == 0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            countDownTimer = new CountDownTimer(60000, 1000) {
                                                // 第一个参数是总的倒计时时间
                                                // 第二个参数是每隔多少时间(ms)调用一次onTick()方法
                                                public void onTick(long millisUntilFinished) {
                                                    btn_get_verify_code.setText(millisUntilFinished / 1000 + "秒重新发送");
                                                    btn_get_verify_code.setEnabled(false);
                                                }

                                                public void onFinish() {
                                                    btn_get_verify_code.setText("获取验证码");
                                                    btn_get_verify_code.setEnabled(true);
                                                }
                                            }.start();
                                        }
                                    });
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UIUtil.showToastAtCenter(LoginActivity.this,msg);
                                        }
                                    });
                                    getImgCode();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    //图片验证码为空
                    UIUtil.showToastAtCenter(LoginActivity.this,"请填写图片验证码");
                }
            }
        } else {
            UIUtil.showToastAtCenter(this, network_error);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
