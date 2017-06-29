package com.lt.nexthud2017;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.lt.nexthud2017.address.AddressFragment;
import com.lt.nexthud2017.address.AddressViewModel;
import com.lt.nexthud2017.hud.HudFragment;
import com.lt.nexthud2017.normal.NormalFragment;
import com.lt.nexthud2017.base.FragmentsAdapter;
import com.lt.nexthud2017.base.FunctionNavigation;
import com.lt.nexthud2017.music.MusicFragment;
import com.lt.nexthud2017.music.MusicViewModel;
import com.lt.nexthud2017.weixin.WeiXinFragment;
import com.lt.nexthud2017.weixin.WeiXinViewModel;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPortActivity;

public class MainActivity extends SerialPortActivity {
    public static MainActivity mainContext;
    public static MusicFragment music;
    public BlankFragment testFragment;
    public BlankFragment2 testFragment2;
    public BlankFragment3 testFragment3;
    public BlankFragment4 testFragment4;
    public NormalFragment normalFragment;
    public AddressFragment addressFragment;
    public static HudFragment hudFragment;
    public WeiXinFragment weiXinFragment;

    private MusicViewModel musicViewModel;
    private AddressViewModel addressViewModel;
    private WeiXinViewModel weiXinViewModel;

    public static Boolean isMusicPlayedBoolean = false;
    private static ViewPager  viewPager;
    private static List<Fragment> fragmentList = new ArrayList<>();
    private static FragmentsAdapter fragmentsAdapter;

    private FunctionNavigation navigation;
    private static FragmentManager fm;
    public static String DeviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialSpeech();

        mainContext = this;
        fm = getSupportFragmentManager();
//        music = (MusicFragment) fm.findFragmentById(R.id.musicFragment);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.show(music);
//        ft.commit();

        music = new MusicFragment();
        testFragment = new BlankFragment();
        testFragment2 = new BlankFragment2();
        testFragment3 = new BlankFragment3();
        testFragment4 = new BlankFragment4();
        normalFragment = new NormalFragment();
        addressFragment = new AddressFragment();
        hudFragment = new HudFragment();
        weiXinFragment = new WeiXinFragment();
        fragmentList.add(normalFragment);
        fragmentList.add(addressFragment);
        fragmentList.add(weiXinFragment);
        fragmentList.add(music);
        fragmentList.add(testFragment4);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        rebuildFragmentsAdapterAndSetAdapter();
        navigation = (FunctionNavigation)findViewById(R.id.navigation);

    }

    private static void rebuildFragmentsAdapterAndSetAdapter(){
        fragmentsAdapter =new  FragmentsAdapter(fm,fragmentList);
        viewPager.setAdapter(fragmentsAdapter);
    }
    public static double Latitude = 0;
    public static String Speed = "0";
    public static double Longitude = 0;

    @Override
    protected void onStart() {
        super.onStart();
        musicViewModel = new MusicViewModel(this);
        musicViewModel.setMusicFragment(music);

        addressViewModel = new AddressViewModel(this);
        addressViewModel.setAddressFragment(addressFragment);

        weiXinViewModel = new WeiXinViewModel(this);
        weiXinViewModel.setWeiXinFragment(weiXinFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        music.setMusicViewModel(musicViewModel);
        addressFragment.setAddressViewModel(addressViewModel);
        weiXinFragment.setWeiXinViewModel(weiXinViewModel);
    }

    private void initialSpeech(){
        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MainActivity.this, param.toString());
    }

    public static byte[] getMsg(ArrayList<Byte> messages) {
        int size = messages.size();
        byte[] newbuf = new byte[size];
        synchronized (lock1) {
            for (int i = 0; i < size; i++) {
                newbuf[i] = messages.get(i).byteValue();
            }
        }
        return newbuf;
    }

    public static void clearMsg() {
        synchronized (lock1) {
            messageList.clear();
        }
    }

    private static ArrayList<Byte> messageList = new ArrayList<Byte>();
    public static final Object lock1 = new Object();

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{

                    synchronized (lock1){
                        for(int i = 0; i<size;i++){
                            messageList.add(buffer[i]);
                        }
                    }
                    byte[] _msg = getMsg(messageList);
                    String bleMsgStr = new String(_msg, Charset.forName("UTF-8"));
                    if(bleMsgStr.contains("36-")){
                        clearMsg();
                        if (bleMsgStr.contains("right")) {
                            navigation.pressNextButton();
                        }else if (bleMsgStr.contains("center") && navigation.isDisplayed) {
                            if(fragmentList.size()>5){
                                viewPager.setCurrentItem(5);

                            }else {
                                viewPager.setCurrentItem(navigation.currentIndex);
                                startToDisplayWeiXinFragment();
                                navigation.pressCenterButton();
                            }
                        }else {
                            handleChangingActionForEachFragmentPage(navigation.currentIndex,bleMsgStr);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void startToDisplayWeiXinFragment(){
        if(navigation.currentIndex == 2){
            weiXinFragment.showWeiXinFragment();
        }
    }

    public static void addHudFragment(){
        fragmentList.add(hudFragment);
        rebuildFragmentsAdapterAndSetAdapter();
        if(getHudFragmentIndex()>=0){
            viewPager.setCurrentItem(getHudFragmentIndex());
        }
    }

    private static int getHudFragmentIndex(){
        int index = fragmentList.indexOf(hudFragment);
        return index;
    }

    private void handleChangingActionForEachFragmentPage(int index, String bleMsgStr){
        switch (index){
            case 0:
//                music.changeAction(bleMsgStr);
                break;
            case 1:
                addressFragment.changeAction(bleMsgStr);
                break;
            case 2:
//                music.changeAction(bleMsgStr);
                break;
            case 3:
                music.changeAction(bleMsgStr);
                break;
            case 4:
//                music.changeAction(bleMsgStr);
                break;

        }
    }

    @Override
    protected void onCall(String cmdId) {

    }

    @Override
    protected void onBLUEDataReceived(byte[] buffer, int size) {

    }

    @Override
    protected void onTimer() {

    }

    @Override
    protected void onSendDataReceived(byte[] buffer, int size) {

    }

    @Override
    protected void onOBDDataReceived(byte[] buffer, int size) {

    }
}
