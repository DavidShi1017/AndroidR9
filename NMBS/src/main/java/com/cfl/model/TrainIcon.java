package com.cfl.model;

import com.google.gson.annotations.SerializedName;
import com.cfl.util.Utils;

import java.io.Serializable;

public class TrainIcon implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("BrandName")
	private String brandName;
	
	@SerializedName("LinkedHafasCodes")
	private String[] linkedHafasCodes;
	
	@SerializedName("LinkedTariffGroups")
	private String[] linkedTariffGroups;
	
	@SerializedName("LinkedTrainBrands")
	private String[] linkedTrainBrands;
	
	@SerializedName("HighResImage")
	private String imageHighResolution;
	@SerializedName("LowResImage")
	private String imageLowResolution;


	public TrainIcon(){

	}
	public String getBrandName() {
		return brandName;
	}
	public String[] getLinkedHafasCodes() {
		return linkedHafasCodes;
	}
	public String getHafasCodes() {
		return Utils.arrayToString(linkedHafasCodes);
	}

	public String[] getLinkedTariffGroups() {
		return linkedTariffGroups;
	}
	public String getTariffGroups() {
		return Utils.arrayToString(linkedTariffGroups);
	}
	public String[] getLinkedTrainBrands() {
		return linkedTrainBrands;
	}
	public String getTrainBrands() {
		return Utils.arrayToString(linkedTrainBrands);
	}
	public String getImageHighResolution() {
		return imageHighResolution;
	}
	public String getImageLowResolution() {
		return imageLowResolution;
	}

	public TrainIcon(String brandName, String linkedHafasCodes, String linkedTariffGroups, String linkedTrainBrands, String imageHighResolution, String imageLowResolution){
		this.brandName = brandName;
		this.linkedHafasCodes = Utils.stringToArray(linkedHafasCodes);
		this.linkedTariffGroups = Utils.stringToArray(linkedTariffGroups);
		this.linkedTrainBrands = Utils.stringToArray(linkedTrainBrands);
		this.imageHighResolution = imageHighResolution;
		this.imageLowResolution = imageLowResolution;
	}
	public String getIcon(int deviceDensity){
		//if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.imageHighResolution;
		//}else{
		//	return this.imageLowResolution;
		//}
	}
}
