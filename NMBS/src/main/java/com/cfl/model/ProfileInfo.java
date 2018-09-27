package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileInfo extends RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("Email")
    private String email;

    @SerializedName("MobilePhone")
    private String mobilePhone;

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }
}

