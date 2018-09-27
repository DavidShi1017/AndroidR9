package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("LogonInfo")
    private LogonInfo logonInfo;

    public LogonInfo getLogonInfo() {
        return logonInfo;
    }
}
