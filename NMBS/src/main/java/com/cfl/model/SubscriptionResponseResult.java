package com.cfl.model;

import java.util.List;

/**
 * Created by Richard on 5/20/16.
 */
public class SubscriptionResponseResult {
    private boolean isSuccess;
    private List<SubscriptionResponse> subscriptionResponses;

    public void setSuccess(boolean isSuccess){
        this.isSuccess = isSuccess;
    }

    public void setSubscriptionResponses(List<SubscriptionResponse> subscriptionResponses){
        this.subscriptionResponses = subscriptionResponses;
    }

    public boolean isSuccess(){
        return this.isSuccess;
    }

    public List<SubscriptionResponse> getSubscriptionResponses(){
       return this.subscriptionResponses;
    }
}
