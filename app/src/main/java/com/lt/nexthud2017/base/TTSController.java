package com.lt.nexthud2017.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * Created by Administrator on 2017/6/6.
 */

public class TTSController implements AMapNaviListener {
    private static String TAG = TTSController.class.getSimpleName();

    public static TTSController ttsManager;
    private Context mContext;

    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认云端发音人
    public static String voicerCloud="xiaoyan";
    // 默认本地发音人
    public static String voicerLocal="xiaoyan";

    private boolean isPause=false;

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;


    public void init() {

        // String text = mContext.getString(R.string.app_id);
        // SpeechUtility.createUtility(mContext, "appid=" + text);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);

        mEngineType = SpeechConstant.TYPE_CLOUD;

        setParam();
    }

    public void playText(String playText) {
        // setParam();
        Log.e("tts", "playText："+isPause);
        Log.e("tts", "playText："+playText);

        if(isPause){
            return;
        }

        if(!mTts.isSpeaking())
            mTts.startSpeaking(playText, mTtsListener);
        //int aaa=code;
    }

    public void playTipText(String playText) {
        Log.e("tts", "playText："+playText);

        if(!mTts.isSpeaking())
            mTts.startSpeaking(playText, mTtsListener);
    }


    public void stopSpeaking() {
        isPause=true;
        Log.e("tts", "stopSpeaking");
        if (mTts != null)
            mTts.stopSpeaking();
    }

    public void startSpeaking() {
        Log.e("tts", "startSpeaking");
        isPause = false;
    }


    TTSController(Context context) {
        mContext = context;
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
            ttsManager.init();
        }
        return ttsManager;
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                //showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            //showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            //showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {

            } else if (error != null) {

            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };



    /**
     * 参数设置
     * @param param
     * @return
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD))
        {
            //设置使用云端引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);
        }else {
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
        }



        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED,  "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH,"50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME,"100");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,  "3");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    //获取发音人资源路径
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/"+TTSController.voicerLocal+".jet"));
        return tempBuffer.toString();
    }


    @Override
    public void onArriveDestination() {

        Log.e("hud", "onArriveDestination1");
        // TODO Auto-generated method stub d
    }

    @Override
    public void onArrivedWayPoint(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
    }

    @Override
    public void onCalculateRouteSuccess() {
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        // TODO Auto-generated method stub
        this.playText(arg1);
    }

    @Override
    public void onInitNaviFailure() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviSuccess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onReCalculateRouteForYaw() {
    }

    @Override
    public void onStartNavi(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTrafficStatusUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    @Deprecated
    public void onArriveDestination(NaviStaticInfo arg0) {
        // TODO Auto-generated method stub
        Log.e("hud", "onArriveDestination3");
    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo arg0) {
        // TODO Auto-generated method stub
        Log.e("hud", "onArriveDestination2");

    }

}

