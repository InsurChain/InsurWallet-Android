package com.insurchain.insur_wallet.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.insurchain.insur_wallet.Global;
import com.insurchain.insur_wallet.util.LogUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Global.WX_APP_ID, false);
        api.registerApp(Global.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        LogUtil.e("onReq", req.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp != null) {
            if (baseResp instanceof SendMessageToWX.Resp) {
                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
//                    UIUtil.showToastAtCenter(this,"分享成功");
                } else if (baseResp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
//                    UIUtil.showToastAtCenter(this,"分享取消");
                }
            }
        } else {
//            UIUtil.showToastAtCenter(this,"分享失败");
            //wechat error do nothing
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        api = null;
    }
}