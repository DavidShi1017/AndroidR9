package com.nmbs.model;

import java.io.Serializable;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;


public class HomeBanner implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	@SerializedName("Title")
	private String title;
	@SerializedName("SubTitle")
	private String subTitle;
	
	@SerializedName("HighResImage")
	private String imageHighResolution;
	@SerializedName("LowResImage")
	private String imageLowResolution;
	
	@SerializedName("IncludeActionButton")
	private boolean includeActionButton;

	@SerializedName("ActionButtonText")
	private String actionButtonText;
	@SerializedName("Hyperlink")
	private String hyperlink;

	@SerializedName("NavigationInNormalWebView")
	private boolean navigationInNormalWebView;
	
	public String getTitle() {
		return title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public String getHyperlink() {
		return hyperlink;
	}
	public String getImageHighResolution() {
		return imageHighResolution;
	}
	public String getImageLowResolution() {
		return imageLowResolution;
	}
	
	public String getDetailImage(){
		return this.imageHighResolution;
	}
	public String getId() {
		return id;
	}
	public boolean isIncludeActionButton() {
		return includeActionButton;
	}
	public String getActionButtonText() {
		return actionButtonText;
	}

	public boolean isNavigationInNormalWebView() {
		return navigationInNormalWebView;
	}
}
