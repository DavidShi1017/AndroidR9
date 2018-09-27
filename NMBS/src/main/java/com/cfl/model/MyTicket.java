package com.cfl.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by shig on 2016/5/19.
 */
public class MyTicket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originStationName;
    private String destinationStationName;
    private Date departure;
    private Date sortedDate;

    public MyTicket(String originStationName, String destinationStationName, Date departure, Date sortedDate){
        this.originStationName = originStationName;
        this.destinationStationName = destinationStationName;
        this.departure = departure;
        this.sortedDate = sortedDate;
    }

    public String getOriginStationName() {
        return originStationName;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public Date getDeparture() {
        return departure;
    }

    public Date getSortedDate() {
        return sortedDate;
    }
}
