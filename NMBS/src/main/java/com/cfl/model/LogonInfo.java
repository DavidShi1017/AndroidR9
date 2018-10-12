package com.cfl.model;

import com.google.gson.annotations.SerializedName;
import com.cfl.application.NMBSApplication;
import com.cfl.util.DecryptUtils;
import com.cfl.util.Utils;

import java.io.Serializable;
import java.util.Date;

public class LogonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("CustomerId")
    private String customerId;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("Email")
    private String email;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("State")
    private String state;

    @SerializedName("StateDescription")
    private String stateDescription;

    @SerializedName("PersonId")
    private String personId;

    @SerializedName("LastUpdateTimestampPassword")
    private Date lastUpdateTimestampPassword;

    @SerializedName("Id")
    private String id;


    private String code;
    private String loginProvider;

    public LogonInfo(String customerId, String firstName, String email, String code, String phoneNumber, String state,
                     String stateDescription, String personId, Date lastUpdateTimestampPassword, String loginProvider){
        this.customerId = customerId;
        this.firstName = firstName;
        this.email = email;
        this.code = code;
        this.phoneNumber = phoneNumber;
        this.state = state;
        this.stateDescription = stateDescription;
        this.personId = personId;
        this.lastUpdateTimestampPassword = lastUpdateTimestampPassword;
        this.loginProvider = loginProvider;
    }

    public String getState() {
        return state;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public String getCustomerId() {
        return customerId;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber == null? "": "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {

        return code == null? "": "";
    }

    public String getPersonId() {
        return personId;
    }

    public Date getLastUpdateTimestampPassword() {
        return lastUpdateTimestampPassword;
    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public void setLoginProvider(String loginProvider) {
        this.loginProvider = loginProvider;
    }

    public String getId() {
        return id;
    }
}
