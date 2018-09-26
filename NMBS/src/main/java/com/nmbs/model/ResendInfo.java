package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResendInfo extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("Success")
    private boolean success;

    @SerializedName("FailureMessage")
    private String failureMessage;

    public boolean isSuccess() {
        return success;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
