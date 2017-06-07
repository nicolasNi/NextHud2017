package com.lt.nexthud2017.weixin.util;

import android.util.Log;

import com.google.gson.Gson;
import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.weixin.WeChatClass;
import com.lt.nexthud2017.weixin.WeiXinMessage;
import com.lt.nexthud2017.weixin.util.HttpClient;
import com.lt.nexthud2017.weixin.util.MsgBean;
import com.lt.nexthud2017.weixin.util.StringSubClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class HeartBeatThread extends Thread{

    private HttpClient hc=HttpClient.getInstance();
    private StringSubClass ss=new StringSubClass();
    private Gson gson=new Gson();
    private OnNewMsgListener mNewMsgListener;
    private WeChatClass wechat;
    public interface OnNewMsgListener{
        void onNewMsg(WeiXinMessage message);

        void startBeat();

        void logout();

    }
    public HeartBeatThread(WeChatClass wechat) {
        this.wechat=wechat;
    }

    public void setmNewMsgListener(OnNewMsgListener mNewMsgListener) {
        this.mNewMsgListener = mNewMsgListener;
    }


    String checkUrl="/synccheck?r=%s&sid=%s&uin=%s&synckey=%s&skey=%s&deviceid=%s";
    static String syncKey="";


    @Override
    public void run() {
        if (mNewMsgListener!=null) {
            mNewMsgListener.startBeat();
        }
        while (true) {

            if(!wechat.isBeat){
                Log.e("check weixin", "!wechat.isBeat");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                continue;
            }
            String url=String.format(wechat.baseUrl+checkUrl, System.currentTimeMillis()+"",wechat.wxsid,wechat.wxuin,wechat.keyString,wechat.skey, MainActivity.DeviceId);
            String syncResult=hc.get(url+"&_="+System.currentTimeMillis());
            Log.e("check weixin", syncResult);
            String selector;
            String retcode;
            try {
                selector=ss.subStringOne(syncResult, "selector:\"", "\"}");
                retcode=ss.subStringOne(syncResult, "retcode:\"", "\"}");
            } catch (Exception e) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                continue;
            }

            if(retcode.contains("1101")){
                Log.e("wechat", "logout");
                mNewMsgListener.logout();
                break;
            }

            if (!selector.equals("0")) {
                if (mNewMsgListener!=null) {
                    String key=wechat.gson.toJson(wechat.initbean.getSyncKey());
                    if(syncKey.length()>5){
                        key=syncKey;
                    }
                    String data2="{\"BaseRequest\":{\"Uin\":\""+wechat.wxuin+"\",\"Sid\":\""+wechat.wxsid+"\",\"Skey\":\""+wechat.skey+"\",\"DeviceID\":\""+MainActivity.DeviceId+"\"},\"SyncKey\":"
                            +key+",\"rr\":"+~System.currentTimeMillis()+"}";
                    String url2=wechat.baseUrl+"/webwxsync?sid="+wechat.wxsid+"&skey="+wechat.skey+"&pass_ticket="+wechat.pass_ticket;
                    String newMsg=hc.post(url2,data2);

                    JSONObject obj2=null;
                    try {
                        obj2 = new JSONObject(newMsg);
                        if(obj2!=null){
                            String keys=obj2.getString("SyncKey");
                            syncKey=obj2.getString("SyncCheckKey");
                            JSONObject obj = new JSONObject(keys);
                            JSONArray array= obj.getJSONArray("List");

                            StringBuffer synckey = new StringBuffer();
                            for (int i = 0, len = array.length(); i < len; i++) {
                                JSONObject jb = new JSONObject(array.get(i).toString());
                                if(i==0)
                                    synckey.append(jb.getInt("Key") + "_" + jb.getInt("Val"));
                                else
                                    synckey.append("|" + jb.getInt("Key") + "_" + jb.getInt("Val"));
                            }
                            wechat.keyString=synckey.toString();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //Log.e("wx","newMsg:"+newMsg);
                    MsgBean msgBean=gson.fromJson(newMsg, MsgBean.class);
                    List<MsgBean.AddMsgListEntity> msgList = msgBean.getAddMsgList();
                    for (MsgBean.AddMsgListEntity addMsgListEntity : msgList) {
                        if (addMsgListEntity.getFromUserName().startsWith("@")) {
                            if(addMsgListEntity.getMsgType()!=51){
                                String msg=addMsgListEntity.getContent();

                                if(addMsgListEntity.getFromUserName().contains("@@")){
                                    if(msg.indexOf("<br/>")>=0){
                                        msg=msg.substring(msg.indexOf("<br/>")+5,msg.length()-1);
                                    }
                                }
                                else{
                                    if(msg.indexOf("<br/>")>=0){
                                        msg=msg.substring(0,msg.indexOf("<br/>")-1);

                                    }
                                }

                                //Log.e("wxmsg", msg);

                                WeiXinMessage message=new WeiXinMessage();
                                message.StringMessage=msg;
                                message.FromUser=addMsgListEntity.getFromUserName();

                                if(addMsgListEntity.getMsgType()==34){
                                    message.MessageType=addMsgListEntity.getMsgType();
                                    message.Mp3Message=addMsgListEntity.getMsgId();
                                    message.StringMessage="[语音][按确认播放]";
                                    mNewMsgListener.onNewMsg(message);
                                }
                                if(addMsgListEntity.getMsgType()==43){
                                    message.MessageType=addMsgListEntity.getMsgType();
                                    message.VedioMessage=addMsgListEntity.getMsgId();
                                    message.StringMessage="[视频][按确认播放]";
                                    mNewMsgListener.onNewMsg(message);
                                }
                                else if(addMsgListEntity.getMsgType()==3){
                                    message.MessageType=addMsgListEntity.getMsgType();
                                    message.ImageMessage=addMsgListEntity.getMsgId();
                                    message.StringMessage="[图片][按确认打开]";
                                    mNewMsgListener.onNewMsg(message);
                                }
                                else if(addMsgListEntity.getUrl().length()>5 && addMsgListEntity.getUrl().contains("map")){
                                    message.MessageType=99;
//                                    String location=  HudDisplayActivity.split(addMsgListEntity.getUrl(),"=")[1];
//                                    String[] locations=  HudDisplayActivity.split(location,",");
//                                    message.Latitude=Double.parseDouble(locations[0]);
//                                    message.Longitude=Double.parseDouble(locations[1]);
                                    message.StringMessage="[位置]："+message.StringMessage+"[按确认导航]";
                                    message.Address=message.StringMessage;
                                    mNewMsgListener.onNewMsg(message);
                                }
                                else if(addMsgListEntity.getMsgType()==1)
                                {
                                    message.FromUser=addMsgListEntity.getFromUserName();
                                    message.MessageType=addMsgListEntity.getMsgType();
                                    mNewMsgListener.onNewMsg(message);
                                }

                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        }//while
    }
}

