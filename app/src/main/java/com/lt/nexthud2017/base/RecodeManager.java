package com.lt.nexthud2017.base;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017/5/17.
 */

public class RecodeManager {

    private File file;//录音文件
    private MediaRecorder mediaRecorder;//android 媒体录音类
    private SoundAmplitudeListen soundAmplitudeListen;//声波振幅监听器
    private final Handler mHandler=new Handler();
    private Runnable mUpdateMicStatusTimer=new Runnable() {
        /**
         * 分贝的计算公式K=20lg(Vo/Vi) Vo当前的振幅值,Vi基准值为600
         */
        private int BASE=500;
        private int RATIO=5;
        private int postDelayed=200;
        @Override
        public void run() {
            int ratio=mediaRecorder.getMaxAmplitude()/BASE;
            int db=(int)(20*Math.log10(Math.abs(ratio)));
            int value=db/RATIO;
            if(value<0) value=0;
            if(soundAmplitudeListen!=null){
                soundAmplitudeListen.amplitude(ratio, db, value);
                mHandler.postDelayed(mUpdateMicStatusTimer,postDelayed);
            }
        }
    };

    public void startRecordCreateFile() throws IOException {
        if(!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            return ;
        }
        file=new File(Environment.getExternalStorageDirectory()+File.separator+"1"+File.separator+
                new DateFormat().format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA))+".amr");
        mediaRecorder=new MediaRecorder();//创建录音对象
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//从麦克风源进行录音
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//设置输出格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置编码格式
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        //创建文件
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        mediaRecorder.prepare();
        mediaRecorder.start();
        mHandler.post(mUpdateMicStatusTimer);
    }

    public File stopRecord(){
        if(mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder=null;
            mHandler.removeCallbacks(mUpdateMicStatusTimer);
        }
        return file;
    }
    public void setSoundAmplitudeListen(SoundAmplitudeListen soundAmplitudeListen){
        this.soundAmplitudeListen=soundAmplitudeListen;
    }
    public interface SoundAmplitudeListen{
        public void amplitude(int amplitude,int db,int value);
    }
}
