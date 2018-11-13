package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CheckAppUpdate implements Serializable {
	private static final long serialVersionUID = 1L;
	@SerializedName("IsUpToDate")
	private boolean isUpToDate;
	@SerializedName("CurrentVersion")
	private String version;
	@SerializedName("ReleaseNotes")
	private List<ReleaseNote> ReleaseNotes = new ArrayList<ReleaseNote>(); 
	public CheckAppUpdate(String version, boolean isUpToDate, List<ReleaseNote> ReleaseNotes){
		this.version = version;
		this.isUpToDate = isUpToDate;
		this.ReleaseNotes = ReleaseNotes;
	}
	public boolean isUpToDate() {
		return isUpToDate;
	}
	public List<ReleaseNote> getReleaseNotes() {
		return ReleaseNotes;
	}
	
	public String getVersion(){
		return this.version;
	}
	

}
