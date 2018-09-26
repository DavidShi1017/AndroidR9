package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeneralSettingResponse extends RestResponse implements Serializable{

	
	private static final long serialVersionUID = 1L;

	@SerializedName("Settings")
	private GeneralSetting settings;

	public GeneralSetting getGeneralSetting() {
		return settings;
	}
	
	
}
