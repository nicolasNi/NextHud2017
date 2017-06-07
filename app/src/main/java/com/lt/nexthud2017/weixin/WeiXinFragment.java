package com.lt.nexthud2017.weixin;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lt.nexthud2017.R;
import com.lt.nexthud2017.weixin.util.WeiXinAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeiXinFragment extends Fragment {

    public static WeiXinFragment wxApi;
    boolean isShowing = false;
    Timer baseTimer = null;

    List<WeiXinFriend> friendCache = null;
    List<WeiXinFriend> allFriendCache = null;

    public static Stack<WeiXinMessage> wxStack;
    View weixinView;

    public static WeChatClass weChat = null;
    ImageView qrcode;
    ListView lv;
    int friendCount = 0;
    boolean isLogin = false;
    WeiXinAdapter adapter;

    public List<WeiXinFriend> getMyFriend() {
        return friendCache;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wxStack = new Stack<WeiXinMessage>();
        friendCache = new ArrayList<WeiXinFriend>();
        allFriendCache = new ArrayList<WeiXinFriend>();
        wxApi = this;
        weixinView = inflater.inflate(R.layout.activity_weixin, container, false);
        qrcode = (ImageView) weixinView.findViewById(R.id.qrcode);
        return weixinView;
    }

//    TimerTask baseTask = new TimerTask() {
//        public void run() {
//            Message message = new Message();
//            message.what = 1;
//            baseHandler.sendMessage(message);
//        }
//    };

    boolean isLoad = false;
    int reflash = 0;


//    final Handler baseHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    new Thread(networkTask).start();
//                    break;
//            }
//        }
//    };

    /**
     * 网络操作相关的子线程
     */
//    Runnable networkTask = new Runnable() {
//
//        @Override
//        public void run() {
//            try {
//                if (!isLoad) {
//                    isLoad = true;
//                    initWeChat();
//                }
//                reflash++;
//                if (reflash % 25 == 0 && !isLogin) {
//                    try {
//                        reflash = 1;
//                        initWeChat();
//                        Log.e("weixin", "reflash");
//                    } catch (Exception ex2) {
//                        Log.e("weixin2", ex2.toString());
//                    }
//                }
//            } catch (Exception ex) {
//                isLoad = false;
//                Log.e("weixin", ex.toString());
//            }
//        }
//    };

}
