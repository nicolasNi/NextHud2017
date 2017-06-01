package com.lt.nexthud2017.address;

import android.app.DownloadManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableField;

import com.lt.nexthud2017.address.util.AddressAdapter;
import com.lt.nexthud2017.base.BaseViewModel;
import com.lt.nexthud2017.music.Music;
import com.lt.nexthud2017.music.MusicFragment;
import com.lt.nexthud2017.music.util.MusicAdapter;
import com.lt.nexthud2017.music.util.MusicDBHelper;
import com.lt.nexthud2017.music.util.MusicService;

import java.util.List;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/6/1.
 */

public class AddressViewModel extends BaseViewModel {
    private Context mContext;
    private List<Address> listSearchResult;
    public ObservableField<AddressAdapter> addressAdapter = new ObservableField<>();
    public ObservableField<String> searchKey = new ObservableField<>();



    private AddressFragment addressFragment;
    private Subscriber<String> keySubscriber;
    private Subscriber<List<Music>> musicSubscriber;
    private MusicService.ControlMusicBinder musicBinder;
    private MusicDBHelper musicDBHelper;
    private SQLiteDatabase db;
    private DownloadManager downloadManager;
    private boolean displayMusicHistory = true;


    public AddressViewModel(Context context){
        mContext = context;
    }

    public void setAddressFragment(AddressFragment fragment){
        addressFragment = fragment;
    }

    @Override
    protected void appendText(String text, boolean isLast) {
        if (text != null && !text.trim().equals("") && text.length() > 0) {
            searchKey.set(searchKey.get()+text);
        }
        if (isLast) {
//            searchMusics();
        }
    }

    public void pressCenter(){
        AddressAdapter adapter = addressAdapter.get();
        if(adapter == null ||adapter.getCount()==0|| adapter.selectItem < 0){
//            musicBinder.pauseMusic();
            searchKey.set("");
            startIat();
        }else {
//            playMusic();
        }
    }
}
