package com.nmbs.model;

/**
 * Created by Richard on 5/10/16.
 */
public class SubscriptionResponse {
    private String id;
    private String subscriptionId;
    private boolean isSuccess;
    private ErrorType errorType;
    public enum ErrorType{
        NOSUCHSUBSCRIPTION,OTHERERROR
    }
    public void setId(String id){
        this.id = id;
    }
    public ErrorType getErrorType(){
        return this.errorType;
    }

    public void setErrorType(ErrorType errorType){
        this.errorType = errorType;
    }
    public void setSubscriptionId(String subscriptionId){
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId(){
        return this.subscriptionId;
    }

    public void setSuccess(boolean isSuccess){
        this.isSuccess = isSuccess;
    }

    public String getId(){
        return id;
    }

    public boolean isSuccess(){
        return isSuccess;
    }
}
