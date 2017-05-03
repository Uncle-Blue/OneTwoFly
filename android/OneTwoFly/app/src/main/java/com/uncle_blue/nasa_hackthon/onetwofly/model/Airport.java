package com.uncle_blue.nasa_hackthon.onetwofly.model;

/**
 * Created by huanglingchieh on 2017/4/30.
 */

public class Airport {
    private double latitude, longitude;
    private int humidity, temperature;
    private String airportName;

    public Airport(double latitude, double longitude, int humidity, int temperature,
                   String airportName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.humidity = humidity;
        this.temperature = temperature;
        this.airportName = airportName;
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

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }
}
