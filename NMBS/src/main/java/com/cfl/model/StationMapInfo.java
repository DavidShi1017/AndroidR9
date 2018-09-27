package com.cfl.model;

/**
 * Created by Richard on 2/16/16.
 */
public class StationMapInfo {
    private String name;
    private String address;
    private boolean isParking;
    private double latitude;
    private double longitude;
    public StationMapInfo(String name, String address, boolean isParking, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.isParking = isParking;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public boolean isParking(){
        return isParking;
    }

}
