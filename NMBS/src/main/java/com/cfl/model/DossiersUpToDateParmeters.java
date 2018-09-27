package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DossiersUpToDateParmeters implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;

	@SerializedName("Elements")
	private List<DossiersUpToDateParmeter> dossiersUpToDateParmeters;


	public DossiersUpToDateParmeters(List<DossiersUpToDateParmeter> dossiersUpToDateParmeters){
		this.dossiersUpToDateParmeters = dossiersUpToDateParmeters;
	}

	public List<DossiersUpToDateParmeter> getDossiersUpToDateParmeters(){
		return this.dossiersUpToDateParmeters;
	}
}
