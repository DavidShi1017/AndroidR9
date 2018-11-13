package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ForgotPwdInfo extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("Success")
    private boolean success;

    @SerializedName("FailureMessage")
    private String failureMessage;

    @SerializedName("ResetLogin")
    private boolean resetLogin;

    public boolean isSuccess() {
        return success;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public boolean isResetLogin() {
        return resetLogin;
    }
}

