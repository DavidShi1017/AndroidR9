package com.nmbs.model;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.nmbs.model.OfferQuery.ComforClass;
import com.nmbs.model.SeatLocationForOD.Direction;
import com.nmbs.util.Utils;



public class Order implements Serializable {


	private static final long serialVersionUID = 1L;
	private String trainType;
	private int personNumber;
	private String trainNr;
	private int orderState;
	private String DNR;
	private String originStationName;
	private String originCode;
	private String destinationStationName;
	private String destinationCode;
	private Date departureDate;
	private String pnr;
	private String dossierGUID;
	private String travelSegmentID;
	private boolean includesEBS;
	private ComforClass travelclass;
	private Direction direction;
	private boolean hasDepartureTime;
	private Date sortDepartureDate;
	private boolean isCorrupted;
	private Date sortFirstChildDepartureDate;

	private String refundable;	

	private String exchangeable;
	
	private String rulfillmentFailed;
	
	private boolean hasDuplicatedStationboard;
	private String duplicatedStationboardId;
	 
	public enum OrderParameterFeedbackTypes{
		CORRECT, EMPTY_EMAIL, EMPTY_DNR, DNR_INCORRECT, EMAIL_INCORRECT
	}
	
	private String email;
	
	public String getPnr() {
		if(pnr != null){
			return pnr.trim();
		}else{
			return pnr;
		}
		
	}
	public String getTrainType() {
		return trainType;
	}
	public int getPersonNumber() {
		return personNumber;
	}
	public String getTrainNr() {
		return trainNr;
	}
	public int getOrderState() {
		
		return orderState;
	}
	public String getDNR() {
		return DNR;
	}
	public String getOrigin() {
		return originStationName;
	}
	public String getDestination() {
		
		return destinationStationName == ""? destinationCode: destinationStationName;
	}
	public Date getDepartureDate() {
		return departureDate;
	}
	public void setOrderState(int orderState) {
		this.orderState = orderState;
	}
	public String getOriginCode() {
		return originCode;
	}
	public String getDestinationCode() {
		return destinationCode;
	}
	public Order(String trainType, int personNumber, 
			String trainNr, int orderState, String DNR, String originStationName, String originCode, String destinationStationName, 
			String destinationCode, String pnr, Date departureDate, String dossierGUID, String travelSegmentID, 
			boolean includesEBS, ComforClass travelclass, Direction direction, boolean hasDepartureTime, 
			Date sortDepartureDate, Date sortFirstChildDepartureDate, boolean isCorrupted, String email, String 
			refundable, String exchangeable, String rulfillmentFailed, boolean hasDuplicatedStationboard, String duplicatedStationboardId){
		
		this.trainType = trainType;
		this.personNumber = personNumber;
		this.trainNr = trainNr;
		this.orderState = orderState;
		this.DNR = DNR;
		this.originStationName = originStationName;
		this.originCode = originCode;
		this.destinationStationName = destinationStationName;
		this.destinationCode = destinationCode;
		this.departureDate = departureDate;
		this.pnr = pnr;
		this.dossierGUID = dossierGUID;
		this.travelSegmentID = travelSegmentID;
		this.includesEBS = includesEBS;
		this.travelclass = travelclass;
		this.direction = direction;
		this.hasDepartureTime = hasDepartureTime;
		this.sortDepartureDate = sortDepartureDate;
		this.sortFirstChildDepartureDate = sortFirstChildDepartureDate;
		this.hasDuplicatedStationboard = hasDuplicatedStationboard;
		this.duplicatedStationboardId = duplicatedStationboardId;
		this.isCorrupted = isCorrupted;
		this.email = email;
		this.refundable = refundable;
		this.exchangeable = exchangeable;
		this.rulfillmentFailed = rulfillmentFailed;
	}
	public int describeContents() {		
		return 0;
	}
	public String getDossierGUID() {
		return dossierGUID;
	}
	public String getTravelSegmentID() {
		return travelSegmentID;
	}
	public boolean isIncludesEBS() {
		return includesEBS;
	}
	public ComforClass getTravelclass() {
		return travelclass;
	}
	public Direction getDirection() {
		
		return direction;
	}
	public boolean isHasDepartureTime() {
		return hasDepartureTime;
	}
	public Date getSortDepartureDate() {
		return sortDepartureDate;
	}
	public void setSortDepartureDate(Date sortDepartureDate) {
		this.sortDepartureDate = sortDepartureDate;
	}
	public boolean isCorrupted() {
		return isCorrupted;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public OrderParameterFeedbackTypes validateParameter(){
		
		
		if (DNR == null || StringUtils.isEmpty(DNR)) {
			return OrderParameterFeedbackTypes.EMPTY_DNR;
		}else {
			Pattern dnrPattern = Pattern.compile("^[a-zA-Z]{7}");
			if (!dnrPattern.matcher(DNR).matches()) {
				return OrderParameterFeedbackTypes.DNR_INCORRECT;
			}
		}
		
		if (email == null || StringUtils.isEmpty(email)) {
			return OrderParameterFeedbackTypes.EMPTY_EMAIL;
		}
		if (!Utils.checkEmailPattern(email)) {
			return OrderParameterFeedbackTypes.EMAIL_INCORRECT;
		}
			
		return OrderParameterFeedbackTypes.CORRECT;
	}
	public String getRefundable() {
		return refundable;
	}
	public String getExchangeable() {
		return exchangeable;
	}
	public String getRulfillmentFailed() {
		return rulfillmentFailed;
	}
	public void setRulfillmentFailed(String rulfillmentFailed) {
		this.rulfillmentFailed = rulfillmentFailed;
	}
	public Date getSortFirstChildDepartureDate() {
		return sortFirstChildDepartureDate;
	}
	public boolean isHasDuplicatedStationboard() {
		return hasDuplicatedStationboard;
	}
	public void setHasDuplicatedStationboard(boolean hasDuplicatedStationboard) {
		this.hasDuplicatedStationboard = hasDuplicatedStationboard;
	}
	public String getDuplicatedStationboardId() {
		return duplicatedStationboardId;
	}
	public void setDuplicatedStationboardId(String duplicatedStationboardId) {
		this.duplicatedStationboardId = duplicatedStationboardId;
	}
	
	
	
}
