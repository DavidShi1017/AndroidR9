package com.cflint.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class PDF implements Serializable{
	private static final long serialVersionUID = 1L;
	@SerializedName("PDFId")
	private String pdfId;
	
	@SerializedName("PDFURL")
	private String pdfURL;

	@SerializedName("CreationTimeStamp")
	private Date creationTimeStamp;

	public String getPdfId() {
		return pdfId;
	}

	public String getPdfURL() {
		return pdfURL;
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}
}
