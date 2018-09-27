package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileInfoResponse extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("ProfileInfo")
    private ProfileInfo profileInfo;

    public ProfileInfo getProfileInfo() {
        return profileInfo;
    }
}
