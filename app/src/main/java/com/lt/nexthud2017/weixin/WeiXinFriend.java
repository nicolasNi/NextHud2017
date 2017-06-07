package com.lt.nexthud2017.weixin;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class WeiXinFriend {
    public String nameId;
    public String nickName;
    public String headImgUrl;
    public String remarkName;
    public Bitmap headImg;
    public List<WeiXinMessage> messages;
    public Date lastSendDate;

    public int getMessageSize()
    {
        int count=0;
        if(messages!=null){
            for(WeiXinMessage msg:messages){
                if(!msg.isRead){
                    count++;
                }
            }
        }
        return count;
    }

    public void setMsgRead()
    {
        if(messages!=null){
            for(WeiXinMessage msg:messages){
                msg.isRead=true;
            }
        }
    }
}
