package com.lt.nexthud2017.music.util;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import com.lt.nexthud2017.music.Music;

import java.util.ArrayList;
import java.util.List;

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

public class SearchMusicUtil {
    public static final String BASE_URL =  "http://s.music.qq.com/";

    private Retrofit retrofit;
    private MusicHttpService musicHttpServiceService;

    //构造方法私有
    private SearchMusicUtil() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        musicHttpServiceService = retrofit.create(MusicHttpService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final SearchMusicUtil INSTANCE = new SearchMusicUtil();
    }

    //获取单例
    public static SearchMusicUtil getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getMusicsJson(Subscriber<List<Music>> subscriber, String keyword, ParseMusicsFromResult parseMusicsFromResult){
        Log.d("getKeyJson===",Thread.currentThread().getName());
        musicHttpServiceService.getMusicsJson("10", "json", "GB2312", "utf-8", keyword)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(parseMusicsFromResult)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    static public class ParseMusicsFromResult implements Func1<JsonObject,List<Music>> {
        private String key;

        public ParseMusicsFromResult(String key){
            this.key = key;
        }
        @Override
        public List<Music> call(JsonObject jsonObject) {
            List<Music> musicList = new ArrayList<>();
            try
            {
                JSONObject json=new JSONObject(jsonObject.toString());
                JSONObject songData = json.getJSONObject("data");
                JSONObject song = songData.getJSONObject("song");
                JSONArray listObj = (JSONArray)song.getJSONArray("list");

                for(int i=0; i < listObj.length()-1; i++)
                {
                    JSONObject music =(JSONObject)listObj.get(i);
                    String musciName = music.getString("fsong");
                    String airtistName = music.getString("fsinger");
                    String albumName = music.getString("albumName_hilight");
                    if (albumName.contains(">"))
                    {
                        albumName = albumName.substring(albumName.indexOf(">")+1);
                        albumName = albumName.substring(0, albumName.indexOf("<"));
                    }
                    String f = music.getString("f");
                    String[] fForSongID = f.split("\\u007C");
                    String musicId = fForSongID[20];
                    String path = "http://ws.stream.qqmusic.qq.com/C200"+musicId+".m4a?vkey="+key+"&guid=7524721365&fromtag=30";
                    musicList.add(new Music(musciName,airtistName,path,albumName,musicId));
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return musicList;
        }
    }
}

