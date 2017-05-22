package android_serialport_api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.lt.nexthud2017.R;

/**
 * Created by Administrator on 2017/5/16.
 */

public class WakeUpCmd {
    private String TAG = "ivw";
    private Toast mToast;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private int curThresh = -10;
    private String keep_alive = "1";
    private String ivwNetMode = "0";
    WakeuperListener listener;
    Activity myContext=null;
    public void Create(Activity context,WakeuperListener listener)
    {
        myContext=context;
        mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.listener=listener;
        init(context);
    }

    void init(Context context)
    {
        mIvw = VoiceWakeuper.createWakeuper(context.getApplicationContext(), null);

        /**
         * 闭环优化网络模式有三种：
         * 模式0：关闭闭环优化功能
         *
         * 模式1：开启闭环优化功能，允许上传优化数据。需开发者自行管理优化资源。
         * sdk提供相应的查询和下载接口，请开发者参考API文档，具体使用请参考本示例
         * queryResource及downloadResource方法；
         *
         * 模式2：开启闭环优化功能，允许上传优化数据及启动唤醒时进行资源查询下载；
         * 本示例为方便开发者使用仅展示模式0和模式2；
         */
        ivwNetMode = "0";
        mIvw = VoiceWakeuper.getWakeuper();
        if(mIvw != null) {
            resultString = "";

            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+ curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);

            mIvw.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "1000");

            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());

            //mIvw.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

            // 启动唤醒
            mIvw.startListening(listener);

            showTip("语音唤醒启动");
        } else {
            showTip("唤醒未初始化");
        }
    }

    public void Stop()
    {
        mIvw.stopListening();
    }

    public void Start()
    {
        init(myContext);
        //mIvw.startListening(listener);
    }



    private String getResource() {

        String fileName=myContext.getString(R.string.app_id);

        String path= ResourceUtil.generateResourcePath(myContext.getBaseContext(),
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/"+fileName+".jet");

        return path;
    }




    public void onDestroy() {
        Log.e(TAG, "onDestroy WakeDemo");
        // 销毁合成对象
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
    }

    private void showTip(final String str) {
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }


}