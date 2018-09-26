package com.nmbs.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * The CityDetailResponse class contains the actual 
 * response payload sent by the server when a client application 
 * requests for the detail information on a city.
 * @author DAVID	
 *
 */
public class HomeBannerResponse extends RestResponse implements Serializable{

	
	private static final long serialVersionUID = 1L;
	@SerializedName("MobileBanners")
	private List<HomeBanner> homeBanners;
	/*public HomeBanner getHomeBanner() {
		return homeBanner;
	}*/
	public List<HomeBanner> getHomeBanners() {
		return homeBanners;
	}

	
}
