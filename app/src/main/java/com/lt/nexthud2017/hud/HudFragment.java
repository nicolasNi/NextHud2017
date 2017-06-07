package com.lt.nexthud2017.hud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.BroadcastMode;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.DriveWayView;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.lt.nexthud2017.MainActivity;
import com.lt.nexthud2017.R;
import com.lt.nexthud2017.base.TTSController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/5.
 */

public class HudFragment extends Fragment implements AMapNaviListener {
    private View view;
    public AMapNavi mAMapNavi;

    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    ImageView hud_roadImage;
    ImageView hud_nextImage;
    ImageView hud_destImage;
    TextView hud_roadName;
    TextView hud_residualDistance;
    TextView hud_nextRoadName;
    TextView hud_distance;
    TextView hud_time;
    TextView hud_speed;
    ImageView hud_cross;
    DriveWayView hud_roadPane;

    String hud_nextRoadName_x;

    private int NavMode = 1;

    Timer timer;

    int imageId;
    int smallImageId;
    String roadName;
    String nextRoadName;
    int cameraSpeed;
    String distance;
    String time;
    String residualDistance;

    ImageView hud_msg_logo;
    TextView hud_msg_title;
    public TextView hud_msg_context;
    ImageView hud_msg_line;

    ImageView hud_msg_left;
    TextView hud_msg_left_txt;
    TextView hud_msg_right_txt;
    ImageView hud_msg_right;
    public TextView hud_msg_type;
    public TextView hud_msg_location;

    TextView hud_roadName_msg;
    ImageView hud_cameraImage;
    TextView hud_msg_id;
    Context context;
    static int switchImg = 0;
    int color;
    public TTSController mTtsManager;
    static boolean isShowing = false;
    public static boolean isShowDialog = false;


    public static int showMsgTimes = 0;
    public static boolean isShowFinished = false;
    static Bitmap road1 = null;
    static Bitmap road2 = null;
    static Bitmap road3 = null;
    static Bitmap road4 = null;
    static Bitmap road5 = null;

    boolean naving = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_hud, container, false);
        initView();
        return view;
    }

    private void initView() {
        hud_roadImage = (ImageView) view.findViewById(R.id.hud_roadImage);
        hud_nextImage = (ImageView) view.findViewById(R.id.hud_nextImage);
        hud_destImage = (ImageView) view.findViewById(R.id.hud_destImage);
        hud_roadName = (TextView)view.findViewById(R.id.hud_roadName);
        hud_residualDistance = (TextView) view.findViewById(
                R.id.hud_residualDistance);
        hud_nextRoadName = (TextView) view.findViewById(
                R.id.hud_nextRoadName);
        hud_distance = (TextView) view.findViewById(R.id.hud_distance);
        hud_time = (TextView) view.findViewById(R.id.hud_time);
        hud_cameraImage = (ImageView) view.findViewById(
                R.id.hud_cameraImage);
        hud_roadPane = (DriveWayView) view.findViewById(R.id.hud_roadPane);

        hud_roadName.setText("路线计算中。。。");
        hud_cross = (ImageView) view.findViewById(R.id.hud_cross);

        hud_msg_id = (TextView) view.findViewById(R.id.hud_msg_id);
        hud_msg_logo = (ImageView) view.findViewById(R.id.hud_msg_logo);
        hud_msg_title = (TextView) view.findViewById(R.id.hud_msg_title);
        hud_msg_context = (TextView) view.findViewById(R.id.hud_msg_context);
        hud_msg_left_txt = (TextView) view.findViewById(R.id.hud_msg_left_txt);
        hud_msg_right_txt = (TextView) view.findViewById(R.id.hud_msg_right_txt);
        hud_msg_left = (ImageView) view.findViewById(R.id.hud_msg_left);
        hud_msg_right = (ImageView) view.findViewById(R.id.hud_msg_right);
        hud_msg_type = (TextView) view.findViewById(R.id.hud_msg_type);
        hud_msg_location = (TextView) view.findViewById(R.id.hud_msg_location);
        hud_msg_line = (ImageView) view.findViewById(R.id.hud_msg_line);
        hud_roadName_msg = (TextView) view.findViewById(R.id.hud_roadName_msg);

        hud_speed = (TextView) view.findViewById(R.id.hud_speed_txt);
        Bitmap bmpLine = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        hud_msg_line.setImageBitmap(bmpLine);

        Bitmap left = BitmapFactory.decodeResource(getResources(), R.drawable.left);
        hud_msg_left.setImageBitmap(left);

        Bitmap right = BitmapFactory.decodeResource(getResources(), R.drawable.right);
        hud_msg_right.setImageBitmap(right);

        hud_msg_left_txt.setText("忽略");
        hud_msg_right_txt.setText("回复");

        color = Color.argb(1, 245, 255, 250);

        timer = new Timer(true);
        timer.schedule(task, 120, 120);

        road1 = BitmapFactory.decodeResource(getResources(), R.drawable.road1);
        road2 = BitmapFactory.decodeResource(getResources(), R.drawable.road2);
        road3 = BitmapFactory.decodeResource(getResources(), R.drawable.road3);
        road4 = BitmapFactory.decodeResource(getResources(), R.drawable.road4);
        road5 = BitmapFactory.decodeResource(getResources(), R.drawable.road5);
    }

    public void caclRoute(String addressName, double startLatitude,
                          double startLongitude, double latitude, double longitude,
                          int navMode) {
        NavMode =  navMode;
        context = MainActivity.mainContext;
        mTtsManager = TTSController.getInstance(context);
        mTtsManager.init();
        mTtsManager.startSpeaking();
        mAMapNavi = AMapNavi.getInstance(context);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setReCalculateRouteForYaw(true);
        mAMapNavi.setBroadcastMode(BroadcastMode.DETAIL);
        mEndPoints.clear();
        mStartPoints.clear();
        if (latitude < 5) {
            mTtsManager.playText("经纬度错误");
        }

        if (navMode == 1) {
            NaviLatLng start = new NaviLatLng(startLatitude, startLongitude);
            mStartPoints.add(start);
            NaviLatLng end = new NaviLatLng(latitude, longitude);
            mEndPoints.add(end);
        } else {
            NaviLatLng start = new NaviLatLng(startLatitude, startLongitude);
            if (startLatitude < 1) {
                start = new NaviLatLng(39.989614, 116.481763);
            }
            mStartPoints.add(start);
            NaviLatLng end = new NaviLatLng(latitude, longitude);
            mEndPoints.add(end);
            mAMapNavi.setEmulatorNaviSpeed(500);
        }

        naving = true;

        mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null,
                PathPlanningStrategy.DRIVING_DEFAULT);
        isShowing = true;
    }

    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    int getSpeed(int speed) {
        if (speed == 0) {
            return 6;
        } else if (speed < 10) {
            return 10;
        } else if (10 < speed && speed < 20) {
            return 9;
        } else if (20 < speed && speed < 30) {
            return 8;
        } else if (30 < speed && speed < 40) {
            return 7;
        } else if (40 < speed && speed < 50) {
            return 6;
        } else if (50 < speed && speed < 60) {
            return 5;
        } else if (60 < speed && speed < 70) {
            return 4;
        } else if (70 < speed && speed < 80) {
            return 3;
        } else if (80 < speed && speed < 90) {
            return 2;
        } else {
            return 1;
        }
    }

    static int myCount = 0;
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!isShowing) {
                        return;
                    }
                    try {

                        int speed =5;
                        try
                        {
                            speed=Integer.parseInt(MainActivity.Speed);
                        }
                        catch(Exception ex1){

                        }
                        speed = getSpeed(speed);
                        if (speed > 0 && myCount % speed == 0) {
                            drawNormal();
                        }

                        drawNavMsg();
                        myCount++;
                        if (myCount % 10 == 0) {
                            hud_speed.setText(MainActivity.Speed + "km/h");
                            if (myCount > 0)
//                                GetPhoneMessage();
                            if (showMsgTimes > 0) {
                                showMsgTimes--;
                            } else {
                                isShowFinished = false;
                            }
//                            GetPhoneMessageView();
                        }
                    } catch (Exception ex) {
                        hud_roadName.setText(ex.getMessage());
                        Log.e("message", ex.getMessage());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void drawNormal() {
        if (imageId < 0)
            return;
        Bitmap bmp = Bitmap.createBitmap(250, 200, Bitmap.Config.ARGB_8888);
        float contrast = (float) 1;

        // 改变对比度
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        try {
            Canvas canvas = new Canvas(bmp);
            if (imageId == R.drawable.hud_turn_front) {
                if (showMsgTimes <= 0) {
                    hud_roadImage.setVisibility(View.VISIBLE);

                } else {
                    hud_roadImage.setVisibility(View.GONE);
                }
                if (switchImg == 0) {

                    // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                    canvas.drawBitmap(road1, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
                } else if (switchImg == 1) {

                    // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                    canvas.drawBitmap(road2, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
                } else if (switchImg == 2) {

                    // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                    canvas.drawBitmap(road3, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
                } else if (switchImg == 3) {

                    // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                    canvas.drawBitmap(road4, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
                } else if (switchImg == 4) {

                    // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                    canvas.drawBitmap(road5, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
//					hud_roadImage_msg.setImageBitmap(bmp);
                }

                switchImg++;
                if (switchImg > 4) {
                    switchImg = 0;
                }
            } else {
                if(imageId>0){
                    Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(),
                            imageId);
                    canvas.drawBitmap(srcBitmap, 0, 0, paint);
                    hud_roadImage.setImageBitmap(bmp);
                }
            }

            Bitmap bmp6 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            Canvas canvas6 = new Canvas(bmp6);
            Bitmap bmp3 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dest);
            canvas6.drawBitmap(bmp3, 0, 0, paint);
            hud_destImage.setImageBitmap(bmp6);

            if (smallImageId > -1) {
                hud_nextImage.setVisibility(View.VISIBLE);
                cMatrix.set(new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,// 改变对比度
                        0, 0, 1, 0, 0, 0, 0, 0, 1, 0 });
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
                Bitmap bmp5 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                Canvas canvas5 = new Canvas(bmp5);
                Bitmap bmp2 = BitmapFactory.decodeResource(getResources(),
                        smallImageId);
                if(bmp2!=null)
                    canvas5.drawBitmap(bmp2, 0, 0, paint);

                hud_nextImage.setImageBitmap(bmp5);
            } else {
                hud_nextImage.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            Log.e("hudmsg", "error:"+result);
        }
    }

    void drawNavMsg() {
        hud_roadName.setText(roadName);
        hud_roadName_msg.setText(roadName);
        hud_residualDistance.setText(residualDistance);
        hud_nextRoadName.setText(nextRoadName);
        hud_distance.setText(distance);
        hud_time.setText(time);
    }


    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        int direction = naviInfo.getIconType();
        roadName = naviInfo.m_CurRoadName;
        nextRoadName = naviInfo.m_NextRoadName;
        cameraSpeed = naviInfo.m_CameraSpeed;

		/*
		 * int cameraType = naviInfo.getCameraType(); cameraName = "";
		 * switch(cameraType) { case 0: cameraName = "SPEED"; break; case 1:
		 * cameraName = "SURVEILLANCE"; break; case 2: cameraName =
		 * "TRAFFICLIGHT"; break; case 3: cameraName = "BREAKRULE"; break; case
		 * 4: cameraName = "BUSWAY"; break; case 5: cameraName = "EMERGENCY";
		 * break; }
		 */
        if (cameraSpeed <= 1) {
            hud_cameraImage.setVisibility(View.GONE);
            //hud_cameraImage_msg.setVisibility(View.GONE);
        } else {
            if (showMsgTimes <= 0) {
                hud_cameraImage.setVisibility(View.VISIBLE);
                //hud_cameraImage_msg.setVisibility(View.GONE);
            } else {
                //hud_cameraImage_msg.setVisibility(View.VISIBLE);
                hud_cameraImage.setVisibility(View.GONE);
            }
        }

        int _distance = naviInfo.getCurStepRetainDistance();
        switch (direction) {
            case 0: // none
                break;
            case 1: // car
                break;
            case 2: // lef
                // imageId=R.drawable.hud_turn_left;
                smallImageId = R.drawable.hud_turn_left_small;
                break;
            case 3: // right
                // imageId=R.drawable.hud_turn_right;
                smallImageId = R.drawable.hud_turn_right_small;
                break;
            case 4: // 左前方
                // imageId=R.drawable.hud_turn_left_front;
                smallImageId = R.drawable.hud_turn_left_front_small;
                break;
            case 5: // 右前方
                // imageId=R.drawable.hud_turn_right_front;
                smallImageId = R.drawable.hud_turn_right_front_small;
                break;
            case 6: // 左后方
                // imageId=R.drawable.hud_turn_left_back;
                smallImageId = R.drawable.hud_turn_left_back_small;
                break;
            case 7: // 右后方
                // imageId=R.drawable.hud_turn_right_back;
                smallImageId = R.drawable.hud_turn_right_back_small;
                break;
            case 8: // 左转掉头
                // imageId=R.drawable.hud_turn_back;
                smallImageId = R.drawable.hud_turn_back_small;
                break;
            case 9: // 直行
                // switchImg=0;
                imageId = R.drawable.hud_turn_front;
                smallImageId = R.drawable.hud_turn_front_small;
                break;
            case 10: // 到达途经点
                // imageId=R.drawable.hud_turn_via;
                smallImageId = R.drawable.hud_turn_via_small;
                break;
            case 11: // 进入环岛
                imageId = R.drawable.hud_turn_ring;
                smallImageId = R.drawable.hud_turn_ring_small;
                break;
            case 12: // 驶出环岛
                imageId = R.drawable.hud_turn_ring_out;
                smallImageId = R.drawable.hud_turn_ring_out_small;
                break;
            case 13: // 到达服务区
                imageId = R.drawable.rg_route_search_restaurant;
                smallImageId = R.drawable.rg_route_search_restaurant_small;
                break;
            case 14: // 到达收费站
                imageId = R.drawable.hud_turn_tollgate;
                smallImageId = R.drawable.hud_turn_tollgate_small;
                break;
            case 15: // 到达目的地
                // imageId=R.drawable.transfer_remind_icon_finish;
                smallImageId = R.drawable.transfer_remind_icon_finish_small;
                break;
            case 16: // 进入隧道
                imageId = R.drawable.tunnel;
                smallImageId = R.drawable.zou16_small;
                break;

        }
        DecimalFormat df = new DecimalFormat("0.0");
        int retainDistance = naviInfo.getPathRetainDistance();
        if (retainDistance > 1000) {
            residualDistance = df.format(retainDistance / 1000.0) + "公里";
        } else {
            residualDistance = retainDistance + "米";
        }
        int time_ = naviInfo.getPathRetainTime();

        if (time_ > 60) {
            int _time = time_ / 60;
            if (_time > 60) {
                time = _time / 60 + "小时" + _time % 60 + "分钟";
            } else {
                time = _time + "分钟";
            }
        } else {
            time = time_ + "秒";
        }

        if (_distance > 1000) {
            distance = df.format(_distance / 1000.0) + "公里";
        } else {
            distance = _distance + "米";
        }

        if (_distance > 250) {
            imageId = R.drawable.hud_turn_front;
        } else if (_distance == 0) {
            smallImageId = -1;
        }
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onCalculateRouteSuccess() {
        if (NavMode == 1) {
            mAMapNavi.startNavi(NaviType.GPS);
        } else {
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }
}
