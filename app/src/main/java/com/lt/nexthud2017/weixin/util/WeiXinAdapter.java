package com.lt.nexthud2017.weixin.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.R;
import com.lt.nexthud2017.weixin.WeiXinFragment;
import com.lt.nexthud2017.weixin.WeiXinFriend;

/**
 * Created by Administrator on 2017/6/7.
 */

public class WeiXinAdapter extends BaseAdapter {
    private static final String TAG = "WeiXinAdapter";
    private Context mContext;
    WeiXinFragment wx;
    public WeiXinAdapter(Context mContext,WeiXinFragment wx) {
        this.mContext = mContext;
        this.wx=wx;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getCount() {
        return wx.getMyFriend().size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.weixin, null);
        ImageView image = (ImageView) convertView.findViewById(R.id.wxfriendhead);
        TextView friend = (TextView) convertView.findViewById(R.id.wxfriendname);
        WeiXinFriend wxfriend= wx.getMyFriend().get(position);
        image.setTag(wxfriend.headImgUrl);
        if(wxfriend.remarkName.equals("")){
            friend.setText(wxfriend.nickName);
        }
        else{
            friend.setText(wxfriend.remarkName);
        }
        if(wxfriend.messages!=null){
            int size=wxfriend.getMessageSize();
            if(size>0){
                BadgeView badge1 = new BadgeView(MainActivity.mainContext, image);
                //badge1.setRotationY(180);
                badge1.setText(size+"");
                badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                badge1.setTextColor(Color.WHITE);
                badge1.setBadgeBackgroundColor(Color.RED);
                badge1.setTextSize(12);
                badge1.show();
            }
        }
        new CanvasImageTask().execute(image);//异步加载图片
        Log.i(TAG, "execute:"+wxfriend.nickName);
        return convertView;
    }
}
