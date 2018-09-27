package com.cfl.model;

import java.io.Serializable;
import java.util.Date;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;

public class MobileMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Description")
	private String description;
	
	@SerializedName("Validity")
	private String validity;
	
	@SerializedName("LowResIcon")
	private String lowResIcon;

	@SerializedName("HighResIcon")
	private String highResIcon;

	@SerializedName("LowResImage")
	private String lowResImage;

	@SerializedName("HighResImage")
	private String highResImage;
	
	@SerializedName("MessageType")
	private String messageType;
	
	@SerializedName("IncludeActionButton")
	private boolean includeActionButton;

	@SerializedName("ActionButtonText")
	private String actionButtonText;

	@SerializedName("Hyperlink")
	private String hyperlink;

	@SerializedName("DisplayInOverlay")
	private boolean displayInOverlay;

	@SerializedName("RepeatDisplayInOverlay")
	private int repeatDisplayInOverlay;

	@SerializedName("NavigationInNormalWebView")
	private boolean navigationInNormalWebView;

	private String nextDisplay;
    
	public MobileMessage(String id, String title, String description, String validity, String lowResIcon, String highResIcon, String lowResImage,
						 String highResImage, String messageType, boolean includeActionButton, String actionButtonText, String hyperlink,
						 boolean displayInOverlay, int repeatDisplayInOverlay, boolean navigationInNormalWebView){
		this.id = id;
		this.title = title;
		this.description = description;
		this.validity = validity;
		this.lowResIcon = lowResIcon;
		this.highResIcon = highResIcon;
		this.lowResImage = lowResImage;
		this.highResImage = highResImage;
		this.messageType = messageType;
		this.includeActionButton = includeActionButton;
		this.actionButtonText = actionButtonText;
		this.hyperlink = hyperlink;
		this.displayInOverlay = displayInOverlay;
		this.repeatDisplayInOverlay = repeatDisplayInOverlay;
		this.navigationInNormalWebView = navigationInNormalWebView;
	}
	
	
	public String getActionButtonText() {
		return actionButtonText;
	}




	public void setActionButtonText(String actionButtonText) {
		this.actionButtonText = actionButtonText;
	}


	public void setNextDisplay(String nextDisplay) {
		this.nextDisplay = nextDisplay;
	}

	public String getNextDisplay() {
		return nextDisplay;
	}

	public String getId() {
		return id;
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




	public String getLowResIcon() {
		return lowResIcon;
	}




	public String getHighResIcon() {
		return highResIcon;
	}




	public String getLowResImage() {
		return lowResImage;
	}




	public String getHighResImage() {
		return highResImage;
	}




	public String getMessageType() {
		return messageType;
	}




	public boolean isIncludeActionButton() {
		return includeActionButton;
	}




	public String getHyperlink() {
		return hyperlink;
	}




	public boolean isDisplayInOverlay() {
		return displayInOverlay;
	}




	public int getRepeatDisplayInOverlay() {
		return repeatDisplayInOverlay;
	}

	public boolean isNavigationInNormalWebView() {
		return navigationInNormalWebView;
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
}
