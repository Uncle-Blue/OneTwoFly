package com.uncle_blue.nasa_hackthon.onetwofly.util.network;

import com.uncle_blue.nasa_hackthon.onetwofly.model.Airport;
import com.uncle_blue.nasa_hackthon.onetwofly.model.AqiStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    public static final String URL_OTF_SERVER_ = "http://10.20.4.115:8000/testing";
    public static final String URL_AQI_STATION_SERVER = "https://api.waqi.info/map/bounds/?";

    public static final String TOKEN_AQI_STATION_API = "4b33edc080ac635bd7259d9d5f9a1a9d8fa465c4";

    public static final int TYPE_FLIGHT = 666;
    public static final int TYPE_AQI_STATION = 667;

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

    public static String getAqiStationRequestUrl(float latitude, float longtitude){
        return new StringBuffer(URL_AQI_STATION_SERVER)
                .append("latlng=" + String.valueOf(latitude - 1))
                .append("," + String.valueOf(longtitude - 1))
                .append("," + String.valueOf(latitude + 1))
                .append("," + String.valueOf(longtitude + 1))
                .append("&token=" + TOKEN_AQI_STATION_API)
                .toString();
    }

    public static ArrayList<AqiStation> parseAqiStationJsonData(String rawData) throws JSONException {
        ArrayList<AqiStation> stations = new ArrayList<>();

        JSONObject obj = new JSONObject(rawData);
        JSONArray dataSet = obj.getJSONArray("data");

        for (int i = 0; i < dataSet.length(); i++)
        {
            try {
                double latitude = dataSet.getJSONObject(i).getDouble("lat");
                double longtitude = dataSet.getJSONObject(i).getDouble("lon");
                int uid = dataSet.getJSONObject(i).getInt("uid");

                // FIXME: workaround for unknown data value.
                String aqiStr = dataSet.getJSONObject(i).getString("aqi");
                int aqi = -1;
                if(!"-".equals(aqiStr)){
                    aqi = Integer.parseInt(aqiStr);
                }

                AqiStation aqiStation = new AqiStation(latitude, longtitude, uid, aqi);
                stations.add(aqiStation);
            }catch (Exception e){
                String string = e.getMessage();
            }
        }

        return stations;
    }

    public static Airport parseAirportJsonData(String rawData) throws JSONException {
        JSONObject obj = new JSONObject(rawData);
        Airport airport = new Airport(
                obj.getDouble("latitude"),
                obj.getDouble("longitude"),
                obj.getInt("humidity"),
                obj.getInt("temperature"),
                obj.getString("airportName")
        );

        return airport;
    }
}
