package com.uncle_blue.nasa_hackthon.onetwofly.model;

/**
 * Created by huanglingchieh on 2017/4/30.
 */

public class Flight {
    private Airport deapture, destination;

    public Flight(Airport deapture, Airport destination){
        this.deapture = deapture;
        this.destination = destination;
    }

    public void setDeapture(Airport deapture){
        this.deapture = deapture;
    }

    public Airport getDeapture() {
        return deapture;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }
}
