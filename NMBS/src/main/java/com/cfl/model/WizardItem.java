package com.cfl.model;

import java.io.Serializable;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;

public class WizardItem implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Description")
	private String description;
	
	@SerializedName("HighResImage")
	private String highResolutionImage;
	
	@SerializedName("LowResImage")
	private String lowResolutionImage;
	
	@SerializedName("AreaTitle")
	private String areaTitle;
	
	@SerializedName("AreaIcon")
	private String areaIcon;	
	
	@SerializedName("NewInRelease")
	private boolean isNew;
	
	@SerializedName("Context")
	private String [] context;

	public String getTitle() {
		return title;
	}

	public String getHighResolutionImage() {
		return highResolutionImage;
	}
	public String getLowResolutionImage() {
		return lowResolutionImage;
	}
	
	
	public String getDescription() {
		return description;
	}

	public String getAreaTitle() {
		return areaTitle;
	}

	public String getAreaIcon() {
		return areaIcon;
	}

	public String[] getContext() {
		return context;
	}

	public boolean isNew() {
		return isNew;
	}
	public String getImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.highResolutionImage;
		}else{
			return this.lowResolutionImage;
		}
	}
	
}
