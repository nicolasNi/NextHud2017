package com.lt.nexthud2017.normal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


/**
 * Created by Administrator on 2017/5/31.
 */

public class NormalFragment extends Fragment implements AMapLocationListener {
    ImageView hud_roadImage;
    TextView hud_currentTime;
    TextView hud_time;
    TextView hud_time1;
    public ImageView hud_signal;
    TextView hud_time_wx;
    TextView hud_time1_wx;
    ImageView hud_roadImage_wx;
    TextView dbg;
    ImageView hud_msg_logo;
    public static TextView hud_msg_title;
    public static TextView hud_msg_context;
    ImageView hud_msg_line;

    ImageView hud_msg_left;
    TextView hud_msg_left_txt;
    TextView hud_msg_id;
    TextView hud_msg_right_txt;
    ImageView hud_msg_right;
    public static TextView hud_msg_type;
    public static TextView hud_msg_location;

    View normalView;
    Timer timer;
    public LinearLayout loading;
    public ImageView phone;
    public ImageView gps;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    static boolean isShowing = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        normalView = inflater.inflate(R.layout.activity_normal, container, false);
        initView();
        initAMapLocation();
        return normalView;
    }

    void initView() {
        dbg = (TextView) normalView.findViewById(R.id.dbg);
        hud_roadImage = (ImageView) normalView.findViewById(R.id.hud_roadImage_nor);
        hud_currentTime = (TextView) normalView.findViewById(R.id.hud_currentTime_nor);
        hud_time = (TextView) normalView.findViewById(R.id.hud_time_nor);
        hud_time1 = (TextView) normalView.findViewById(R.id.hud_time1_nor);
        hud_msg_line = (ImageView) normalView.findViewById(R.id.hud_msg_line);
        hud_signal = (ImageView) normalView.findViewById(R.id.signal);
        hud_time_wx = (TextView) normalView.findViewById(R.id.hud_time_wx);
        hud_time1_wx = (TextView) normalView.findViewById(R.id.hud_time1_wx);
        hud_roadImage_wx = (ImageView) normalView.findViewById(R.id.hud_roadImage_wx);

        hud_msg_logo = (ImageView) normalView.findViewById(R.id.hud_msg_logo);
        hud_msg_title = (TextView) normalView.findViewById(R.id.hud_msg_title);
        hud_msg_context = (TextView) normalView.findViewById(R.id.hud_msg_context);
        gps = (ImageView) normalView.findViewById(R.id.gps);
        hud_msg_left_txt = (TextView) normalView.findViewById(R.id.hud_msg_left_txt);
        hud_msg_right_txt = (TextView) normalView.findViewById(R.id.hud_msg_right_txt);
        hud_msg_left = (ImageView) normalView.findViewById(R.id.hud_msg_left);
        hud_msg_right = (ImageView) normalView.findViewById(R.id.hud_msg_right);
        hud_msg_type = (TextView) normalView.findViewById(R.id.hud_msg_type);
        hud_msg_id = (TextView) normalView.findViewById(R.id.hud_msg_id);
        hud_msg_location = (TextView) normalView.findViewById(R.id.hud_msg_location);
        phone = (ImageView) normalView.findViewById(R.id.phone);
        loading = (LinearLayout) normalView.findViewById(R.id.normal_wait);

        Bitmap bmpLine = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        hud_msg_line.setImageBitmap(bmpLine);

        Bitmap left = BitmapFactory.decodeResource(getResources(), R.drawable.left);
        hud_msg_left.setImageBitmap(left);

        Bitmap right = BitmapFactory.decodeResource(getResources(), R.drawable.right);
        hud_msg_right.setImageBitmap(right);

        hud_msg_left_txt.setText("向左忽略");
        hud_msg_right_txt.setText("向右回复");

        Bitmap tbmp = Bitmap.createBitmap(260, 180, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tbmp);
        Matrix orig = canvas.getMatrix();
        orig.setScale(-1, 1);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.speed_panel);
        Bitmap _convertBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), orig, true);
        hud_roadImage.setImageBitmap(_convertBmp);
        hud_roadImage_wx.setImageBitmap(_convertBmp);
        hud_currentTime.setText("--:--");
        hud_time.setText(" 0");
        hud_time_wx.setText(" 0");

        timer = new Timer(true);
//        timer.schedule(task, 2000, 1000);

    }

    void initAMapLocation() {
        locationClient = new AMapLocationClient((Context) this.getActivity().getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationOption.setInterval(1000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        locationClient.startLocation();
    }

    boolean isGpsSuccess = false;
    static boolean isSetBri=false;

    void setBri(int date)
    {
        if(!isSetBri){
            if(date>=18 || date<=8)
            {
//                Brightness.sendBrightness("20-a");
            }
            else
            {
//                Brightness.sendBrightness("20-s");
            }
            isSetBri=true;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        // TODO Auto-generated method stub
        if (amapLocation != null) {
            int errorCode = amapLocation.getErrorCode();
            if (errorCode == 0) {
                // 定位成功回调信息，设置相关消息
                // public static final int LOCATION_TYPE_GPS = 1;

                int LOCATION_TYPE = amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
                if (LOCATION_TYPE == 1) {
                    // hud_currentTime.setText("GPS");
                    isGpsSuccess = true;
                    gps.setVisibility(View.VISIBLE);

                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    Date date = new Date(amapLocation.getTime());
                    hud_currentTime.setText(df.format(date));

                    SimpleDateFormat df2 = new SimpleDateFormat("HH");
                    int date2=Integer.parseInt(df2.format(date));
                    setBri(date2);

                } else {
                    gps.setVisibility(View.GONE);
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    Date date = new Date(amapLocation.getTime());
                    hud_currentTime.setText(df.format(date));

                    SimpleDateFormat df2 = new SimpleDateFormat("HH");
                    int date2=Integer.parseInt(df2.format(date));
                    setBri(date2);
                }
                MainActivity.Latitude = amapLocation.getLatitude();// 获取纬度
                MainActivity.Longitude = amapLocation.getLongitude();// 获取经度
            } else {

                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError",
                        "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                locationClient.startLocation();
            }
        }
    }
}
