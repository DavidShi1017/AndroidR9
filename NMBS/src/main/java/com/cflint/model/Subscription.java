package com.cflint.model;

/**
 * Created by Richard on 4/7/16.
 */
public class Subscription {
    private int id;
    private String subscriptionId;
    private String reconCtx;
    private String originStationRcode;
    private String originStationRcodeHashCocde;
    private String destinationStationRcode;
    private String departure;
    private String originStationName;
    private String destinationStationName;
    private String dnr;
    private String subscriptionStatus;
    private String language;
    private String connectionId;
    public Subscription(String subscriptionId,String reconCtx, String originStationRcode,String destinationStationRcode, String  departure,
                        String originStationName, String destinationStationName,String dnr, String connectionId){
        this.subscriptionId = subscriptionId;
        this.reconCtx = reconCtx;
        this.originStationRcode = originStationRcode;
        this.destinationStationRcode = destinationStationRcode;
        this.departure= departure;
        this.originStationName = originStationName;
        this.destinationStationName = destinationStationName;
        this.dnr = dnr;
        this.connectionId = connectionId;
    }

    public Subscription(String subscriptionId,String originStationRcodeHashCocde,String reconCtx, String originStationRcode,String destinationStationRcode, String  departure,
                        String originStationName, String destinationStationName,String dnr, String connectionId){
        this.subscriptionId = subscriptionId;
        this.reconCtx = reconCtx;
        this.originStationRcode = originStationRcode;
        this.destinationStationRcode = destinationStationRcode;
        this.departure= departure;
        this.originStationName = originStationName;
        this.destinationStationName = destinationStationName;
        this.dnr = dnr;
        this.originStationRcodeHashCocde = originStationRcodeHashCocde;
        this.connectionId = connectionId;
    }

    public Subscription(){

    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDnr(String dnr) {
        this.dnr = dnr;
    }

    public String getOriginStationRcodeHashCocde(){
        return this.originStationRcodeHashCocde;
    }

    public void setSubscriptionId(String subscriptionId){
        this.subscriptionId = subscriptionId;
    }

    public void setSubscriptionStatus(String subscriptionStatus){
        this.subscriptionStatus = subscriptionStatus;
    }

    public void setReconCtx(String reconCtx){
        this.reconCtx = reconCtx;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getSubscriptionStatus(){
        return this.subscriptionStatus;
    }

    public String getLanguage(){
        return this.language;
    }

    public String getOriginStationName(){
        return this.originStationName;
    }

    public String getDestinationStationName(){
        return this.destinationStationName;
    }

    public String getDnr(){
        return this.dnr;
    }
    public String getSubscriptionId(){
        return this.subscriptionId;
    }

    public String getReconCtx(){
        return this.reconCtx;
    }

    public String getOriginStationRcode(){
        return  this.originStationRcode;
    }

    public String getDestinationStationRcode(){
        return this.destinationStationRcode;
    }

    public String getDeparture(){
        return this.departure;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
