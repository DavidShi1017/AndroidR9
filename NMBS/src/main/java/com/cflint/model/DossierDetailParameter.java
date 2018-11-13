package com.cflint.model;

import com.google.gson.annotations.SerializedName;
import com.cflint.model.validation.IInsuranceAndDeliveryMethodFeedback;

import java.io.Serializable;

/**
 * Dossier Parameter, This is the wrapper containing the different parts  
 *@author: Tony
 */
public class DossierDetailParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("TrainSelectionParameter")
	private String dnr;
	private String email  ;

	public DossierDetailParameter(String dnr, String email){
		this.dnr = dnr;
		this.email = email;
	}

	public String getDnr() {
		return dnr;
	}

	public String getEmail() {
		return email;
	}
}
