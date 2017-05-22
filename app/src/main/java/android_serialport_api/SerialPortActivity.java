package android_serialport_api;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.lt.nexthud2017.R;
import com.lt.nexthud2017.base.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/15.
 */

public abstract class SerialPortActivity extends FragmentActivity {

    protected android_serialport_api.Application mApplication;
    protected static SerialPort revSerialPort;
    protected static OutputStream mRevOutputStream;
    private static InputStream mRevInputStream;
    private static ReadThread mRevReadThread;

    protected static SerialPort sendSerialPort;
    protected static OutputStream mSendOutputStream;
    private static InputStream mSendInputStream;
    private static ReadThread2 mSendReadThread;

    protected static SerialPort obdSerialPort;
    protected static OutputStream obdOutputStream;
    private static InputStream obdInputStream;
    private static ReadThread3 obdReadThread;


    protected static SerialPort blueSerialPort;
    protected static OutputStream blueOutputStream;
    private static InputStream blueInputStream;
    private static ReadThread4 blueReadThread;
    protected boolean isInit=false;
    /////////////////////////////////////////////////////////////////

    private static String TAG = "nexthud";
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 缓存
    private SharedPreferences mSharedPreferences;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地词典
    private String mLocalLexicon = null;
    // 云端语法文件
    private String mCloudGrammar = null;
    // 本地语法构建路径
    private String grmPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/msc/test";
    // 返回结果格式，支持：xml,json
    private String mResultType = "json";

    private  final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
    private  final String GRAMMAR_TYPE_ABNF = "abnf";

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private Toast mToast;
    ////////////////////////////////////////////////

    Timer baseTimer;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();

            while(true) {
                int size;
                try {
                    byte[] buffer = new byte[512];
                    if (mRevInputStream == null) continue;
                    size = mRevInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
//                    com.lt.nexthud.MainActivity.normal.DBG(e.toString());
                    continue;
                }

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    private class ReadThread2 extends Thread {

        @Override
        public void run() {
            super.run();

            while(true) {
                int size;
                try {
                    byte[] buffer = new byte[512];
                    if (mSendInputStream == null) continue;
                    size = mSendInputStream.read(buffer);
                    if (size > 0) {
                        onSendDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    private class ReadThread3 extends Thread {

        @Override
        public void run() {
            super.run();

            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[300];
                    if (obdInputStream == null) continue;
                    size = obdInputStream.read(buffer);
                    if (size > 0) {
                        onOBDDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    private class ReadThread4 extends Thread {

        @Override
        public void run() {
            super.run();

            while(true) {

                if (blueInputStream == null) continue;

                byte[] buffer = new byte[512];
                int size = 0;
                try {
                    size = blueInputStream.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (size > 0) {
                    onBLUEDataReceived(buffer, size);
                }

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }


    protected WakeUpCmd wake=null;
    public SoundPool soundPool;
    public int soundId=0;
    public int soundD_USwitchId=0;
    public int soundWXId=0;
    protected int soundErrorId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication =new android_serialport_api.Application();
        soundPool = new SoundPool(15,AudioManager.STREAM_MUSIC, 0);
        soundId=soundPool.load(this, R.raw.switcher, 1);
        soundErrorId=soundPool.load(this, R.raw.error, 1);
        soundD_USwitchId=soundPool.load(this, R.raw.kaka, 1);
        soundWXId=soundPool.load(this, R.raw.wx, 1);
        try {
            revSerialPort = mApplication.getRevSerialPort();
            mRevOutputStream = revSerialPort.getOutputStream();
            mRevInputStream = revSerialPort.getInputStream();


            sendSerialPort = mApplication.getSendSerialPort();
            mSendOutputStream=sendSerialPort.getOutputStream();
            mSendInputStream=sendSerialPort.getInputStream();

            obdSerialPort = mApplication.getOBDSerialPort();
            obdOutputStream=obdSerialPort.getOutputStream();
            obdInputStream=obdSerialPort.getInputStream();

            blueSerialPort = mApplication.getBlueSerialPort();
            blueOutputStream=blueSerialPort.getOutputStream();
            blueInputStream=blueSerialPort.getInputStream();

            //	if(blueSerialPort==null)
            //		Toast.makeText(SerialPortActivity.this,"blueSerialPort==null",Toast.LENGTH_LONG).show();

			/* Create a receiving thread */
            mRevReadThread = new ReadThread();
            mRevReadThread.start();

            mSendReadThread = new ReadThread2();
            mSendReadThread.start();

            obdReadThread = new ReadThread3();
            obdReadThread.start();

            blueReadThread = new ReadThread4();
            blueReadThread.start();
        } catch (SecurityException e) {
            Log.e("android_serialport_api", e.getStackTrace().toString());
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            Log.e("android_serialport_api", e.getStackTrace().toString());
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            Log.e("android_serialport_api", e.getStackTrace().toString());
            //DisplayError(R.string.error_configuration);
        }

        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);


        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());


        //Create();

        //initMsc();
        //initASR();
        baseTimer = new Timer(true);
        baseTimer.schedule(baseTask,2000, 1000);
    }


    public void initWake()
    {
        wake=new WakeUpCmd();
        wake.Create(this,mWakeuperListener);
    }



    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.e("phone", result.getResultString());
            //String resultString=null;

            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                int score=Integer.parseInt(object.optString("score"));
                if(score>4)
                {
                    soundPool.play(soundId, (float)15.0, (float)15.0, 1, 0, (float)1.0);

                    wake.Stop();
                    if (!setParam()) {
                        showTip("请先构建语法。");
                        Log.e("asr","请先构建语法。");
                        return;
                    };

                    int ret = mAsr.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("识别失败,错误码1111: " + ret);
                        Log.e("asr","识别失败,错误码1111: " + ret);
                    }

                }
            } catch (JSONException e) {
                //resultString = "结果解析出错";
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {

        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };



    TimerTask baseTask = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            baseHandler.sendMessage(message);
        }
    };

    static int myCount=0;
    static boolean isBuildSuccess=false;
    final Handler baseHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    myCount++;
                    if(isInit && myCount %7==0 ){
                        if(buildGrammar!=0 && mAsr!=null)
                        {
                            buildGrammar = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, grammarListener);
                            if(buildGrammar != ErrorCode.SUCCESS)
                                showTip("语法构建失败,错误码：" + buildGrammar);
                        }
                    }

                    onTimer();
                    break;
            }
        }
    };


    protected abstract void onTimer();

    protected abstract void onCall(String cmdId);

    String mContent;// 语法、词典临时变量
    int buildGrammar=-1;
    protected void initASR()
    {
        mSharedPreferences = getSharedPreferences(getPackageName(),	MODE_PRIVATE);

        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化语法、命令词
        mCloudGrammar = FucUtil.readFile(this,"wake_grammar.abnf","utf-8");
        //mLocalGrammar = FucUtil.readFile(this,"call.bnf", "utf-8");
        mContent = new String(mCloudGrammar);
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置文本编码格式
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        //使用8k音频的时候请解开注释
//		mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        buildGrammar = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, grammarListener);
        if(buildGrammar != ErrorCode.SUCCESS){
            showTip("语法构建失败,错误码444：" + buildGrammar);
            Log.e("asr","语法构建失败,错误码444：" + buildGrammar);
        }
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码444："+code);
                Log.e("asr","初始化失败,错误码444：" + code);
            }
        }
    };

    /**
     * 更新词典监听器。
     */
    private LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if(error == null){
                showTip("词典更新成功");
            }else{
                showTip("词典更新失败,错误码333："+error.getErrorCode());
            }
        }
    };

    /**
     * 构建语法监听器。
     */
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if(error == null){
                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    if(!TextUtils.isEmpty(grammarId))
                        editor.putString(KEY_GRAMMAR_ABNF_ID, grammarId);
                    editor.commit();
                }
                showTip("语音识别启动123");
            }else{
                buildGrammar=-1;
                showTip("语法构建失败,错误码222：" + error.getErrorCode());
                Log.e("asr","语法构建失败,错误码222：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //if(volume>5)
            //	showTip("当前正在说话，音量大小：" + volume);
            //Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.e(TAG, "recognizer result：" + result.getResultString());

                //showTip(result.getResultString());

                if (mResultType.equals("json")) {
                    ArrayList<VoiceCommand> array = JsonParser.parseGrammar(result.getResultString(), mEngineType);
                    if(array.size()>0)
                    {
                        int score=0;
                        VoiceCommand _cmd=null;
                        for(int i=0;i<array.size();i++){
                            VoiceCommand cmd=array.get(i);
                            if(score<cmd.Score)
                            {
                                score=cmd.Score;
                                _cmd=cmd;
                            }
                        }

                        if(score>29){
                            //showTip("当前正在说话，音量大小：" + score);
                            onCall(_cmd.Command);
                        }
                        else{
                            soundPool.play(soundErrorId, (float)15.0, (float)15.0, 1, 0, (float)1.0);
                        }
                    }

                } else if (mResultType.equals("xml")) {
                    //text = XmlParser.parseNluResult(result.getResultString());
                }
                mAsr.stopListening();
                wake.Start();
                //showTip(text);
                // 显示
                //((EditText) findViewById(R.id.isr_text)).setText(text);
            } else {
                Log.e("asr", "recognizer result : null");
            }

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            //showTip("onError Code："	+ error.getErrorCode());
            Log.e("asr","onError Code：" + error.getErrorCode());
            if( error.getErrorCode()==10119){
                soundPool.play(soundErrorId, (float)15.0, (float)15.0, 1, 0, (float)1.0);
            }
            mAsr.stopListening();
            wake.Start();
            //mAsr.startListening(mRecognizerListener);
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
     * @param
     * @return
     */
    public boolean setParam(){
        boolean result = false;
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        mAsr.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "7500");
        ////timeout=50000
        // 设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "2");
        //	mAsr.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        if("cloud".equalsIgnoreCase(mEngineType))
        {
            String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
            if(TextUtils.isEmpty(grammarId))
            {
                result =  false;
            }else {
                // 设置返回结果格式
                mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
                // 设置云端识别使用的语法id
                mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                result =  true;
            }
        }
        else
        {
            // 设置本地识别资源
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
            // 设置返回结果格式
            mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
            // 设置本地识别使用语法id
            mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");


            // 设置识别的门限值
            mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "2");

            // 使用8k音频的时候请解开注释
//			mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            result = true;
        }

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");
        return result;
    }

    public void onDestroyMasr() {
        mAsr.stopListening();
        mAsr.cancel();
        mAsr.destroy();
    }

    //获取识别资源路径
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        //识别8k资源-使用8k的时候请解开注释
//		tempBuffer.append(";");
//		tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }


    protected abstract void onDataReceived(final byte[] buffer, final int size);

    protected abstract void onSendDataReceived(final byte[] buffer, final int size);

    protected abstract void onOBDDataReceived(final byte[] buffer, final int size);

    protected abstract void onBLUEDataReceived(final byte[] buffer, final int size);

    @Override
    protected void onDestroy() {
        if (mRevReadThread != null)
            mRevReadThread.interrupt();

        if (mSendReadThread != null)
            mSendReadThread.interrupt();

        if (obdReadThread != null)
            obdReadThread.interrupt();

        if (blueReadThread != null)
            blueReadThread.interrupt();

        mApplication.closeSerialPort();
        revSerialPort = null;
        sendSerialPort=null;
        obdSerialPort=null;
        blueSerialPort=null;
        super.onDestroy();
    }
}
