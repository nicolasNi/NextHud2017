package com.lt.nexthud2017.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lt.nexthud2017.R;

/**
 * Created by Administrator on 2017/5/17.
 */

public class AudioDialog  extends Dialog {

    public AudioDialog(Context context) {
        super(context);
    }

    public AudioDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void cancel()
    {
        Builder.Recoder().stopRecord();
        super.cancel();

    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private View contentView;
        static RecodeManager recodeManager = null;

        public Builder(Context context) {
            this.context = context;
        }

        public static RecodeManager Recoder() {
            return recodeManager;
        }


        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        private ImageView progress;    //显示录音的振幅

        private Drawable[] progressImg = new Drawable[7];//显示录音振幅图片缓存

        public AudioDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme


            if (recodeManager == null) {
                recodeManager = new RecodeManager();
            }

            progressImg[0] = context.getResources().getDrawable(R.drawable.mic_1);
            progressImg[1] = context.getResources().getDrawable(R.drawable.mic_2);
            progressImg[2] = context.getResources().getDrawable(R.drawable.mic_3);
            progressImg[3] = context.getResources().getDrawable(R.drawable.mic_4);
            progressImg[4] = context.getResources().getDrawable(R.drawable.mic_5);
            progressImg[5] = context.getResources().getDrawable(R.drawable.mic_6);
            progressImg[6] = context.getResources().getDrawable(R.drawable.mic_7);

            final AudioDialog dialog = new AudioDialog(context, R.style.audioDialog);
            View layout = inflater.inflate(R.layout.dialog_sound, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            progress = (ImageView) layout.findViewById(R.id.sound_progress);
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(
                        contentView, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT));
            }

            try {
                recodeManager.setSoundAmplitudeListen(onSoundAmplitudeListen);
                recodeManager.startRecordCreateFile();
            } catch (Exception e) {
                Log.e("audio", e.toString());
                dialog.cancel();
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dialog.setContentView(layout);
            return dialog;
        }

        private RecodeManager.SoundAmplitudeListen onSoundAmplitudeListen = new RecodeManager.SoundAmplitudeListen() {

            @SuppressWarnings("deprecation")
            @Override
            public void amplitude(int amplitude, int db, int value) {
                // TODO Auto-generated method stub
                if (value >= 6) {
                    value = 6;
                }
                try {
                    progress.setBackgroundDrawable(progressImg[value]);
                } catch (Exception e) {
                    Log.e("audio", e.toString());
                }
            }
        };


    }
}
