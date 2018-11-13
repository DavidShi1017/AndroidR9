package com.cflint.model;

/**
 * Created by Richard on 4/6/16.
 */
public class HafasUser {
    private String userId;
    private String registerId;
    private String language;
    private int minDeviationInterval;
    private int minDeviationFollowing;
    private int notificationStart;

    public HafasUser(String userId, String registerId, String language){
        this.userId = userId;
        this.registerId = registerId;
        this.language = language;
    }

    public HafasUser(String userId, String registerId, String language, int minDeviationInterval,int minDeviationFollowing,int notificationStart){
        this.userId = userId;
        this.registerId = registerId;
        this.language = language;
        this.minDeviationFollowing = minDeviationFollowing;
        this.minDeviationInterval = minDeviationInterval;
        this.notificationStart = notificationStart;
    }


    public int getMinDeviationInterval(){
        return this.minDeviationInterval;
    }

    public int getMinDeviationFollowing(){
        return this.minDeviationFollowing;
    }

    public int getNotificationStart(){
        return this.notificationStart;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getLanguage(){
        return this.language;
    }

    public String getRegisterId(){
        return this.registerId;
    }
}
