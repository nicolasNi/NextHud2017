package com.lt.nexthud2017.weixin.util;

/**
 * Created by Administrator on 2017/6/7.
 */

public class StringSubClass {

    public  String subStringOne(String from,String start,String end) {
        int s=from.indexOf(start)+start.length();
        int e=from.indexOf(end,s);
        return from.substring(s, e);
    }
    public  String[] subStringAll(String from,String start,String end) {
        int fromIndex=0;
        int count=0;
        int startStringLength=0;
        int endStringLength=end.length();
        while ((fromIndex=from.indexOf(start, fromIndex+startStringLength))!=-1) {
            if (count==0) {
                startStringLength=start.length();
            }
            count++;
        }//��ȡ���м���ƥ�����
        String returnString[]=new String[count];
        for(int j=0;j<count;j++){
            returnString[j]=subStringOne(from, start, end);
            from=from.substring(from.indexOf(end)+endStringLength);
        }
        return returnString;
    }

}