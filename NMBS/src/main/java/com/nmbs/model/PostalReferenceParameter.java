package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Postal Reference Parameter 
 *@author: Tony
 */
public class PostalReferenceParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("FirstName")
	private String firstName;
	@SerializedName("LastName")
	private String lastName;
	@SerializedName("Street")
	private String street;
	@SerializedName("Number")
	private String number;
	@SerializedName("Box")
	private String box;
	@SerializedName("Zip")
	private String zip;
	@SerializedName("City")
	private String city;
	@SerializedName("Country")
	private String country;
	
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getStreet() {
		return street;
	}
	public String getNumber() {
		return number;
	}
	public String getBox() {
		return box;
	}
	public String getZip() {
		return zip;
	}
	public String getCity() {
		return city;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public void setBox(String box) {
		this.box = box;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountry() {
		return country;
	}
	public PostalReferenceParameter() {

	}
		
	
	public enum PostalReferenceParameterFeedbackTypes {
		   
	       EMPTY_FIRSTNAME,
	       EMPTY_LASTNAME,	       
	       EMPTY_STREET,
	       EMPTY_NUMBER,
	       //EMPTY_BOX,
	       EMPTY_ZIP,
	       EMPTY_CITY,
	       EMPTY_COUNTRY,	
	       INVALID_NAME,
	       CORRECT
		}
	
	public PostalReferenceParameterFeedbackTypes validatePostalReferenceParameter() {
		if("".equals(firstName)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_FIRSTNAME;
		}	
		if("".equals(lastName)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_LASTNAME;
		}	
		if("".equals(street)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_STREET;
		}	
		if("".equals(number)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_NUMBER;
		}	
		/*if("".equals(box)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_BOX;
		}	*/
		if("".equals(zip)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_ZIP;
		}	
		if("".equals(city)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_CITY;
		}	
		if("".equals(country)){
			return PostalReferenceParameterFeedbackTypes.EMPTY_COUNTRY;
		}	
		if (firstName.length() < 2 || lastName.length() < 2) {
			return PostalReferenceParameterFeedbackTypes.INVALID_NAME;
		}
		
		return PostalReferenceParameterFeedbackTypes.CORRECT;
	}
}
