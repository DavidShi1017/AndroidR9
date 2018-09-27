package com.cfl.model;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Description")
	private String description;
	
	@SerializedName("Validity")
	private String validity;
	
	@SerializedName("HighResIcon")
	private String highResIcon;
	
	@SerializedName("LowResIcon")
	private String lowResIcon;
	
	@SerializedName("HighResImage")
	private String highResImage;
	
	@SerializedName("LowResImage")
	private String lowResImage;
	
	@SerializedName("IncludeActionButton")
	private boolean includeActionButton;
	
	@SerializedName("ActionButtonText")
	private String actionButtonText;
	
	@SerializedName("Hyperlink")
	private String hyperlink;

	public UserMessage(String title,String description, String validity, String highResIcon, String lowResIcon, String highResImage,
					   String lowResImage, boolean includeActionButton, String actionButtonText, String hyperlink){
		this.title = title;
		this.description = description;
		this.validity = validity;
		this.highResIcon = highResIcon;
		this.highResImage = highResImage;
		this.lowResIcon = lowResIcon;
		this.lowResImage = lowResImage;
		this.includeActionButton = includeActionButton;
		this.actionButtonText = actionButtonText;
		this.hyperlink = hyperlink;
	}
	
	public String getTitle() {
		return title;
	}



	public String getDescription() {
		return description;
	}



	public String getValidity() {
		return validity;
	}



	public String getHighResIcon() {
		return highResIcon;
	}



	public String getLowResIcon() {
		return lowResIcon;
	}



	public String getHighResImage() {
		return highResImage;
	}



	public String getLowResImage() {
		return lowResImage;
	}



	public boolean isIncludeActionButton() {
		return includeActionButton;
	}



	public String getActionButtonText() {
		return actionButtonText;
	}



	public String getHyperlink() {
		return hyperlink;
	}



	public String getImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.highResImage;
		}else{
			return this.lowResImage;
		}
	}

	public String getIcon(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.highResIcon;
		}else{
			return this.lowResIcon;
		}
	}

	public UserMessage(){}


	
}
