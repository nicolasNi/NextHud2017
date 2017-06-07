package com.lt.nexthud2017.weixin.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/7.
 */

public class DownLoadQrCodeThread extends Thread{
    private String imageUrl="";
    private OnloadQrCodeFinnishListener listener;

    public void setListener(OnloadQrCodeFinnishListener listener) {
        this.listener = listener;
    }
    public interface OnloadQrCodeFinnishListener{
        void onLoadSuccess(byte[] imageBytes);
    }


    public DownLoadQrCodeThread(String url,boolean writeToFile) {
        imageUrl=url;
    }
    @Override
    public void run() {
        URL url;
        DataInputStream bfin = null;
        try {
            url=new URL(imageUrl);
            bfin=new DataInputStream(url.openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length=0;
            while((length = bfin.read(buffer))>0){
                bos.write(buffer, 0, length);
            }
            bfin.close();

            byte[] imageBytes=bos.toByteArray();
            if (listener!=null) {
                listener.onLoadSuccess(imageBytes);
            }
            bos.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}

