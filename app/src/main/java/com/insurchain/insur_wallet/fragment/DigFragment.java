package com.insurchain.insur_wallet.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.insurchain.insur_wallet.BuildConfig;
import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.XApplication;
import com.insurchain.insur_wallet.activity.LoginActivity;
import com.insurchain.insur_wallet.activity.WebActivity;
import com.insurchain.insur_wallet.util.BaseEvent;
import com.insurchain.insur_wallet.util.LogUtil;
import com.insurchain.insur_wallet.util.ShareUtil;
import com.insurchain.insur_wallet.util.SharedPreferenceUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DigFragment extends SupportFragment {

    private IWXAPI api;
    private ShareUtil shareUtil;

    @BindView(R.id.webView)
    WebView webView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DigFragment() {
        // Required empty public constructor
    }

    public static DigFragment newInstance(String param1, String param2) {
        DigFragment fragment = new DigFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        api = WXAPIFactory.createWXAPI(getActivity(), Global.WX_APP_ID, false);
        api.registerApp(Global.WX_APP_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dig, container, false);
        ButterKnife.bind(this, rootView);
        webView.setBackgroundColor(0);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        //表示按返回键时的操作
                        webView.goBack();   //后退
                        //webview.goForward();//前进
                        return true;    //已处理  表示这个事件已经被消费，不会再向上传播
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initData() {
        //WebView加载web资源
        //启用支持javascript
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
        //将图片下载阻塞
//        settings.setBlockNetworkImage(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this, "jsBridge");
        //设置视图客户端
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                if (progress != null) {
//                    if (newProgress == 100) {
//                        progress.setVisibility(View.GONE);
//                    } else {
//                        progress.setVisibility(View.VISIBLE);
//                        progress.setProgress(newProgress);//设置加载进度
//                    }
//                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.loadUrl("https://wallet.qusukj.com/h5/index.html" + "?version=" + BuildConfig.VERSION_NAME);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
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
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("token", SharedPreferenceUtil.getString("token", null));
            String jsStr = JSON.toJSONString(hashMap);
            view.loadUrl("javascript:nativeToJs('" + jsStr + "')");
        }
    }

    @JavascriptInterface
    public void jsBridge(final String jsonStr) {
        LogUtil.e("jsBridge", jsonStr);
        try {
            final JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("upgrade")) {
                XApplication.upgrade = jsonObject.getBoolean("upgrade");
            } else if (jsonObject.has("download")) {
                if (jsonObject.getBoolean("download")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://wallet.qusukj.com/h5/shareSucc.html");
                    intent.setData(content_url);
                    startActivity(intent);
                }
            } else {
                if (SharedPreferenceUtil.getString("token", null) != null) {
                    if (jsonObject.has("isShare")) {
                        if (jsonObject.getBoolean("isShare")) {
                            final String url = jsonObject.getString("url");
                            final String inviteUrl = jsonObject.getString("inviteUrl");
                            final String desc = jsonObject.getString("desc");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    final Dialog dialog = builder.create();
                                    dialog.show();

                                    final WindowManager m = ((Activity) getContext()).getWindowManager();
                                    Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                                    final android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
                                    p.width = (int) (d.getWidth());    //宽度设置为屏幕的宽度
                                    dialog.getWindow().setAttributes(p);     //设置生效

                                    dialog.setCancelable(true);
                                    dialog.setCanceledOnTouchOutside(true);
                                    Window window = dialog.getWindow();
                                    window.setBackgroundDrawableResource(android.R.color.transparent);
                                    window.setWindowAnimations(R.style.sendgift_style);
                                    View view = View.inflate(getContext(), R.layout.share_layout, null);
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
//                                        shareUtil = new ShareUtil(api, url, imgUrl, title, desc);
//                                        shareUtil.shareToWx(1);
                                            dialog.dismiss();
                                            String str = "文字已复制，快发给好友吧！\n" + "【" + desc + "\n" + url + "】";
                                            copy(getActivity(), desc + "\n" + url);
                                            new android.support.v7.app.AlertDialog.Builder(getActivity())
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
//                                        shareUtil = new ShareUtil(api, url, imgUrl, title, desc);
//                                        shareUtil.shareToWx(2);
//                                        dialog.dismiss();
                                            dialog.dismiss();
                                            Intent intent = new Intent(getActivity(), WebActivity.class);
                                            intent.putExtra("url", inviteUrl);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }
                    } else if (jsonObject.has("url")) {
                        String url = jsonObject.getString("url");
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent.UpdateInfoEvent updateInfoEvent) {
        if (webView != null) {
            webView.reload();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent.ReLoadEvent reLoadEvent) {
        if (webView != null) {
            webView.reload();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent.LogoutEvent logoutEvent) {
        if (webView != null) {
            webView.reload();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent.LoginEvent loginEvent) {
        LogUtil.e("loginEvent", SharedPreferenceUtil.getString("token", null));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", SharedPreferenceUtil.getString("token", null));
        String jsStr = JSON.toJSONString(hashMap);
        webView.loadUrl("javascript:nativeToJs('" + jsStr + "')");
        if (SharedPreferenceUtil.getString("token", null) != null) {
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
    public void onDestroy() {
        if (webView != null) {
            webView.clearHistory();
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
