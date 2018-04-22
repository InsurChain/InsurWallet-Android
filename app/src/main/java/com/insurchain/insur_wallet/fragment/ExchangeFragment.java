package com.insurchain.insur_wallet.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.insurchain.insur_wallet.R;
import com.insurchain.insur_wallet.activity.LoginActivity;
import com.insurchain.insur_wallet.activity.WebActivity;
import com.insurchain.insur_wallet.util.LogUtil;
import com.insurchain.insur_wallet.util.SharedPreferenceUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

public class ExchangeFragment extends SupportFragment {

    @BindView(R.id.webView)
    WebView webView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExchangeFragment() {
        // Required empty public constructor
    }

    public static ExchangeFragment newInstance(String param1, String param2) {
        ExchangeFragment fragment = new ExchangeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, rootView);
        webView.setBackgroundColor(0);
        LogUtil.e("品牌:",android.os.Build.BRAND);
        if(android.os.Build.BRAND.equals("vivo")){
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.e("onAttach","onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.e("onDetach","onDetach");
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

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
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
            }
        });
        webView.loadUrl("https://wallet.qusukj.com/h5/change.html");
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

        }
    }

    @JavascriptInterface
    public void jsBridge(String jsonStr) {
        LogUtil.e("jsBridge", jsonStr);
        if(SharedPreferenceUtil.getString("token",null) != null){
            try{
                JSONObject jsonObject = new JSONObject(jsonStr);
                String url = jsonObject.getString("url");
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
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
    }
}
