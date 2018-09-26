package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

public class DossierDetailsResponse extends RestResponse{
	private static final long serialVersionUID = 1L;
	@SerializedName("Dossier")
	private Dossier dossier;

	public Dossier getDossier() {
		return dossier;
	}
}
