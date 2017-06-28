package com.lt.nexthud2017.weixin.util;

import android.util.Log;

import com.lt.nexthud2017.weixin.WeChatClass;
import com.lt.nexthud2017.weixin.WeiXinFriend;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/6/7.
 */

public class WaitScanAndLoginThread extends Thread{
    public  int tip=1;
    public  String uuid;
    public boolean running=true;
    private HttpClient hc=HttpClient.getInstance();
    private StringSubClass ss=new StringSubClass();
    private OnScanListener mScanListener;
    private WeChatClass wechat;


    public String logloginResult;

    public interface OnScanListener{
        void onScan();
        void onReload();
        void onSure();
        void onInitMyFriends(List<WeiXinFriend> friends);
        void onInitMyAllFriends(List<WeiXinFriend> friends);
    }

    public void setmScanListener(OnScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }

    public WaitScanAndLoginThread(String uuid,WeChatClass wechat){
        this.uuid=uuid;
        this.wechat=wechat;

    }
    @Override
    public void run() {
        while(running){

            String result = hc.getLoginResultFromRetrofit( tip + "" ,uuid );
            Log.e("weixin result", result);

//            WeChatHttpMethod.getInstance().getLoginResult(tip, uuid, new Action1<String>() {
//                @Override
//                public void call(String result) {
//                }
//            });

            String code="";
            try
            {
                code=ss.subStringOne(result, ".code=", ";");
            }
            catch(Exception ex)
            {
                Log.e("weixin login error", result);
            }
            Log.e("weixin login1",code);
            if (mScanListener!=null) {
                if(code.equals("201")){
                    tip=0;
                    mScanListener.onScan();
                }else if (code.equals("200")) {

                    String redirect_uri=ss.subStringOne(result, "window.redirect_uri=\"", "\";");
                    running=false;
                    String loginResult=hc.get(redirect_uri+"&fun=new");

                    Log.e("wx","loginResult:"+loginResult);
                    logloginResult=loginResult;
                    WeChatClass.skey=ss.subStringOne(loginResult, "<skey>", "</skey>");

                    Log.e("wx","wechat.skey:"+WeChatClass.skey);

                    WeChatClass.wxsid=ss.subStringOne(loginResult, "<wxsid>", "</wxsid>");
                    WeChatClass.pass_ticket=ss.subStringOne(loginResult, "<pass_ticket>", "</pass_ticket>");

                    Log.e("wx","wechat.pass_ticket:"+WeChatClass.pass_ticket);

                    WeChatClass.wxuin=ss.subStringOne(loginResult, "<wxuin>", "</wxuin>");
                    WeChatClass.baseUrl=redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));

                    mScanListener.onSure();
                    wechat.init();
                }
                if(code.equals("408")|| code.equals("400")){

                    running=false;
                    mScanListener.onReload();
                }
            }
        }//while

    }

}
