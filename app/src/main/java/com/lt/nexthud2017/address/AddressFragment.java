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
    private ListView addressListView;


    // add address history
    private boolean displayAddressHistory = true;
    private SQLiteDatabase db;


    private AddressViewModel addressViewModel;
    private ActivityAddressBinding addressFragmentBinding;
    private EditText edtKey;


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
            addressViewModel.moveToPreviousAddress();

        } else if (param.contains("down")) {
            addressViewModel.moveToNextAddress();
        } else if (param.contains("center")) {
            addressViewModel.pressCenter();
        } else if (param.contains("right")) {
        }
    }

    public void undisplayCursor() {
        if (edtKey != null) {
            edtKey.setFocusable(false);
            edtKey.setCursorVisible(false);
            edtKey.setHintTextColor(Color.WHITE);
            edtKey.setTextColor(Color.WHITE);
        }
    }

    public void displayCursor() {
        if (edtKey != null) {
            edtKey.setFocusable(true);
            edtKey.setHintTextColor(Color.rgb(23, 169, 200));
            edtKey.setTextColor(Color.rgb(23, 169, 200));
        }
    }

    public void scrollListView(int position){
        addressListView.smoothScrollToPosition(position);
    }
}
