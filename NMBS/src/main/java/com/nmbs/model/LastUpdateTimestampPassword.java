package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class LastUpdateTimestampPassword extends RestResponse implements Serializable {

    @SerializedName("LastUpdateTimestampPassword")
    private Date lastUpdateTimestampPassword;

    public Date getLastUpdateTimestampPassword() {
        return lastUpdateTimestampPassword;
    }
}
