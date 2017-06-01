package com.lt.nexthud2017.address;

import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amap.api.navi.AMapHudView;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.R;
import com.lt.nexthud2017.address.util.AddressAdapter;


import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lt.nexthud2017.databinding.ActivityAddressBinding;
import com.lt.nexthud2017.databinding.MusicFragmentBinding;
import com.lt.nexthud2017.music.MusicViewModel;

import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AddressFragment extends Fragment {

    static int rowIndex = -1;
    private ListView lvSearchReasult;
    private List<Address> listSearchResult;
    AddressAdapter adapter;
    boolean isShowing = false;
    GeocodeSearch geocoderSearch;
    String cityAddress;

    // add address history
    private boolean displayAddressHistory = true;
    private SQLiteDatabase db;


    private AddressViewModel addressViewModel;
    private ActivityAddressBinding addressFragmentBinding;
    private EditText edtKey;
    private ListView addressListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createBinding(inflater, container);
        edtKey = addressFragmentBinding.addressSearch;
        edtKey.setCursorVisible(false);
        addressListView = addressFragmentBinding.addressSearchList;
        if(addressViewModel != null){
            addressFragmentBinding.setViewModel(addressViewModel);
        }
        return addressFragmentBinding.getRoot();

//        PoiSearch search;
//        PoiSearch.Query query;
//        query = new PoiSearch.Query("东方明珠", null, "上海");
//        query.setPageSize(10);
//        query.setPageNum(1);
//
//        // 查询兴趣点
//        search = new PoiSearch(MainActivity.mainContext, query);
//        // 异步搜索
//        search.searchPOIAsyn();
//        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
//            @Override
//            public void onPoiSearched(PoiResult poiResult, int j) {
//                List<String> strs = new ArrayList<String>();
//                List<PoiItem> items = poiResult.getPois();
//                if (items != null && items.size() > 0) {
//                    PoiItem item = null;
//                    for (int i = 0, count = items.size(); i < count; i++) {
//                        item = items.get(i);
//                        strs.add(item.getTitle());
//                    }
//                }
//            }
//
//            @Override
//            public void onPoiItemSearched(PoiItem poiItem, int i) {
//
//            }
//        });

    }

    private void createBinding(LayoutInflater inflater, ViewGroup container){
        addressFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.activity_address,container,false);
    }

    public void setAddressViewModel(AddressViewModel viewModel){
        addressViewModel = viewModel;
        if(addressFragmentBinding != null){
            addressFragmentBinding.setViewModel(addressViewModel);
        }
    }

    public void changeAction(String param) {
        if (param.contains("up")) {
//            addressViewModel.moveToPreviousSong();

        } else if (param.contains("down")) {
//            addressViewModel.moveToNextSong();
        } else if (param.contains("center")) {
            addressViewModel.pressCenter();
        } else if (param.contains("right")) {
        }
    }
}
