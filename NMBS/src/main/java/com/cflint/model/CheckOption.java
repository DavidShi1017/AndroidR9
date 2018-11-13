package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class CheckOption extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("Count")
    private int count;

    @SerializedName("Expiration")
    private Date expiration;

    @SerializedName("CallSuccessful")
    private boolean callSuccessful;

    public CheckOption(int count, Date expiration){
        this.count = count;
        this.expiration = expiration;
    }

    public int getCount() {
        return count;
    }

    public Date getExpiration() {
        return expiration;
    }

    public boolean isCallSuccessful() {
        return callSuccessful;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

