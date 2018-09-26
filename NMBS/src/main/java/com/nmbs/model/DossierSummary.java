package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


public class DossierSummary implements Serializable {
	private static final long serialVersionUID = -1426972747024532699L;
	private String dossierId;
	private String dossierDetails;
	private String dossierDate;
	private boolean dossierPushEnabled;
	private boolean pdfSuccessfully;
	private boolean barcodeSuccessfully;
	private boolean travelSegmentAvailable;
	private boolean displayOverlay;

	private Date latestTravelDate;
	private Date earliestTravelDate;

	public DossierSummary(String dossierId, String dossierDetails, String dossierDate, boolean dossierPushEnabled,
						  boolean pdfSuccessfully, boolean barcodeSuccessfully, boolean travelSegmentAvailable,
						  boolean displayOverlay, Date latestTravelDate, Date earliestTravelDate){
		this.dossierId = dossierId;
		this.dossierDetails = dossierDetails;
		this.dossierDate = dossierDate;
		this.dossierPushEnabled = dossierPushEnabled;
		this.pdfSuccessfully = pdfSuccessfully;
		this.barcodeSuccessfully = barcodeSuccessfully;
		this.travelSegmentAvailable = travelSegmentAvailable;
		this.displayOverlay = displayOverlay;
		this.latestTravelDate = latestTravelDate;
		this.earliestTravelDate = earliestTravelDate;
	}

	public String getDossierId() {
		return dossierId;
	}

	public String getDossierDetails() {
		return dossierDetails;
	}

	public void setDossierDetails(String dossierDetails) {
		this.dossierDetails = dossierDetails;
	}

	public String getDossierDate() {
		return dossierDate;
	}

	public void setDossierDate(String dossierDate) {
		this.dossierDate = dossierDate;
	}

	public boolean isDossierPushEnabled() {
		return dossierPushEnabled;
	}

	public void setDossierPushEnabled(boolean dossierPushEnabled) {
		this.dossierPushEnabled = dossierPushEnabled;
	}

	public boolean isBarcodeSuccessfully() {
		return barcodeSuccessfully;
	}

	public void setBarcodeSuccessfully(boolean barcodeSuccessfully) {
		this.barcodeSuccessfully = barcodeSuccessfully;
	}

	public boolean isPDFSuccessfully() {

		return pdfSuccessfully;
	}

	public void setPDFSuccessfully(boolean PDFSuccessfully) {
		this.pdfSuccessfully = PDFSuccessfully;
	}

	public void setDossierId(String dossierId) {
		this.dossierId = dossierId;
	}

	public boolean isDisplayOverlay() {
		return displayOverlay;
	}

	public void setDisplayOverlay(boolean displayOverlay) {
		this.displayOverlay = displayOverlay;
	}

	public boolean isTravelSegmentAvailable() {
		return travelSegmentAvailable;
	}

	public void setTravelSegmentAvailable(boolean travelSegmentAvailable) {
		this.travelSegmentAvailable = travelSegmentAvailable;
	}

	public Date getLatestTravelDate() {
		return latestTravelDate;
	}

	public void setLatestTravelDate(Date latestTravelDate) {
		this.latestTravelDate = latestTravelDate;
	}

	public Date getEarliestTravel() {
		return earliestTravelDate;
	}

	public void setEarliestTravelDate(Date earliestTravelDate) {
		this.earliestTravelDate = earliestTravelDate;
	}
}
