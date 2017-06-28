package com.lt.nexthud2017.weixin.util;

import okhttp3.Call;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/6/15.
 */

public interface WeChatService {
    @GET("jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_=jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_=")
    Observable<ResponseBody> getUUID();

    @GET("cgi-bin/mmwebwx-bin/login")
    Observable<ResponseBody> getLoginResult(@Query("tip")int tip, @Query("uuid")String uuid);

    @GET("")
    retrofit2.Call<ResponseBody> get();


    @GET("cgi-bin/mmwebwx-bin/login")
    retrofit2.Call<ResponseBody> getResult(@Query("tip")String tip, @Query("uuid")String uuid);

    @FormUrlEncoded
    @POST
    retrofit2.Call<ResponseBody> getInitialResult(@Body ResponseBody body);
}
