package com.uncle_blue.nasa_hackthon.onetwofly.util.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huanglingchieh on 2017/4/30.
 */

public class OtfApiHelper {
    public static final String OTF_SERVER = "http://10.20.4.115:8000/testing";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient mClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static Response GET(String url ) throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder().url(url)
                .build();
        //will get IOException when there is no internet
        Response response = mClient.newCall(request).execute();
        return response;
    }

    public static Response POST(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        }catch (IOException e){
            System.out.println(e.getStackTrace());
        }
        return response;
    }

    public static String stringToJson(String string){
        return null;
    }
}
