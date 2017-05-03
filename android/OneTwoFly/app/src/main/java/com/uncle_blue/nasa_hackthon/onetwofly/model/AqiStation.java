package com.uncle_blue.nasa_hackthon.onetwofly.model;

/**
 * Created by huanglingchieh on 2017/4/30.
 */

public class AqiStation {
    private double latitude, longitude;
    private int uid, aqi;

    public AqiStation(double latitude, double longitude, int uid, int aqi){
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
        this.aqi = aqi;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }
}
