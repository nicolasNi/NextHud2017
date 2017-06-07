package com.lt.nexthud2017.weixin;

/**
 * Created by Administrator on 2017/6/7.
 */

public class WeiXinMessage {
    public boolean isRead;
    public String FromUser;
    public String FromName;
    public int MessageType;
    public String StringMessage;
    public String ImageMessage;
    public String Mp3Message;
    public byte[] Mp3MessageFile;
    public String AddressMessage;
    public String Address;
    public double Latitude;
    public double Longitude;
    public String SendTime;
    public String VedioMessage;
    public String EMOTICONMessage;
    //<!-- ngIf: message.MsgType == CONF.MSGTYPE_EMOTICON -->
}
