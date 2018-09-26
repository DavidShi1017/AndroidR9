package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Richard on 3/14/16.
 */

public class ScheduleDetailRefreshModel {
    private static final long serialVersionUID = 1L;
    @SerializedName("ConnectionId")
    private String connectionId;
    @SerializedName("DepartureDate")
    private Date departureDate;

    public ScheduleDetailRefreshModel(String connectionID, Date departureDate){
        this.connectionId = connectionID;
        this.departureDate = departureDate;
    }

    public String getConnectionId(){
        return this.connectionId;
    }

    public Date getDepartureDate(){
        return this.departureDate;
    }

}
