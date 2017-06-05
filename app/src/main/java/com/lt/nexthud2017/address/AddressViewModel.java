package com.lt.nexthud2017.address;

import android.app.DownloadManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.address.util.AddressAdapter;
import com.lt.nexthud2017.base.BaseViewModel;
import com.lt.nexthud2017.music.Music;
import com.lt.nexthud2017.music.MusicFragment;
import com.lt.nexthud2017.music.util.MusicAdapter;
import com.lt.nexthud2017.music.util.MusicDBHelper;
import com.lt.nexthud2017.music.util.MusicService;

import java.util.ArrayList;
import java.util.HashMap;
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
    private boolean displayAddressHistory = true;
    private PoiSearch.Query query;
    String cityAddress="";
    GeocodeSearch geocoderSearch;


    private AddressFragment addressFragment;
    private Subscriber<String> keySubscriber;
    private Subscriber<List<Music>> musicSubscriber;
    private MusicService.ControlMusicBinder musicBinder;
    private MusicDBHelper musicDBHelper;
    private SQLiteDatabase db;
    private DownloadManager downloadManager;


    public AddressViewModel(Context context){
        mContext = context;
        listSearchResult = new ArrayList<Address>();
        if (geocoderSearch == null) {
            geocoderSearch = new GeocodeSearch(MainActivity.mainContext);
            geocoderSearch.setOnGeocodeSearchListener(geocodeSearchListener);
        }
        LatLonPoint llp = new LatLonPoint(MainActivity.Latitude,
                MainActivity.Longitude);
        if(MainActivity.Latitude<1){
            llp = new LatLonPoint((double)31.2776,
                    (double)121.189262);
            MainActivity.Latitude=(double)31.2776;
            MainActivity.Longitude=(double)121.189262;
        }

        RegeocodeQuery query = new RegeocodeQuery(llp, (float) 100, "1");
        geocoderSearch.getFromLocationAsyn(query);
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
            searchAddress();
        }
    }

    public void pressCenter(){
        AddressAdapter adapter = addressAdapter.get();
        if(adapter == null ||adapter.getCount()==0|| adapter.selectItem < 0){
//            musicBinder.pauseMusic();
            searchKey.set("");
            startIat();
        }else {
            nav();
        }
    }

    public void nav() {
        MainActivity.addHudFragment();
//        if (listSearchResult == null || listSearchResult.size() < 1) {
//            return;
//        }
//        Address address = listSearchResult.get(rowIndex);
//        // add address history
//        if (!isExist(address)) {
//            getANewAddress(address);
//            // download(musicPlayed);
//        }
//
//        PageSwitcher.showPage(PAGETYPE.hudPage);
//
//
//        MainActivity.hud.caclRoute("",MainActivity.Latitude,MainActivity.Longitude,
//                address.latitude, address.longitude,2);

    }


    private void searchAddress() {
        displayAddressHistory = false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                String address = searchKey.get();
                query = new PoiSearch.Query(address, "", cityAddress);
                query.setPageSize(10);// 设置每页最多返回多少条poiitem
                query.setPageNum(0);
                PoiSearch poiSearch = new PoiSearch(MainActivity.mainContext, query);
                poiSearch.setOnPoiSearchListener(listener);
                poiSearch.searchPOIAsyn();
            }
        }).start();
    }

    PoiSearch.OnPoiSearchListener listener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            listSearchResult.clear();
            if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(query)) {// 是否是同一条
                        PoiResult poiResult = result;
                        // 取得搜索到的poiitems有多少页
                        List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        List<SuggestionCity> suggestionCities = poiResult
                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                        if (poiItems != null && poiItems.size() > 0) {
                            for (PoiItem item : poiItems) {
                                Address address = new Address();
                                address.address = item.getTitle();
                                address.latitude = item.getLatLonPoint()
                                        .getLatitude();
                                address.longitude = item.getLatLonPoint()
                                        .getLongitude();
                                listSearchResult.add(address);
                            }
                            Message msg = new Message();
                            msg.what = 0;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    addressAdapter.set(new AddressAdapter(MainActivity.mainContext,listSearchResult));
                    break;
            }
        };
    };


    private GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            cityAddress = regeocodeResult.getRegeocodeAddress().getCityCode();
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };


    public void moveToNextAddress(){
        AddressAdapter adapter = addressAdapter.get();
        if(adapter != null && (adapter.selectItem< adapter.getCount()-1)){

            adapter.selectItem++;
            adapter.notifyDataSetChanged();
            addressFragment.scrollListView(adapter.selectItem);
        }
        if(adapter.selectItem == 0){
            addressFragment.undisplayCursor();
        }
    }

    public void moveToPreviousAddress(){
        AddressAdapter adapter = addressAdapter.get();
        if(adapter != null && (adapter.selectItem >= 0)){
            adapter.selectItem--;
            adapter.notifyDataSetChanged();
            addressFragment.scrollListView(adapter.selectItem);
        }
        if(adapter.selectItem == -1){
            addressFragment.displayCursor();
        }
    }
}
