package com.insurchain.insur_wallet.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.util.BaseEvent;
import com.insurchain.insur_wallet.util.LogUtil;
import com.insurchain.insur_wallet.util.ShareUtil;
import com.insurchain.insur_wallet.util.SharedPreferenceUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huangshan on 18/2/6.
 */

public class WebActivity extends AppCompatActivity {

    private IWXAPI api;
    private ShareUtil shareUtil;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    //    private int REQ_CHOOSE = 200;
    private int FILE_CHOOSER_RESULT_CODE = 300;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.btnShare)
    TextView btnShare;

    private String webUrl;

    private String title;
    private String desc;
    private String url;
    private String imgUrl;
    private String inviteUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        webUrl = getIntent().getStringExtra("url");
        api = WXAPIFactory.createWXAPI(this, Global.WX_APP_ID, false);
        api.registerApp(Global.WX_APP_ID);
        initData();
    }

    @OnClick({R.id.ivBack, R.id.btnShare})
    public void ClickEvent(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnShare:
                //分享
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final Dialog dialog = builder.create();
                dialog.show();

                final WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                final android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
                p.width = (int) (d.getWidth());    //宽度设置为屏幕的宽度
                dialog.getWindow().setAttributes(p);     //设置生效

                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setWindowAnimations(R.style.sendgift_style);
                View view = View.inflate(this, R.layout.share_layout, null);
                window.setContentView(view);

                RelativeLayout dialog_root = view.findViewById(R.id.dialog_root);
                dialog_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                RelativeLayout item_wechat = (RelativeLayout) view.findViewById(R.id.item_wechat_share);
                item_wechat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String str = "文字已复制，快发给好友吧！\n"+"【"+desc+"\n"+url+"】";
                        copy(WebActivity.this,desc+"\n"+url);
                        new android.support.v7.app.AlertDialog.Builder(WebActivity.this)
                                .setMessage(str)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                });
                RelativeLayout item_wechat_circle = (RelativeLayout) view.findViewById(R.id.item_circle_share);
                item_wechat_circle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(WebActivity.this, WebActivity.class);
                        intent.putExtra("url", inviteUrl);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initData() {
        //WebView加载web资源
        //启用支持javascript
        webView.setBackgroundColor(0);
        webView.requestFocusFromTouch();
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setNeedInitialFocus(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this, "jsBridge");
        //设置视图客户端
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mTitleTextView != null)
                    mTitleTextView.setText(title);
            }

            //          // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                chosePic();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                chosePic();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                chosePic();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                chosePic();
                return true;
            }
        });
        webView.loadUrl(webUrl);
    }

    /**
     * 209.
     * 本地相册选择图片
     * 210.
     */
    private void chosePic() {
        Intent getAlbum = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getAlbum.setType("image/*");
        startActivityForResult(getAlbum, FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            LogUtil.e("WebViewClient_url", url);
            return false;
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

    @JavascriptInterface
    public void jsBridge(String jsonStr) {
        LogUtil.e("jsBridge", jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("isShare")) {
                if (jsonObject.getBoolean("isShare") == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShare.setVisibility(View.VISIBLE);
                        }
                    });
                    url = jsonObject.getString("url");
                    inviteUrl = jsonObject.getString("inviteUrl");
                    desc = jsonObject.getString("desc");
                }
            } else if (jsonObject.has("url")) {
                String url = jsonObject.getString("url");
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            } else if (jsonObject.has("updateInfo")) {
                if (jsonObject.getBoolean("updateInfo")) {
                    EventBus.getDefault().post(new BaseEvent.UpdateInfoEvent());
                    finish();
                }
            } else if (jsonObject.has("reload")) {
                if (jsonObject.getBoolean("reload")) {
                    EventBus.getDefault().post(new BaseEvent.ReLoadEvent());
                }
            }
            if (jsonObject.has("loginOut")) {
                if (jsonObject.getBoolean("loginOut") == true) {
                    SharedPreferenceUtil.remove("token");
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().post(new BaseEvent.LogoutEvent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 字符串复制
     *
     * @param mContext context
     * @param stripped 字符串
     */
    public void copy(Context mContext, String stripped) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("content", stripped);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.clearHistory();
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
