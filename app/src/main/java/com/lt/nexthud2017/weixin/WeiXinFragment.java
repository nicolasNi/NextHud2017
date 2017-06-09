package com.lt.nexthud2017.weixin;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.R;
import com.lt.nexthud2017.setting.Brightness;
import com.lt.nexthud2017.weixin.util.HeartBeatThread;
import com.lt.nexthud2017.weixin.util.HttpClient;
import com.lt.nexthud2017.weixin.util.WaitScanAndLoginThread;
import com.lt.nexthud2017.weixin.util.WeiXinAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.Observer;
import rx.Observable;
import rx.functions.Action1;

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
    boolean isLoad = false;
    int reflash = 0;
    public static WeChatClass weChat = null;
    ImageView qrcode;
    ListView lv;
    int friendCount = 0;
    boolean isLogin = false;
    WeiXinAdapter adapter;
    static int rowIndex = 0;
    private Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wxStack = new Stack<WeiXinMessage>();
        friendCache = new ArrayList<WeiXinFriend>();
        allFriendCache = new ArrayList<WeiXinFriend>();
        wxApi = this;
        weixinView = inflater.inflate(R.layout.activity_weixin, container, false);
        qrcode = (ImageView) weixinView.findViewById(R.id.qrcode);
        initWeChat();
        return weixinView;
    }

    public List<WeiXinFriend> getMyFriend() {
        return friendCache;
    }

    public void shoWeiXinFragment() {
        if (!isLogin) {
            isLoad = false;
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.weixin);
            qrcode.setImageBitmap(bmp);

//            if (baseTimer == null) {
//                baseTimer = new Timer(true);
//                baseTimer.schedule(baseTask, 100, 25000);
//            }
            startToKeepRunningGogogoMethod();
        } else if (adapter != null) {
            lv.setAdapter(adapter);
            if (adapter.getCount() > 0) {
                rowIndex = 0;
                DelyChangeColor();
            }
        }
    }

    private void startToKeepRunningGogogoMethod(){
        subscription = Observable.interval(0, 25, TimeUnit.SECONDS).subscribe(
                new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        try {
                            if (!isLogin) {
                                try {
                                    weChat.gogogo();
                                    Log.e("weixin", "reflash");
                                } catch (Exception ex2) {
                                    Log.e("weixin2", ex2.toString());
                                }
                            }
                        } catch (Exception ex) {
                            isLoad = false;
                            Log.e("weixin", ex.toString());
                        }
                    }
                }
        );
    }


    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            try {
                if (!isLoad) {
                    isLoad = true;
                    weChat.gogogo();
                }
                reflash++;
                if (reflash % 25 == 0 && !isLogin) {
                    try {
                        reflash = 1;
                        weChat.gogogo();
                        Log.e("weixin", "reflash");
                    } catch (Exception ex2) {
                        Log.e("weixin2", ex2.toString());
                    }
                }
            } catch (Exception ex) {
                isLoad = false;
                Log.e("weixin", ex.toString());
            }
        }
    };


    void initWeChat() {
        weChat = new WeChatClass();

        weChat.setmScanListener(new WaitScanAndLoginThread.OnScanListener() {

            @Override
            public void onSure() {
                Log.e("", "登陆成功");
                isLogin = true;
                MainActivity.mainContext.runOnUiThread(new Runnable() {
                    public void run() {
                        String value = Brightness.readBrightness();
                        if (value != null) {
                            Brightness.sendBrightness(value);
                        }

                        qrcode.setVisibility(View.GONE);
                        if (lv != null) {
                            lv.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onScan() {
                isLogin = true;
                Log.e("", "已经扫描成功，等待确认登陆");
            }

            @Override
            public void onReload() {
                isLogin = false;
                new Thread(networkTask).start();
            }

            @Override
            public void onInitMyFriends(final List<WeiXinFriend> friends) {

                MainActivity.mainContext.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (lv == null)
                                lv = (ListView) weixinView.findViewById(R.id.weixinList);
                            friendCache.clear();
                            friendCache.addAll(friends);
                            adapter = new WeiXinAdapter(MainActivity.mainContext, wxApi);
                            lv.setAdapter(adapter);
                            friendCount = friends.size();
                            if (friends.size() > 0) {
                                rowIndex = 0;
                                DelyChangeColor();
                            }
                        } catch (Exception ex) {
                            Log.e("", ex.toString());
                        }
                    }
                });
            }

            @Override
            public void onInitMyAllFriends(List<WeiXinFriend> friends) {
                allFriendCache.clear();
                allFriendCache.addAll(friends);
            }
        });

        weChat.setmNewMsgListener(new HeartBeatThread.OnNewMsgListener() {
            @Override
            public void startBeat() {
                Log.e("", "开始心跳");
            }

            @Override
            public void onNewMsg(WeiXinMessage message) {
                handleMessage(message);
            }

            @Override
            public void logout() {
                isLogin = false;
                reflash = -1;
                rowIndex = 0;
                weChat.logout();
                WeChatClass.keyString = "";
                WeChatClass.myUserId = "";
                WeChatClass.pass_ticket = "";
                WeChatClass.skey = "";
                WeChatClass.wxuin = "";
                WeChatClass.wxsid = "";
                HttpClient.reset();
                MainActivity.mainContext.runOnUiThread(new Runnable() {
                    public void run() {

                        qrcode.setVisibility(View.VISIBLE);
                        Bitmap wxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.weixin);
                        qrcode.setImageBitmap(wxBitmap);

                        if (lv != null) {
                            friendCache.clear();
                            adapter.notifyDataSetChanged();
                            lv.setVisibility(View.GONE);
                        }

//                        PageSwitcher.showPage(PAGETYPE.weixinPage);

                        new Thread(networkTask).start();
                    }
                });

            }
        });

        weChat.setmQrCodeListener(new WeChatClass.OnLoadQrCodeListener() {

            @Override
            public void onLoadSuccess(final byte[] imageBytes) {

                Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                final Bitmap newBm = toHeibai(bm);

                MainActivity.mainContext.runOnUiThread(new Runnable() {
                    public void run() {
                        try {

                            Log.e("weixin", "qrcode gegin");
                            qrcode.setImageBitmap(newBm);
                            Log.e("", "qrcode ok");
                        } catch (Exception ex) {
                            Log.e("", "qrcode error");
                        }
                    }
                });

            }

        });

    }



    private void DelyChangeColor() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                    change();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

    void change() {
        MainActivity.mainContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShowing) {
                    View arg0 = lv.getChildAt(0);
                    if (arg0 != null) {
                        TextView tv_search_list_title = (TextView) arg0.findViewById(R.id.wxfriendname);
                        tv_search_list_title.setTextColor(Color.rgb(23, 169, 200));
                    }
                } else {
//                    if (MainActivity.chat.getShow()) {
//                        MainActivity.chat.reloadMessageList();
//                    }
                }
            }
        });
    }

    public static Bitmap toHeibai(Bitmap mBitmap) {
        int mBitmapWidth = 0;
        int mBitmapHeight = 0;

        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight,
                Bitmap.Config.ARGB_8888);
        //int iPixel = 0;
        for (int i = 0; i < mBitmapWidth; i++) {
            for (int j = 0; j < mBitmapHeight; j++) {
                int curr_color = mBitmap.getPixel(i, j);

                int avg = (Color.red(curr_color) + Color.green(curr_color) + Color
                        .blue(curr_color)) / 3;
                int modif_color = 0;
                if (avg >= 10) {
                    //85,107,47
                    modif_color = Color.argb(255, 139, 69, 0);
                } else {
                    // iPixel = 0;
                    modif_color = 0;//Color.argb(255, iPixel, iPixel, iPixel);
                }


                bmpReturn.setPixel(i, j, modif_color);
            }
        }
        return bmpReturn;
    }

    void handleMessage(WeiXinMessage message) {
        boolean hasSession = false;
        if (friendCache != null) {
            for (int i = 0; i < friendCache.size(); i++) {
                WeiXinFriend friend = friendCache.get(i);
                if (friend.nameId.equals(message.FromUser)) {
                    MainActivity.mainContext.soundPool.play(MainActivity.mainContext.soundWXId, (float) 15.0, (float) 15.0, 1, 0, (float) 1.0);
                    if (friend.messages == null) {
                        friend.messages = new ArrayList<WeiXinMessage>();
                    }
                    message.FromName = friend.nickName;
                    if (friend.remarkName != null && friend.remarkName.length() > 1) {
                        message.FromName = friend.remarkName;
                    }
                    Log.e("wx", "message.FromName:" + message.FromName);
                    friend.messages.add(message);
//                    if (!isShowing && !MainActivity.chat.getShow()) {
//                        Log.e("wx", "add messag");
//                        wxStack.push(message);
//                    }

                    if (friend.messages.size() > 20) { //默认最多缓存20条消息，包括已经发出去的消息
                        friend.messages.remove(0);
                    }

                    Date curDate = new Date(System.currentTimeMillis());
                    friend.lastSendDate = curDate;
                    friendCache.set(i, friend);
                    hasSession = true;
                    break;
                }
            }
            if (!hasSession) {
                for (int i = 0; i < allFriendCache.size(); i++) {
                    WeiXinFriend friend = allFriendCache.get(i);
                    if (friend.nameId.equals(message.FromUser)) {
                        MainActivity.mainContext.soundPool.play(MainActivity.mainContext.soundWXId, (float) 15.0, (float) 15.0, 1, 0, (float) 1.0);
                        if (friend.messages == null) {
                            friend.messages = new ArrayList<WeiXinMessage>();
                        }
                        message.FromName = friend.nickName;
                        if (friend.remarkName != null && friend.remarkName.length() > 1) {
                            message.FromName = friend.remarkName;
                        }
                        Log.e("wx", "message.FromName:" + message.FromName);
                        friend.messages.add(message);
//                        if (!isShowing && !MainActivity.chat.getShow())
//                            wxStack.push(message);

                        if (friend.messages.size() > 20) { //默认最多缓存20条消息，包括已经发出去的消息
                            friend.messages.remove(0);
                        }

                        Date curDate = new Date(System.currentTimeMillis());
                        friend.lastSendDate = curDate;
                        friendCache.add(friend);
                        hasSession = true;
                        break;
                    }
                }
            }

            Collections.sort(friendCache, new Comparator<WeiXinFriend>() {
                @Override
                public int compare(WeiXinFriend lhs, WeiXinFriend rhs) {
                    Date date1 = lhs.lastSendDate;
                    Date date2 = rhs.lastSendDate;

                    if (date1 == null) {
                        return 1;
                    }

                    if (date2 == null) {
                        return -1;
                    }

                    // 对日期字段进行升序，如果欲降序可采用after方法
                    if (date1.after(date2)) {
                        return 1;
                    }
                    return -1;
                }
            });

            MainActivity.mainContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rowIndex = 0;
                    lv.setAdapter(adapter);
                    DelyChangeColor();
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        String s = "fa";
        Log.d("wechat Destory",s);
    }
}
