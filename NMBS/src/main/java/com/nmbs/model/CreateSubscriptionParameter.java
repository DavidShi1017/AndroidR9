package com.nmbs.model;

/**
 * Created by Richard on 4/7/16.
 */
public class CreateSubscriptionParameter {
    private int id;
    private String userId;
    private String reconCtx;
    private String startDate;
    private String endDate;
    private int minDeviationInterval;
    private int minDeviationFollowing;
    private int notificationStart;
    private int selected;
    private String address;
    public CreateSubscriptionParameter(String userId, String reconCtx, String startDate, String endDate, int minDeviationFollowing, int minDeviationInterval,
                                       int notificationStart, String address, int selected){
        this.userId = userId;
        this.reconCtx = reconCtx;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minDeviationFollowing = minDeviationFollowing;
        this.minDeviationInterval = minDeviationInterval;
        this.notificationStart = notificationStart;
        this.address = address;
        this.selected = selected;
    }

    public CreateSubscriptionParameter(int id,String userId, String reconCtx, String startDate, String endDate, int minDeviationFollowing, int minDeviationInterval,
                                       int notificationStart, String address, int selected){
        this.userId = userId;
        this.reconCtx = reconCtx;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minDeviationFollowing = minDeviationFollowing;
        this.minDeviationInterval = minDeviationInterval;
        this.notificationStart = notificationStart;
        this.address = address;
        this.selected = selected;
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public int getNotificationStart(){
        return this.notificationStart;
    }

    public int getSelected(){
        return this.selected;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getReconCtx(){
        return this.reconCtx;
    }

    public String getStartDate(){
        return this.startDate;
    }

    public String getEndDate(){
        return this.endDate;
    }

    public int getMinDeviationInterval(){
        return this.minDeviationInterval;
    }

    public int getMinDeviationFollowing(){
        return this.minDeviationFollowing;
    }

    public String getAddress(){
        return this.address;
    }

}
