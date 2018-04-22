package com.insurchain.insur_wallet.activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.util.NetUtil;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.util.UIUtil;
import com.insurchain.insur_wallet.XApplication;

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

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_verifycode)
    EditText edit_verifycode;
    @BindView(R.id.btn_get_verify_code)
    Button btn_get_verify_code;

    @BindString(R.string.network_error)
    String network_error;

    private CountDownTimer countDownTimer;

    private String phoneNumber;
    private String smsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ivBack, R.id.btn_get_verify_code, R.id.btn_register})
    public void ClickEvent(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btn_get_verify_code:
                //获取验证码
                phoneNumber = edit_phone.getText().toString().trim();
                if (NetUtil.isNetworkAvailable()) {
                    if (!UIUtil.checkPhoneNum(phoneNumber, true)) {
                        return;
                    } else {
                        String url = Global.BaseUrl + Global.getCode;
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("telephone", phoneNumber)
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
                                Log.e("getCode", response.body().string());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        countDownTimer = new CountDownTimer(60000, 1000) {
                                            // 第一个参数是总的倒计时时间
                                            // 第二个参数是每隔多少时间(ms)调用一次onTick()方法
                                            public void onTick(long millisUntilFinished) {
                                                btn_get_verify_code.setText(millisUntilFinished / 1000 + "秒重新发送");
//                                btn_get_verify_code.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.button_get_code_gray_shape));
                                                btn_get_verify_code.setEnabled(false);
                                            }

                                            public void onFinish() {
                                                btn_get_verify_code.setText("获取验证码");
//                                btn_get_verify_code.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.button_get_code_shape));
                                                btn_get_verify_code.setEnabled(true);
                                            }
                                        }.start();
                                    }
                                });

                            }
                        });
//                        sendSMSCode(phoneNumber);
                    }
                } else {
                    UIUtil.showToastAtCenter(this, network_error);
                }
                break;
            case R.id.btn_register:
                //注册
                phoneNumber = edit_phone.getText().toString().trim();
                smsCode = edit_verifycode.getText().toString().trim();
                if (NetUtil.isNetworkAvailable()) {
                    if (!UIUtil.checkPhoneNum(phoneNumber, true)) {
                        return;
                    } else {
                        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(smsCode)) {
//                            registerPhoneNumber(phoneNumber, smsCode);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
