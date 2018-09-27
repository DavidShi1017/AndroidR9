package com.cfl.model;

/**
 * Created by shig on 2016/5/6.
 */
public class StationIsVirtualText {
    private String stationCode;
    private String stationName;
    private String stationText;

    public StationIsVirtualText(String stationCode, String stationName, String stationText){
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.stationText = stationText;
    }
    public String getStationCode() {
        return stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationText() {
        return stationText;
    }
}
