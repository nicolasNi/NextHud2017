package com.lt.nexthud2017.address.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.amap.api.services.core.LatLonPoint;
import com.lt.nexthud2017.MainActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public class LatLonPointUtil {
    public static LatLonPoint getLatLonPoint()
    {
        LatLonPoint llp;
        LocationManager locationManager = (LocationManager) MainActivity.mainContext.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        String locationProvider="";
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            llp = new LatLonPoint((double)31.2776, (double)121.189262);
        }
        //获取Location
        Location location=null;
        try {
            location = locationManager.getLastKnownLocation(locationProvider);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        if(location!=null){
            llp = new LatLonPoint(location.getLatitude(),location.getLongitude());
        }else {
            llp = new LatLonPoint((double)31.2776, (double)121.189262);
        }
        return llp;
    }
}
