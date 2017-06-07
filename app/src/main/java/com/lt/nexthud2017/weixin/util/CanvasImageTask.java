package com.lt.nexthud2017.weixin.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lt.nexthud2017.MainActivity;

/**
 * Created by Administrator on 2017/6/7.
 */

public class CanvasImageTask extends AsyncTask<View, Void, Void> {
    private View mView;
    //private static HashMap<String, SoftReference<Bitmap>> imageCache;
    public CanvasImageTask( ) {
        //if(imageCache==null)
        //	imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    protected Void doInBackground(View... views) {
        final View view = views[0];
        if (view.getTag() != null) {
            String urlStr =view.getTag().toString();
            String url="https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetheadimg";
            String param=urlStr.replace("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetheadimg?", "");
            if(urlStr.contains("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgeticon")){
                url="https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgeticon";
                param=urlStr.replace("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgeticon?", "");
            }
	    		 /*
	            if (imageCache.containsKey(param)) {
	                SoftReference<Bitmap> cache = imageCache.get(param);
	                final Bitmap  bm = cache.get();
	                MainActivity.mainContext.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                        	((ImageView)view).setImageBitmap(bm);
                        }
                    });
	            }
	            else{*/
            try {
                byte[] imageBytes= HttpClient.getInstance().postImg(url,param);
                final Bitmap bm= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                MainActivity.mainContext.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        ((ImageView)view).setImageBitmap(bm);
                        // view.setVisibility(View.VISIBLE);
                    }

                });
                //if(!imageCache.containsKey(param)){
                //	imageCache.put(param, new SoftReference<Bitmap>(bm));
                //}

                Log.e("", "下载图片完成");
            } catch (Exception e) {
                Log.e("img", e.getMessage());
                return null;
            }
            // }
        }
        return null;
    }

    protected void onPostExecute(Drawable drawable) {
        if (drawable != null) {
            ((ImageView)this.mView).setImageDrawable(drawable);
            Log.e("", "设置图片完成...");
            //this.mView = null;
        }
    }
}
