package com.cflint.model;

import java.io.Serializable;
import java.util.Date;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.cflint.model.validation.ILoginFeedback;
import com.cflint.model.validation.IRegisterFeedback;
import com.cflint.util.Utils;

/**
 * Data model implementation record some info for user input
 * in RegisterActivity.
 */
public class Account implements Serializable{

	private static final long serialVersionUID = -1426972747024532699L;
	
	@Expose @SerializedName("EmailAddress")
	private String emailAddress;
	
	@Expose @SerializedName("Password")
	private String password;
	
	private String confirmPassword;
	
	@Expose @SerializedName("Salutation")
	private String salutation;
	
	@Expose @SerializedName("FirstName")
	private String firstName;
	
	@Expose @SerializedName("LastName")
	private String lastName;
	
	@Expose @SerializedName("DateOfBirth")
	private Date dateOfBirth;
	
	@Expose @SerializedName("Language")
	private String language;
	
	@Expose @SerializedName("TelephoneNumber")
	private String telephoneNumber;
	
	@Expose @SerializedName("Mobile")
	private String mobile;
	
	@Expose @SerializedName("Street")
	private String street;
	
	@Expose @SerializedName("HouseNumber")
	private String houseNumber;
	
	@Expose @SerializedName("PostBox")
	private String postBox;
	
	@Expose @SerializedName("ZipCode")
	private String zipCode;
	
	@Expose @SerializedName("City")
	private String city;
	
	@Expose @SerializedName("Country")
	private String country;
	ILoginFeedback loginFeedback;
	IRegisterFeedback registerFeedback;
	
	public Account() {
		super();
	}
	
	public enum RegisterFeedBackTypes {
	   EMPTYEMAILADDRESS,
       EMPTYPASSWORD,
       EMPTYFIRSTNAME,
       EMPTYLASTNAME,
       EMPTYTITLE,
       EMPTYCONFIRMPASSWORD,
       DIFFERENTPASSWORD,
       WRONGEMAILADDRESS,
       WRONGPASSWORD,
       REGISTERCORRECT
	}
	
	public Account(String emailAddress, String password,String confirmPassword, String salutation,
			String firstName, String lastName, Date dateOfBirth,
			String language, String telephoneNumber, String mobile,
			String street, String houseNumber, String postBox, String zipCode,
			String city, String country) {
		super();
		this.emailAddress = emailAddress;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.language = language;
		this.telephoneNumber = telephoneNumber;
		this.mobile = mobile;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postBox = postBox;
		this.zipCode = zipCode;
		this.city = city;
		this.country = country;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	public String getPassword() {
		return password;
	}
	public String getSalutation() {
		return salutation;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public String getLanguage() {
		return language;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	public String getMobile() {
		return mobile;
	}
	public String getStreet() {
		return street;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public String getPostBox() {
		return postBox;
	}
	public String getZipCode() {
		return zipCode;
	}
	public String getCity() {
		return city;
	}
	public String getCountry() {
		return country;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public RegisterFeedBackTypes validateLogin() {
		
		if (isEmptyText(emailAddress)) {
			return RegisterFeedBackTypes.EMPTYEMAILADDRESS;
		}else if (!Utils.checkEmailPattern(emailAddress)) {
			return RegisterFeedBackTypes.WRONGEMAILADDRESS;
		}
		if (isEmptyText(password)) {
			return RegisterFeedBackTypes.EMPTYPASSWORD;
		}

		return RegisterFeedBackTypes.REGISTERCORRECT;
	}
	
	public RegisterFeedBackTypes validateRegister(){
		
		if(isEmptyText(emailAddress)){
			return RegisterFeedBackTypes.EMPTYEMAILADDRESS;
		}
		if(!Utils.checkEmailPattern(emailAddress)){
			return RegisterFeedBackTypes.WRONGEMAILADDRESS;
		}
		if(isEmptyText(password)){
			return RegisterFeedBackTypes.EMPTYPASSWORD;
		}	
		if(isEmptyText(confirmPassword)){
			return RegisterFeedBackTypes.EMPTYCONFIRMPASSWORD;
		}
		if(!Utils.checkPasswordPattern(password)){
			return RegisterFeedBackTypes.WRONGPASSWORD;
		}
		if(isDifferentPassword()){
			return RegisterFeedBackTypes.DIFFERENTPASSWORD;
		}
		if(isEmptyText(lastName)){
			return RegisterFeedBackTypes.EMPTYLASTNAME;
		}
		if(isEmptyText(firstName)){
			return RegisterFeedBackTypes.EMPTYFIRSTNAME;
		}		
		if(isEmptyText(salutation)){
			return RegisterFeedBackTypes.EMPTYTITLE;
		}		
		return RegisterFeedBackTypes.REGISTERCORRECT;
	}
	
	
	public void setILoginFeedbacek(
			ILoginFeedback loginFeedback) {
		this.loginFeedback = loginFeedback;
	}

	public void clearILoginFeedbacek() {
		this.loginFeedback = null;
	}
	
	public void setIRegisterFeedback(
			IRegisterFeedback registerFeedback) {
		this.registerFeedback = registerFeedback;
	}

	public void clearIRegisterFeedback() {
		this.registerFeedback = null;
	}
	
	private boolean isEmptyText(String str) {
		if ("".equals(str) || str == null) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isDifferentPassword() {
		if (password.equals(confirmPassword)) {
			return false;
		} else {
			return true;
		}
	}
	
}
