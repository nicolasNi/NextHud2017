package com.lt.nexthud2017.music;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.base.BaseViewModel;
import com.lt.nexthud2017.music.util.MusicAdapter;
import com.lt.nexthud2017.music.util.MusicDBHelper;
import com.lt.nexthud2017.music.util.MusicService;
import com.lt.nexthud2017.music.util.SearchKeyUtil;
import com.lt.nexthud2017.music.util.SearchMusicUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Administrator on 2017/5/12.
 */

public class MusicViewModel extends BaseViewModel {
    private Context mContext;
    private List<Music> listSearchResult;
    public ObservableField<MusicAdapter> musicAdapter = new ObservableField<>();
    private MusicFragment musicFragment;
    private Subscriber<String> keySubscriber;
    private Subscriber<List<Music>> musicSubscriber;
    private MusicService.ControlMusicBinder musicBinder;
    private MusicDBHelper musicDBHelper;
    private SQLiteDatabase db;
    private DownloadManager downloadManager;
    private boolean displayMusicHistory = true;
    public ObservableField<String> searchKey = new ObservableField<>();


    public MusicViewModel (Context context){
        mContext = context;
        listSearchResult = new ArrayList<Music>();
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, musicServiceConnection,BIND_AUTO_CREATE);
        initialDB();
        if(displayMusicHistory){
            displayMusicHistory();
        }
        searchKey.set("");
    }

    public void setMusicFragment(MusicFragment fragment){
        musicFragment = fragment;
    }

    public void searchMusics(){
        displayMusicHistory = false;
        createSubscriber(searchKey.get());
        SearchKeyUtil.getInstance().getKeyJson(keySubscriber);
    }

    private void initialDB() {
        musicDBHelper = new MusicDBHelper(mContext,"hud.db",null,1);
        db = musicDBHelper.getWritableDatabase();
    }

    private void createSubscriber(final String searchText){
        musicSubscriber = new Subscriber<List<Music>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<Music> musics) {
                listSearchResult = musics;
                if(musics.size()>0){
                    musicFragment.undisplayCursor();
                }
                musicAdapter.set(new MusicAdapter(MainActivity.mainContext,listSearchResult));
            }
        };

        keySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String key) {
                SearchMusicUtil.getInstance().getMusicsJson(musicSubscriber,searchText,new SearchMusicUtil.ParseMusicsFromResult(key));
            }
        };
    }

    public void moveToNextSong(){
        MusicAdapter adapter = musicAdapter.get();
        if(adapter != null && (adapter.selectItem< adapter.getCount()-1)){

            adapter.selectItem++;
            adapter.notifyDataSetChanged();
            musicFragment.scrollListView(adapter.selectItem);
        }
        if(adapter.selectItem == 0){
            musicFragment.undisplayCursor();
        }
    }

    public void moveToPreviousSong(){
        MusicAdapter adapter = musicAdapter.get();
        if(adapter != null && (adapter.selectItem >= 0)){
            adapter.selectItem--;
            adapter.notifyDataSetChanged();
            musicFragment.scrollListView(adapter.selectItem);
        }
        if(adapter.selectItem == -1){
            musicFragment.displayCursor();
        }
    }

    public void playMusic(){
        MusicAdapter adapter = musicAdapter.get();
        if(adapter != null ){
            Music music = (Music)adapter.getItem(adapter.selectItem);
            String url = music.getPath();
            if (displayMusicHistory) {
                File rootFile = android.os.Environment.getExternalStorageDirectory();
                File file = new File(rootFile.getPath() + "/com.lt.nexthud2017/myDownLoadMusic/" + music.getMusciName() + "-" + music.getAirtistName() + "-" + music.getAlbumName() + ".m4a");
                url = file.getPath();
            }
            if (!isExist(music)) {
                getANewSong(music);
                download(music);
           }
            musicBinder.playMusic(url);
        }
    }

    private boolean isExist(Music music) {
        boolean exits = false;
        Cursor cursor = db.query("music",null,"musciName = ? and airtistName = ? and albumName = ?",new String[]{music.getMusciName(), music.getAirtistName(), music.getAlbumName()},null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                int No = cursor.getInt(cursor.getColumnIndex("No"));
                if (No > 0) {
                    exits = true;
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        return exits;
    }

    private void getANewSong(Music newMusic) {
        deleteThe50thMusicFromFolder();
        deletThe50thSong();
        updateMusicList();
        addTheNewSong(newMusic);
    }

    private void deleteThe50thMusicFromFolder(){
        Cursor cursor = db.query("music",null,"No = ?",new String[]{"50"},null,null,null);
        if(cursor.moveToFirst()){
            String musicName = cursor.getString(cursor.getColumnIndex("musciName"));
            String airtistName = cursor.getString(cursor.getColumnIndex("airtistName"));
            String albumName = cursor.getString(cursor.getColumnIndex("albumName"));
            File rootFile = android.os.Environment.getExternalStorageDirectory();
            File file = new File(rootFile.getPath() + "/com.lt.nexthud.onlinemusic2/myDownLoadMusic/" +musicName + "-" + airtistName + "-" + albumName + ".m4a");
            if(file.isFile()){
                file.delete();
            }
        }
    }

    private void deletThe50thSong() {
        db.delete("music", "No = ?", new String[]{"50"});
    }

    private void updateMusicList() {
        Cursor cursor = db.rawQuery("select count(*)from music", null);
        //游标移到第一条记录准备获取数据
        cursor.moveToFirst();
        // 获取数据中的LONG类型数据
        int count = cursor.getInt(0);

        for (int i = count; i > 0; i--) {
            ContentValues cv = new ContentValues();
            cv.put("No", i + 1);
            db.update("music", cv, "No = ?", new String[]{Integer.toString(i)});
        }
    }

    private void addTheNewSong(Music newMusic) {
        newMusic.No = 1;
        //插入数据
        ContentValues contentValues = new ContentValues();
        contentValues.put("musciName",newMusic.getMusciName());
        contentValues.put("airtistName",newMusic.getAirtistName());
        contentValues.put("albumName",newMusic.getAlbumName());
        contentValues.put("No",newMusic.No);
        db.insert("music",null,contentValues);
    }

    private List<Music> getMusicListFromDB() {
        List<Music> musicList = new ArrayList<Music>();
        Cursor cursor = db.query("music",null,null,null,null,null,"No ASC");
        if(cursor.moveToFirst()){
            do{
                String musciName = cursor.getString(cursor.getColumnIndex("musciName"));
                String airtistName = cursor.getString(cursor.getColumnIndex("airtistName"));
                String albumName = cursor.getString(cursor.getColumnIndex("albumName"));
                Music music = new Music();
                music.setMusciName(musciName);
                music.setAirtistName(airtistName);
                music.setAlbumName(albumName);
                musicList.add(music);
            }
            while (cursor.moveToNext());
        }
        return musicList;
    }

    private void download(final Music downloadMusic) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String downloadUrl = downloadMusic.getPath();
                Uri resource = Uri.parse(downloadUrl);
                DownloadManager.Request request = new DownloadManager.Request(resource);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(true);
                // 设置文件类型
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String mimeString = mimeTypeMap
                        .getMimeTypeFromExtension(MimeTypeMap
                                .getFileExtensionFromUrl(downloadUrl));
                request.setMimeType(mimeString);

//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setVisibleInDownloadsUi(false);
                // sdcard的目录下的download文件夹
                String musicName = downloadMusic.getMusciName() + "-" + downloadMusic.getAirtistName() + "-" + downloadMusic.getAlbumName() + ".m4a";
                request.setDestinationInExternalPublicDir(mContext.getPackageName() + "/myDownLoadMusic", musicName);
                downloadManager.enqueue(request);
            }
        }).start();
    }

    public void displayMusicHistory() {
        displayMusicHistory = true;
        listSearchResult = getMusicListFromDB();
        musicAdapter.set(new MusicAdapter(mContext,listSearchResult));
    }

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.ControlMusicBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void pressCenter(){
        MusicAdapter adapter = musicAdapter.get();
        if(adapter == null ||adapter.getCount()==0|| adapter.selectItem < 0){
            musicBinder.pauseMusic();
            searchKey.set("");
            startIat();
        }else {
            playMusic();
        }
    }

    @Override
    protected void appendText(String text, boolean isLast) {
        if (text != null && !text.trim().equals("") && text.length() > 0) {
            searchKey.set(searchKey.get()+text);
        }
        if (isLast) {
            searchMusics();
        }
    }
}
