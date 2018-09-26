package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResendInfoResponse extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("ResendInfo")
    private ResendInfo resendInfo;

    public ResendInfo getResendInfo() {
        return resendInfo;
    }
}
