package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DossiersList extends RestResponse implements Serializable {

    @SerializedName("Dnrs")
    private List<String> Dnrs;

    @SerializedName("CallSuccessful")
    private boolean callSuccessful;

    public List<String> getDnrs() {
        return Dnrs;
    }

    public boolean isCallSuccessful() {
        return callSuccessful;
    }
}
