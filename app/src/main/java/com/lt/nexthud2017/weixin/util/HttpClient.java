package com.lt.nexthud2017.weixin.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/6/7.
 */

public class HttpClient {
    String sessionID = "";
    public String contentType="";
    private static HttpClient hcClient;

    public static HttpClient getInstance(){
        if (hcClient==null) {
            synchronized (HttpClient.class) {
                if (hcClient==null) {
                    hcClient=new HttpClient();
                }
            }
        }
        return hcClient;
    }

    public static void reset(){
        synchronized (HttpClient.class) {
            hcClient=null;
        }
    }


    public String getLoginResult(String url){
        StringBuilder sb=new StringBuilder();
        try{
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection =(HttpURLConnection)url1.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line="";
            while ((line=bufferedReader.readLine()) != null){
                sb.append(line);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getLoginResultFromOKHttp(String url){
        String result="";
        try{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            result = response.body().string();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String getLoginResultFromRetrofit(String tip, String uuid){
        String result="";
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://login.weixin.qq.com/")
                    .build();

            WeChatService service = retrofit.create(WeChatService.class);

            retrofit2.Call<ResponseBody> call = service.getResult(tip,uuid);
            retrofit2.Response<ResponseBody> response = call.execute();
            result = response.body().string();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String get(String url, String charset,String referer,boolean isRedirects) {
        try {
            String key = "";
            String cookieVal = "";

            trustAllHosts();
            URL httpURL = new URL(url);
            HttpsURLConnection http = (HttpsURLConnection) httpURL
                    .openConnection();


            http.setInstanceFollowRedirects(isRedirects);
            if (referer!=null ) {
                http.setRequestProperty("Referer", referer);
            }
            if (contentType!=null ) {
                http.setRequestProperty("content-type", contentType);
            }
            http.setRequestProperty("User-agent","Mozilla/5.0 (X11; " + "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) " +"Chrome/11.0.696.34 Safari/534.24");
            if (!sessionID.equals("")) {
                http.setRequestProperty("Cookie", sessionID);
            }

            http.setHostnameVerifier(DO_NOT_VERIFY);

            for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = http.getHeaderField(i);
                    cookieVal = cookieVal.substring(
                            0,
                            cookieVal.indexOf(";") > -1 ? cookieVal
                                    .indexOf(";") : cookieVal.length() - 1);
                    sessionID = sessionID + cookieVal + ";";
                }
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    http.getInputStream(), charset));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            printEx(e);
            return e.toString();
        }

    }


    public String get(String url) {
        return get(url, "utf-8", null, true);
    }


    public static void trustAllHosts()
    {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                    }

                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public String post(String url, String data, String charset,String referer,boolean isRedirects) {
        try {
            trustAllHosts();
            URL httpURL = new URL(url);
            String key = null;
            String cookieVal = null;
            HttpsURLConnection http = (HttpsURLConnection) httpURL
                    .openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; " + "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) " +"Chrome/11.0.696.34 Safari/534.24");
            http.setInstanceFollowRedirects(isRedirects);
            if (referer!=null ) {
                http.setRequestProperty("Referer", referer);
            }
            if (contentType!=null ) {
                http.setRequestProperty("content-type", contentType);
            }
            if (!sessionID.equals("") && sessionID!=null) {
                http.setRequestProperty("Cookie", sessionID);
            }

            http.setHostnameVerifier(DO_NOT_VERIFY);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    http.getOutputStream(), charset));
            bw.write(data);
            bw.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    http.getInputStream(), charset));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
            br.close();
            for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = http.getHeaderField(i);
                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                    sessionID = sessionID + cookieVal + ";";
                }
            }
            return sb.toString();
        } catch (Exception e) {
            printEx(e);
            return e.toString();
        }
    }


    public byte[] postImg(String url, String data, String charset,String referer,boolean isRedirects) {
        try {

            URL httpURL = new URL(url);
            String key = null;
            String cookieVal = null;
            HttpURLConnection http = (HttpURLConnection) httpURL
                    .openConnection();

            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; " + "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) " +"Chrome/11.0.696.34 Safari/534.24");
            http.setInstanceFollowRedirects(isRedirects);

            if (referer!=null ) {
                http.setRequestProperty("Referer", referer);
            }
            if (contentType!=null ) {
                http.setRequestProperty("content-type", contentType);
            }
            if (!sessionID.equals("") && sessionID!=null) {
                http.setRequestProperty("Cookie", sessionID);
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    http.getOutputStream(), charset));
            bw.write(data);
            bw.close();

            InputStream stream= http.getInputStream();

            Log.e("wx", "mp3:"+	http.getResponseMessage());

            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] buffer = new byte[20480];
            int length=0;
            while((length = stream.read(buffer))>0){
                bos.write(buffer, 0, length);
            }
            stream.close();

            for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = http.getHeaderField(i);
                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                    sessionID = sessionID + cookieVal + ";";
                }
            }
            return 	bos.toByteArray();
        } catch (Exception e) {
            printEx(e);
        }
        return null;
    }


    void printEx(Exception e)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.e("wechat", "error:"+result);
    }

    public byte[] postImg(String url,String data) {
        return postImg(url, data, "utf-8", null, true);

    }

    public byte[] postVedio(String url,String data) {
        return postVedio(url, data, "utf-8", null, true);

    }


    public byte[] postVedio(String url, String data, String charset,String referer,boolean isRedirects) {
        try {
            String key = "";
            String cookieVal = "";

            trustAllHosts();

            URL httpURL = new URL(url);
            HttpsURLConnection http = (HttpsURLConnection) httpURL
                    .openConnection();
            //http.setInstanceFollowRedirects(isRedirects);
            http.setRequestProperty("Accept", "*/*");
            http.setRequestProperty("Referer", "https://wx.qq.com/?&lang=zh_CN");
            http.setRequestProperty("Accept-Encoding" , "identity;q=1, *;q=0");
            http.setRequestProperty("Connection" , "keep-alive");
            http.setRequestProperty("Range", "bytes=0-");
            http.setRequestProperty("Referer", "https://wx.qq.com/?&lang=zh_CN");
            http.setHostnameVerifier(DO_NOT_VERIFY);
            http.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            if (!sessionID.equals("")) {
                http.setRequestProperty("Cookie", sessionID);
                Log.e("wx", "Cookie"+sessionID);
            }

            for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = http.getHeaderField(i);
                    cookieVal = cookieVal.substring(
                            0,
                            cookieVal.indexOf(";") > -1 ? cookieVal
                                    .indexOf(";") : cookieVal.length() - 1);
                    sessionID = sessionID + cookieVal + ";";
                }
            }

            InputStream stream= http.getInputStream();

            Log.e("wx", "vedio:"+	http.getResponseMessage());

            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] buffer = new byte[20480];
            int length=0;
            while((length = stream.read(buffer))>0){
                bos.write(buffer, 0, length);
            }
            stream.close();

            return 	bos.toByteArray();

        } catch (Exception e) {
            printEx(e);
        }
        return null;
    }

    public String post(String url,String data) {
        return post(url, data, "utf-8", null, true);

    }
}
