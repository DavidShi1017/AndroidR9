package com.cflint.model;

/**
 * Created by Richard on 3/29/16.
 */
public class FavoriteStation {
    private String stationCode;
    private boolean isStationBoardEnabled;
    public FavoriteStation(String stationCode, boolean isStationBoardEnabled){
        this.stationCode = stationCode;
        this.isStationBoardEnabled = isStationBoardEnabled;
    }

    public String getStationCode(){
        return this.stationCode;
    }

    public boolean isStationBoardEnabled(){
        return isStationBoardEnabled;
    }
}
