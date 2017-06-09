package com.lt.nexthud2017.setting;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lt.nexthud2017.MainActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/6/7.
 */

public class Brightness {
    public static String readBrightness()
    {
        try {
            FileInputStream is = MainActivity.mainContext.openFileInput("brightness.ini");
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            String value=new String(buffer);
            if(value.equals("") || value.equals(" ")){
                return "20-k";
            }
            return value;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return "20-k";
    }

    static void saveParam(String value)
    {
        try {

            FileOutputStream fos = MainActivity.mainContext.openFileOutput("brightness.ini" , Context.MODE_PRIVATE);
            fos.write(value.getBytes());
            fos.flush();
            fos.close();

        } catch (Exception e) {
            Log.e("text", e.getMessage());
        }
    }


    public static int LightIndex=8;
    public static void sendBrightness(String param){
        if(param.contains("20-a")){
            LightIndex=1;
            setHudI2c("-a");
        }
        else if(param.contains("20-c")){
            LightIndex=2;
            setHudI2c("-c");
        }
        else if(param.contains("20-e")){
            setHudI2c("-e");
            LightIndex=3;
        }
        else if(param.contains("20-g")){
            setHudI2c("-g");
            LightIndex=4;
        }
        else if(param.contains("20-i")){
            setHudI2c("-i");
            LightIndex=5;
        }
        else if(param.contains("20-k")){
            setHudI2c("-k");
            LightIndex=6;
        }
        else if(param.contains("20-m")){
            setHudI2c("-m");
            LightIndex=7;
        }
        else if(param.contains("20-o")){
            setHudI2c("-o");
            LightIndex=8;
        }
        else if(param.contains("20-q")){
            setHudI2c("-q");
            LightIndex=9;
        }
        else if(param.contains("20-s")){
            setHudI2c("-s");
            LightIndex=10;
        }
        else if(param.contains("20-t")){
            setHudI2c("-t");
            LightIndex=11;
        }
        else if(param.contains("20-u")){
            setHudI2c("-u");
            LightIndex=12;
        }
        else if(param.contains("20-w")){
            setHudI2c("-w");
            LightIndex=13;
        }
        else if(param.contains("20-y")){
            setHudI2c("-y");
            LightIndex=14;
        }
        else if(param.contains("20-A")){
            setHudI2c("-A");
            LightIndex=15;
        }

        saveParam(param);

    }


    public static int getLightIndex(String param)
    {
        int _LightIndex=0;
        if(param.contains("20-a")){
            _LightIndex=1;
        }
        else if(param.contains("20-c")){
            _LightIndex=2;
        }
        else if(param.contains("20-e")){
            _LightIndex=3;
        }
        else if(param.contains("20-g")){
            _LightIndex=4;
        }
        else if(param.contains("20-i")){
            _LightIndex=5;
        }
        else if(param.contains("20-k")){
            _LightIndex=6;
        }
        else if(param.contains("20-m")){
            _LightIndex=7;
        }
        else if(param.contains("20-o")){
            _LightIndex=8;
        }
        else if(param.contains("20-q")){
            _LightIndex=9;
        }
        else if(param.contains("20-s")){
            _LightIndex=10;
        }
        else if(param.contains("20-t")){
            _LightIndex=11;
        }
        else if(param.contains("20-u")){
            _LightIndex=12;
        }
        else if(param.contains("20-w")){
            _LightIndex=13;
        }
        else if(param.contains("20-y")){
            _LightIndex=14;
        }
        else if(param.contains("20-A")){
            _LightIndex=15;
        }

        return _LightIndex;
    }

    public static void setHudI2c(String param)
    {
        try {
			/* Missing read/write permission, trying to chmod the file */
            Process su;
            su = Runtime.getRuntime().exec("hudi2c "+param);
            int exitVal = su.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.mainContext,e.getMessage(),Toast.LENGTH_LONG).show();
            //throw new SecurityException();
        }

    }

    public static void sendLight(int index)
    {
        switch(index)
        {
            case 0:
                sendBrightness("20-a");
                break;
            case 1:
                sendBrightness("20-a");
                break;
            case 2:
                sendBrightness("20-c");
                break;
            case 3:
                sendBrightness("20-e");
                break;
            case 4:
                sendBrightness("20-g");
                break;
            case 5:
                sendBrightness("20-i");
                break;
            case 6:
                sendBrightness("20-k");
                break;
            case 7:
                sendBrightness("20-m");
                break;
            case 8:
                sendBrightness("20-o");
                break;
            case 9:
                sendBrightness("20-q");
                break;
            case 10:
                sendBrightness("20-s");
                break;
            case 11:
                sendBrightness("20-t");
                break;
            case 12:
                sendBrightness("20-u");
                break;
            case 13:
                sendBrightness("20-w");
                break;
            case 14:
                sendBrightness("20-y");
                break;
            case 15:
                sendBrightness("20-A");
                break;
        }
        //Toast.makeText(MainActivity.mainContext,"亮度:"+index,Toast.LENGTH_LONG).show();
    }
}

