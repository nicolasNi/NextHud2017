package com.lt.nexthud2017.base;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;
import com.lt.nexthud2017.MainActivity;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/5/17.
 */

public abstract class BaseViewModel extends BaseObservable {
    private static String TAG = "IatDemo";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 听写结果内容
    private EditText mResultText;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public BaseViewModel(){
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        if(mIat==null) {
            mIat = SpeechRecognizer.createRecognizer(MainActivity.mainContext, mInitListener);
        }

        setParam();
    }

    public void startIat(){
        String s = "dff";
        Log.d("log===========",s);



        mIat.startListening(mRecognizerListener);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "Error SpeechRecognizer init() code = " + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, "recognizer result：" + results.getResultString());
            String text = JsonParser.parseIatResult(results.getResultString());
            appendText(text,isLast);
            if (isLast) {
//                isIat=false;
//                recordDialog.cancel();
            }
            //mResultText.setSelection(mResultText.length());
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            String msg=error.getPlainDescription(true);
            Log.e("music",msg);
            //mIatDialog.dismiss();
        }
    };

    /**
     * 参数设置
     * @param
     * @return
     */
    public void setParam(){
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        String lag ="zh_cn";
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT,lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "6000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    protected abstract void appendText(String text,boolean isLast);


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            Log.d("mRecognizerListener","onBeginOfSpeech");
        }

        @Override
        public void onError(SpeechError error) {
            Log.d("mRecognizerListener","onError");

        }

        @Override
        public void onEndOfSpeech() {
            Log.d("mRecognizerListener","onError");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());

            Log.d("mRecognizerListener","onResult:"+text);
            appendText(text,isLast);
            if(isLast) {
                //TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

            Log.d("mRecognizerListener","onVolumeChanged"+volume);
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

}
