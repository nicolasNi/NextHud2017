package com.lt.nexthud2017.weixin;

import android.util.Log;

import com.google.gson.Gson;
import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.weixin.util.BaseResponeBean;
import com.lt.nexthud2017.weixin.util.DownLoadQrCodeThread;
import com.lt.nexthud2017.weixin.util.HeartBeatThread;
import com.lt.nexthud2017.weixin.util.StringSubClass;
import com.lt.nexthud2017.weixin.util.WaitScanAndLoginThread;
import com.lt.nexthud2017.weixin.util.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class WeChatClass {
    public boolean isBeat = false;
    public String uuid;
    public static String baseUrl;
    public static String skey;
    public static String wxsid;
    public static String pass_ticket;
    public static String wxuin;
    public static String keyString;
    public BaseResponeBean initbean;

    public static String myUserId;

    public Gson gson = new Gson();
    private HttpClient hc = HttpClient.getInstance();
    private StringSubClass ss = new StringSubClass();
    private WaitScanAndLoginThread.OnScanListener mScanListener;
    private HeartBeatThread.OnNewMsgListener mNewMsgListener;
    private OnLoadQrCodeListener mQrCodeListener;
    public static WaitScanAndLoginThread loginThread = null;

    //bitmap
    public interface OnLoadQrCodeListener {
        void onLoadSuccess(byte[] imageBytes);
    }


    public void gogogo() {
        System.setProperty("jsse.enableSNIExtension", "false");
        String result = hc.post("https://login.weixin.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_=" + System.currentTimeMillis(), "");
        uuid = ss.subStringOne(result, ".uuid = \"", "\";");
        DownLoadQrCodeThread qrCodeThread = new DownLoadQrCodeThread("https://login.weixin.qq.com/qrcode/" + uuid, true);
        qrCodeThread.setListener(new DownLoadQrCodeThread.OnloadQrCodeFinnishListener() {

            @Override
            public void onLoadSuccess(byte[] imageBytes) {
                if (mQrCodeListener != null) {
                    mQrCodeListener.onLoadSuccess(imageBytes);
                }
                if (loginThread != null) {
                    try {
                        loginThread.interrupt();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                loginThread = new WaitScanAndLoginThread(uuid, WeChatClass.this);
                loginThread.setmScanListener(mScanListener);
                loginThread.start();

            }
        });
        qrCodeThread.start();
    }

    String sendUrl = "/webwxsendmsg?lang=zh_CN&pass_ticket=%s";

    public String sendMsg(String friendId, String msg) {
        String url = String.format(baseUrl + sendUrl, pass_ticket);
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + MainActivity.DeviceId + "\"},\"Msg\":{\"ClientMsgId\":\"" +
                System.currentTimeMillis() + "\",\"Content\":\"" + msg + "\",\"FromUserName\":\"" + myUserId + "\",\"LocalID\":\"" + System.currentTimeMillis() + "\",\"ToUserName\":\"" + friendId + "\",\"Type\":\"1\"},\"Scene\":\"0\"}";
        Log.e("weixin sendMsg", url);
        String result = hc.post(url, data);
        return result;
    }

    String logout = "/webwxlogout?redirect=1&type=1&skey=";

    public void logout() {
        String url = baseUrl + logout + skey;
        String result = hc.get(url);
        Log.e("wx", "logout:" + result);
    }

    /**
     * syncKeys
     */
    public void syncKeys(String reslut) {
        if (reslut == null || reslut.length() < 2) {
            if (mNewMsgListener != null)
                mNewMsgListener.logout();
            Log.e("wx", "syncKeys = null");
            return;
        }
        initbean = gson.fromJson(reslut, BaseResponeBean.class);
        keyString = "";
        List<BaseResponeBean.SyncKeyEntity.ListEntity> keyList = initbean.getSyncKey().getList();
        for (BaseResponeBean.SyncKeyEntity.ListEntity listEntity : keyList) {
            keyString += listEntity.getKey() + "_" + listEntity.getVal() + "|";
        }
        keyString = keyString.substring(0, keyString.length() - 1);
        Log.e("wx", "keyString:" + keyString);
    }


    public void setmScanListener(WaitScanAndLoginThread.OnScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }

    public void setmNewMsgListener(HeartBeatThread.OnNewMsgListener mNewMsgListener) {
        this.mNewMsgListener = mNewMsgListener;
    }

    public void setmQrCodeListener(OnLoadQrCodeListener mQrCodeListener) {
        this.mQrCodeListener = mQrCodeListener;
    }

    public void init() {
        new InitThread().run();
    }

    class InitThread extends Thread {

        @Override
        public void run() {
            String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + MainActivity.DeviceId + "\"}}";
            hc.contentType = "application/json;charset=UTF-8";
            String initResult = hc.post(baseUrl + "/webwxinit?r=" + System.currentTimeMillis(),
                    data);
            Log.e("wx", "initResult:" + initResult);

            try {
                JSONObject obj = new JSONObject(initResult);
                JSONArray array = obj.getJSONArray("ContactList");
                if (array != null) {
                    List<WeiXinFriend> friends = new ArrayList<WeiXinFriend>();
                    for (int i = 0; i < array.length(); i++) {
                        Object obj1 = array.get(i);
                        JSONObject obj2 = new JSONObject(obj1.toString());
                        WeiXinFriend friend = new WeiXinFriend();
                        friend.nameId = obj2.getString("UserName");
                        friend.nickName = obj2.getString("NickName");
                        friend.headImgUrl = baseUrl + obj2.getString("HeadImgUrl");
                        friend.remarkName = obj2.getString("RemarkName");
                        if (friend.nickName.contains("文件传输助手") ||
                                friend.nickName.contains("微信团队") ||
                                friend.nickName.contains("朋友推荐消息")) {
                            continue;
                        }
                        friends.add(friend);
                    }
                    if (mScanListener != null) {
                        mScanListener.onInitMyFriends(friends);
                    }
                }
                String mySelf = obj.getString("User");
                JSONObject myObj = new JSONObject(mySelf);
                myUserId = myObj.getString("UserName");

            } catch (JSONException e) {
                Log.e("login error", e.toString());
            }
            String initResult2 = hc.post(baseUrl + "/webwxgetcontact?r=" + System.currentTimeMillis(), data);
            try {
                JSONObject obj = new JSONObject(initResult2);
                JSONArray array = obj.getJSONArray("MemberList");
                if (array != null) {
                    List<WeiXinFriend> friends = new ArrayList<WeiXinFriend>();
                    for (int i = 0; i < array.length(); i++) {
                        Object obj1 = array.get(i);
                        JSONObject obj2 = new JSONObject(obj1.toString());
                        WeiXinFriend friend = new WeiXinFriend();
                        friend.nameId = obj2.getString("UserName");
                        friend.nickName = obj2.getString("NickName");
                        friend.headImgUrl = baseUrl + obj2.getString("HeadImgUrl");
                        friend.remarkName = obj2.getString("RemarkName");
                        friends.add(friend);
                    }
                    if (mScanListener != null) {
                        mScanListener.onInitMyAllFriends(friends);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e("", e.toString());
            }

            if (!isBeat) {

                try {
                    loginThread.interrupt();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                syncKeys(initResult);
                HeartBeatThread heartBeatThread = new HeartBeatThread(WeChatClass.this);
                heartBeatThread.setmNewMsgListener(mNewMsgListener);
                heartBeatThread.start();
                isBeat = true;
            }
        }
    }

}

