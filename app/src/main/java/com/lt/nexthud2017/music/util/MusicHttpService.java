package com.lt.nexthud2017.music.util;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/5/12.
 */

public interface MusicHttpService {
    @GET("base/fcgi-bin/fcg_musicexpress.fcg")
    Observable<JsonObject> getKeyJson(@Query("guid") String guid, @Query("format") String format);

    @GET("fcgi-bin/music_search_new_platform")
    Observable<JsonObject> getMusicsJson(@Query("n") String n,
                                         @Query("format") String format,
                                         @Query("inCharset") String inCharset,
                                         @Query("outCharset") String outCharset,
                                         @Query("w") String w);
}
