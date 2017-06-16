package com.lt.nexthud2017.weixin.util;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/15.
 */

public class WeChatHttpMethod {
    public static final String BASE_URL = "https://login.weixin.qq.com/";
    private Retrofit retrofit;
    private WeChatService weChatService;
    private StringSubClass ss = new StringSubClass();

    private WeChatHttpMethod(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        weChatService = retrofit.create(WeChatService.class);
    }

    private static class SingletonHolder{
        private static final WeChatHttpMethod INSTANCE = new WeChatHttpMethod();
    }

    public static WeChatHttpMethod getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getUUID(Action1<String> action1){
        weChatService.getUUID()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
                        String result="";
                        try {
                            result = responseBody.string();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        return ss.subStringOne(result, ".uuid = \"", "\";");
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(action1);
    }

    public void getLoginCode(int tip, String uuid, Action1<String> action1){
        weChatService.getLoginCode(tip, uuid+ "&_=")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
                        String code = "";
                        try {
                            code = responseBody.string();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return code;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(action1);
    }
}
