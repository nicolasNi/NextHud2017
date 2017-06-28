package com.lt.nexthud2017.weixin;

import android.content.Context;

import com.lt.nexthud2017.base.BaseViewModel;

/**
 * Created by Administrator on 2017/6/28.
 */

public class WeiXinViewModel extends BaseViewModel{
    private Context mContext;
    private WeiXinFragment weiXinFragment;

    public WeiXinViewModel (Context context){
        mContext = context;
    }

    public void setWeiXinFragment(WeiXinFragment fragment){
        weiXinFragment = fragment;
    }


    @Override
    protected void appendText(String text, boolean isLast) {

    }
}
