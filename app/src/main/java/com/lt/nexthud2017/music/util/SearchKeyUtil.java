package com.lt.nexthud2017.music.util;

import android.util.Log;

import com.google.gson.JsonObject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/12.
 */

public class SearchKeyUtil {
    public static final String BASE_URL =  "http://c.y.qq.com/";

    private Retrofit retrofit;
    private MusicHttpService movieService;

    //构造方法私有
    private SearchKeyUtil() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        movieService = retrofit.create(MusicHttpService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final SearchKeyUtil INSTANCE = new SearchKeyUtil();
    }

    //获取单例
    public static SearchKeyUtil getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getKeyJson(Subscriber<String> subscriber){
        Log.d("getKeyJson===",Thread.currentThread().getName());
        movieService.getKeyJson("7524721365", "json")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new parseKeyFromJson())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private class parseKeyFromJson implements Func1<JsonObject,String> {
        @Override
        public String call(JsonObject jsonObject) {
            return jsonObject.get("key").getAsString();
        }
    }
}
