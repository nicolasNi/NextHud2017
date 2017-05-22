package com.lt.nexthud2017;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.lt.nexthud2017.music.MusicFragment;
import com.lt.nexthud2017.music.MusicViewModel;
import com.lt.nexthud2017.music.util.MusicService;

import java.nio.charset.Charset;
import java.util.ArrayList;

import android_serialport_api.SerialPortActivity;

public class MainActivity extends SerialPortActivity {
    public static MainActivity mainContext;
    public static MusicFragment music;
    private MusicViewModel musicViewModel;

    public static Boolean isMusicPlayedBoolean = false;

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
        FragmentManager fm = getSupportFragmentManager();
        music = (MusicFragment) fm.findFragmentById(R.id.musicFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(music);
        ft.commit();

        musicViewModel = new MusicViewModel(this);
        musicViewModel.setMusicFragment(music);
        music.setMusicViewModel(musicViewModel);

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
                        music.changeAction(bleMsgStr);
                    }
                }
                catch (Exception ex){

                }
            }
        });
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
