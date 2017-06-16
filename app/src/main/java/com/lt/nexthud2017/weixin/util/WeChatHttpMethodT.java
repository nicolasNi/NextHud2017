package com.lt.nexthud2017.weixin.util;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/15.
 */

public class WeChatHttpMethodT {
    public static final String BASE_URL = "https://login.weixin.qq.com/";
    private Retrofit retrofit;
    private WeChatService weChatService;
    private StringSubClass ss = new StringSubClass();

    private WeChatHttpMethodT(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        weChatService = retrofit.create(WeChatService.class);
    }

    private static class SingletonHolder{
        private static final WeChatHttpMethodT INSTANCE = new WeChatHttpMethodT();
    }

    public static WeChatHttpMethodT getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getLoginCode(int tip, String uuid, Subscriber<ResponseBody> subscriber){
        weChatService.getLoginCode(tip, uuid+ "&_=")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
}
