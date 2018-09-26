package com.nmbs.model;

import java.util.Date;

/**
 * Created by Richard on 6/7/16.
 */
public class LocalNotification {
    private Date departureDate;
    private int notificationId;
    private boolean isCancel;

    public LocalNotification(Date departureDate, int notificationId, boolean isCancel){
        this.departureDate = departureDate;
        this.notificationId = notificationId;
        this.isCancel = isCancel;
    }

    public Date getDepartureDate(){
        return this.departureDate;
    }

    public int getNotificationId(){
        return this.notificationId;
    }

    public boolean isCancel(){
        return this.isCancel;
    }
}
