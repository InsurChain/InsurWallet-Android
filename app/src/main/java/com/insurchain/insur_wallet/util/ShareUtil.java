package com.insurchain.insur_wallet.util;

import android.app.Activity;
import android.graphics.Bitmap;

import com.insurchain.insur_wallet.XApplication;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;


/**
 * Created by huangshan on 17/9/4.
 */

public class ShareUtil {

    private Activity activity;
    private IWXAPI api;
    private String url;
    private String imgUrl;
    private String title;
    private String description;

    public ShareUtil(IWXAPI api, String url, String imgUrl, String title, String description) {
        this.api = api;
        this.url = url;
        this.imgUrl = imgUrl;
        this.title = title;
        this.description = description;
    }

    public void shareToWx(final int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Bitmap bitmap = Picasso.with(XApplication.getContext()).load(imgUrl).get();
                    msg.thumbData = WxImageUtil.bmpToByteArray(bitmap, true);
                    LogUtil.e("msg.thumbData",msg.thumbData.length+"");

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;
                    if(type == 1){
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                    }else {
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    }
                    api.sendReq(req);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }.start();
    }


    public String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
