package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WizardResponse implements Serializable{

	
	private static final long serialVersionUID = 1L;
	@SerializedName("WizardItems")
	private List<WizardItem> wizardItems = new ArrayList<WizardItem>();
	public List<WizardItem> getWizards() {
		return wizardItems;
	}
	
	
}
