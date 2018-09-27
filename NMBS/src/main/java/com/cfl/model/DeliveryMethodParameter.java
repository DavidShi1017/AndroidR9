package com.cfl.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.cfl.model.PassengerReferenceParameter.OnFocusGotListener;
import com.cfl.model.PassengerReferenceParameter.PassengerReferenceParameterFeedbackTypes;
import com.cfl.model.validation.ISataionReferenceFeedback;

/**
 * The DeliveryMethodParameter contains all fields required for selecting a delivery method 
 *@author: Tony
 */
public class DeliveryMethodParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String method ;
	
	@SerializedName("StationInfo")
	private StationReferenceParameter stationInfo  ;
	private PostalReferenceParameter postalInfo ;
	private List<PassengerReferenceParameter> passengers  ;
	
	
	@SuppressWarnings("unused")
	private ISataionReferenceFeedback iSataionReferenceFeedback;
	
	public enum StationReferenceRequestFeedbackTypes {
		CORRECT, EMPTY_STATION_REFERENCE
	}
	
	public enum PostalReferenceRequestFeedbackTypes {
		CORRECT, EMPTY_POSTAL_REFERENCE
	}
	
	public void setIOfferRequestFeedback(
			ISataionReferenceFeedback iSataionReferenceFeedback) {
		this.iSataionReferenceFeedback = iSataionReferenceFeedback;
	}
	
	public void clearISataionReferenceFeedback() {
		iSataionReferenceFeedback = null;
	}
	
	public boolean add(PassengerReferenceParameter object) {
		return passengers.add(object);
	}
	public String getMethod() {
		return method;
	}
	public StationReferenceParameter getStationInfo() {
		return stationInfo;
	}
	public PostalReferenceParameter getPostalInfo() {
		return postalInfo;
	}
	public List<PassengerReferenceParameter> getPassengers() {
		return passengers;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public void setStationInfo(StationReferenceParameter stationInfo) {
		this.stationInfo = stationInfo;
	}

	public void setPostalInfo(PostalReferenceParameter postalInfo) {
		this.postalInfo = postalInfo;
	}

	public void setPassengers(List<PassengerReferenceParameter> passengers) {
		this.passengers = passengers;
	}

	
	
	public StationReferenceRequestFeedbackTypes validateSataionReferenceFeedback() {
		if(stationInfo == null){
			return StationReferenceRequestFeedbackTypes.EMPTY_STATION_REFERENCE;
		}
		if(stationInfo.getCode().equals("")){
			return StationReferenceRequestFeedbackTypes.EMPTY_STATION_REFERENCE;
		}
		return StationReferenceRequestFeedbackTypes.CORRECT;
	}
	
	public PassengerReferenceParameterFeedbackTypes validateFtpCardPassengerReferenceParameter(
			List<PassengerReferenceParameter> passengerReferenceParameters, OnFocusGotListener focusGotListener) {
		
		if (passengerReferenceParameters != null) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (int i = 0; i < passengerReferenceParameters.size(); i++) {
				if (map.containsKey(passengerReferenceParameters.get(i).getFtpCard())) {
					focusGotListener.setOnFocusGotListener(i);
					return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeFTPCardDup;
				}
				map.put(passengerReferenceParameters.get(i).getFtpCard(), passengerReferenceParameters.get(i).getFtpCard());
			}
		}

		return PassengerReferenceParameterFeedbackTypes.CORRECT;
	}
}
