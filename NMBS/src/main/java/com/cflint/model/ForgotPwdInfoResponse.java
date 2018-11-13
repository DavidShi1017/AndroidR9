package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ForgotPwdInfoResponse extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("Info")
    private ForgotPwdInfo forgotPwdInfo;

    public ForgotPwdInfo getForgotPwdInfo() {
        return forgotPwdInfo;
    }
}
