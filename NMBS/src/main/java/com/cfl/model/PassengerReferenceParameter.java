package com.cfl.model;

import java.io.Serializable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.cfl.util.CardUtils;



/**
 * Passenger Reference Parameter 
 *@author: Tony
 */
public class PassengerReferenceParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Id")
	private String id;
	@SerializedName("FirstName")
	private String firstName;
	@SerializedName("LastName")
	private String lastName;
	@SerializedName("FtpCard")
	private String ftpCard;
	
	private transient  OnFocusGotListener focusGotListener;
	
	@Expose(serialize = false) 
	private transient boolean isEnabledFirstName;
	@Expose(serialize = false) 
	private transient boolean isEnabledLastName;
	
	
	public enum PassengerReferenceParameterFeedbackTypes {
		CORRECT, EMPTY_FTPCARD, DossierParameterFeedbackTypeStationPickupNoFirstName, 
		DossierParameterFeedbackTypeStationPickupNoLastName, DossierParameterFeedbackTypeStationPickupNoFirstNameForPassenger1, 
		DossierParameterFeedbackTypeStationPickupNoLastNameForPassenger1, DossierParameterFeedbackTypeStationPickupNoFirstNameForPassenger2, 
		DossierParameterFeedbackTypeStationPickupNoLastNameForPassenger2, DossierParameterFeedbackTypeStationPickupIncorrectName, 
		DossierParameterFeedbackTypeStationPickupIncorrectFirstName, DossierParameterFeedbackTypeStationPickupIncorrectLastName,
		DossierParameterFeedbackTypeFTPCardIncorrect, DossierParameterFeedbackTypeFTPCardDup;
	}
	
	
	public String getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getFtpCard() {
		return ftpCard;
	}
	public PassengerReferenceParameter(String id, String firstName,
			String lastName, String ftpCard) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.ftpCard = ftpCard;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setFtpCard(String ftpCard) {
		this.ftpCard = ftpCard;
	}
	
	public PassengerReferenceParameterFeedbackTypes validateFtpCardPassengerReferenceParameter(int i) {
		System.out.println("ftpCard=====" + ftpCard);
		
		if(ftpCard == null || "".equals(ftpCard)){
			focusGotListener.setOnFocusGotListener(i);
			return PassengerReferenceParameterFeedbackTypes.EMPTY_FTPCARD;
		}else {
			if (!CardUtils.isValidFTPCard(ftpCard)) {
				focusGotListener.setOnFocusGotListener(i);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeFTPCardIncorrect;
			}
			if (!CardUtils.luhnTest(ftpCard)) {
				focusGotListener.setOnFocusGotListener(i);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeFTPCardIncorrect;
			}
		}
		
		
		return PassengerReferenceParameterFeedbackTypes.CORRECT;
	}
	
	
	
	
	public PassengerReferenceParameterFeedbackTypes validatePassengerReferenceParameter(int passengerCount, int i, boolean isEnabled) {
		
/*		System.out.println("The passengerCount index is:::" + passengerCount);*/
		System.out.println("The passenger index is:::" + i);
		System.out.println("The passenger firstName is:::" + firstName);
		System.out.println("The passenger lastName is:::" + lastName);
		if(firstName == null || "".equals(firstName)){
			//if (passengerCount == 1) {		
			focusGotListener.setOnFocusGotListener(i, true);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoFirstName;
			/*}else {
				if (i == 0) {
					
					return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoFirstNameForPassenger1;
				}else {
					return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoFirstNameForPassenger2;
				}			
			}*/
				
		}
		
		if(lastName == null || "".equals(lastName)){
			//if (passengerCount == 1) {
			focusGotListener.setOnFocusGotListener(i, false);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoLastName;
			/*} else {
				if (i == 0) {
					return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoLastNameForPassenger1;
				}else {
					return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupNoLastNameForPassenger2;
				}
			}			*/
		}
		if (firstName.length() < 2 || firstName.length() > 15) {
			System.out.println("The firstName length is:::" + firstName.length());
			
			if (!isEnabledFirstName) {
				return PassengerReferenceParameterFeedbackTypes.CORRECT;
			}else {
				focusGotListener.setOnFocusGotListener(i, true);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupIncorrectFirstName;
			}
			
		}
		
		if (lastName.length() < 2 || lastName.length() > 28) {
			System.out.println("The lastName length is:::" + lastName.length());
			if (!isEnabledLastName) {
				return PassengerReferenceParameterFeedbackTypes.CORRECT;
			}else {
				focusGotListener.setOnFocusGotListener(i, false);
				return PassengerReferenceParameterFeedbackTypes.DossierParameterFeedbackTypeStationPickupIncorrectLastName;
			}
			
		}

		return PassengerReferenceParameterFeedbackTypes.CORRECT;
	}
	public boolean isEnabledFirstName() {
		return isEnabledFirstName;
	}
	public void setEnabledFirstName(boolean isEnabledFirstName) {
		this.isEnabledFirstName = isEnabledFirstName;
	}
	public boolean isEnabledLastName() {
		return isEnabledLastName;
	}
	public void setEnabledLastName(boolean isEnabledLastName) {
		this.isEnabledLastName = isEnabledLastName;
	}
	
	public void setOnFocusGotListener(OnFocusGotListener focusGotListener) {
		this.focusGotListener = focusGotListener;
	}

	public OnFocusGotListener getFocusGotListener() {
		return focusGotListener;
	}

	public interface OnFocusGotListener {
		public void setOnFocusGotListener(int position, boolean isFirstName);
		public void setOnFocusGotListener(int position);
	}
}
