package com.cflint.model;

import java.io.Serializable;
import java.util.Date;


import com.google.gson.annotations.SerializedName;
import com.cflint.util.DateUtils;

public class HomePrintTicket implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("PdfUrl")
	private String pdfUrl;
	
	@SerializedName("PdfId")
	private String PdfId;
	
	@SerializedName("CreationTimeStamp")
	private Date creationTimeStamp;
	public String getPdfUrl() {
		return pdfUrl;
	}
	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}
	
	public String getDisplayName(String DNR){
		
		String date = "";

		date = DateUtils.dateToStringNoMiddleLine(creationTimeStamp);
		
		return DNR + "-" + date;
	}
	public String getPdfId() {
		return PdfId;
	}
	
	
}
