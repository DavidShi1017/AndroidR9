package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class GeneralSetting implements Serializable{


	private static final long serialVersionUID = 1L;


	@SerializedName("RestSalt")
	private String restSalt;

	@SerializedName("AutoLogonSalt")
	private String autoLogonSalt;

	@SerializedName("FacebookAppId")
	private String facebookAppId;

	@SerializedName("GoogleAppId")
	private String googleAppId;

	@SerializedName("CreateProfileUrl")
	private String createProfileUrl;

	@SerializedName("ProfileOverviewUrl")
	private String profileOverviewUrl;

	@SerializedName("CommercialTtlListUrl")
	private String commercialTtlListUrl;

	@SerializedName("PrivacyPolicyUrl")
	private String privacyPolicyUrl;

	@SerializedName("Domain")
	private String domain;

	@SerializedName("MaxRealTimeInfoHorizon")
	private int maxRealTimeInfoHorizon;
	
	@SerializedName("DossierAftersalesLifetime")
	private int dossierAftersalesLifetime;
	
	@SerializedName("BookingUrl")
	private String bookingUrl;
	
	@SerializedName("LffUrl")
	private String lffUrl;
     
	@SerializedName("BelgiumPhoneNumber")
	private String belgiumPhoneNumber;
	
	@SerializedName("InternationalPhoneNumber")
	private String internationalPhoneNumber;

	@SerializedName("AllowContextRegistration")
	private boolean allowContextRegistration;

	@SerializedName("InsuranceConditionsPdf")
	private String insuranceConditionsPdf;

	@SerializedName("CheckLastUpdateTimestampPassword")
	private boolean checkLastUpdateTimestampPassword;

	public GeneralSetting(int maxRealTimeInfoHorizon,  int dossierAftersalesLifetime, String bookingUrl, String lffUrl,
						  String belgiumPhoneNumber, String internationalPhoneNumber, boolean allowContextRegistration,
						  String insuranceConditionsPdf, String restSalt, String autoLogonSalt, String facebookAppId,
						  String googleAppId, String createProfileUrl, String profileOverviewUrl, String commercialTtlListUrl,
						  String privacyPolicyUrl, String domain, boolean checkLastUpdateTimestampPassword){
		this.restSalt = restSalt;
		this.autoLogonSalt = autoLogonSalt;
		this.facebookAppId = facebookAppId;
		this.googleAppId = googleAppId;
		this.createProfileUrl = createProfileUrl;
		this.profileOverviewUrl = profileOverviewUrl;
		this.commercialTtlListUrl = commercialTtlListUrl;
		this.privacyPolicyUrl = privacyPolicyUrl;
		this.domain = domain;
		this.checkLastUpdateTimestampPassword = checkLastUpdateTimestampPassword;

		this.maxRealTimeInfoHorizon = maxRealTimeInfoHorizon;
		this.dossierAftersalesLifetime = dossierAftersalesLifetime;
		this.bookingUrl = bookingUrl;
		this.lffUrl = lffUrl;
		this.belgiumPhoneNumber = belgiumPhoneNumber;
		this.internationalPhoneNumber = internationalPhoneNumber;
		this.allowContextRegistration = allowContextRegistration;
		this.insuranceConditionsPdf = insuranceConditionsPdf;
	}
	
	

	public int getMaxRealTimeInfoHorizon() {
		return maxRealTimeInfoHorizon;
	}




	public int getDossierAftersalesLifetime() {
		return dossierAftersalesLifetime;
	}




	public String getBookingUrl() {
		return bookingUrl;
	}




	public String getLffUrl() {
		return lffUrl;
	}




	public String getBelgiumPhoneNumber() {
		return belgiumPhoneNumber;
	}




	public String getInternationalPhoneNumber() {
		return internationalPhoneNumber;
	}




	public boolean isAllowContextRegistration() {
		return allowContextRegistration;
	}

	public void setAllowContextRegistration(boolean allowContextRegistration) {
		this.allowContextRegistration = allowContextRegistration;
	}

	public String getInsuranceConditionsPdf() {
		return insuranceConditionsPdf;
	}
	@SerializedName("BookingComAid")
	private String bookingComAid;
	
	@SerializedName("DefaultOriginStation")
	private String defaultOriginStation;
	


	public String getBookingComAid() {
		return bookingComAid;
	}

	public String getRestSalt() {
		return restSalt;
	}

	public String getAutoLogonSalt() {
		return autoLogonSalt;
	}

	public String getFacebookAppId() {
		return facebookAppId;
	}

	public String getGoogleAppId() {
		return googleAppId;
	}

	public String getCreateProfileUrl() {
		return createProfileUrl;
	}

	public String getProfileOverviewUrl() {
		return profileOverviewUrl;
	}

	public String getCommercialTtlListUrl() {
		return commercialTtlListUrl;
	}

	public String getPrivacyPolicyUrl() {
		return privacyPolicyUrl;
	}

	public String getDomain() {
		return domain;
	}

	public boolean isCheckLastUpdateTimestampPassword() {
		return checkLastUpdateTimestampPassword;
		//return true;
	}

	public String getDefaultOriginStation() {
		return defaultOriginStation;
	}
	public GeneralSetting(String bookingComAid, String defaultOriginStation, int dossierAftersalesLifetime){
		this.bookingComAid = bookingComAid;
		this.defaultOriginStation = defaultOriginStation;
		this.dossierAftersalesLifetime = dossierAftersalesLifetime;
	}


}
