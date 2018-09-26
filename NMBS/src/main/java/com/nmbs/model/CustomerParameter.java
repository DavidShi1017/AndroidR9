package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.nmbs.model.validation.ICustomerParameterFeedback;
import com.nmbs.util.Utils;


/**
 * Customer Parameter 
 *@author: Tony
 */
public class CustomerParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String gender ;
	private String salutation;
	private String firstName;
	private String lastName ;
	private String email ;
	private List<String> additionalEmails = new ArrayList<String>(); 
	private String telephoneNumberForSMS; 
	private String customPaymentServiceProviderTemplate = "/ogonetemplates/PaymentPage_1_Android.htm";
	private List<PassengerReferenceParameter> passengerInfo;
	
	@SuppressWarnings("unused")
	transient private ICustomerParameterFeedback customerParameterFeedback;
	
	private transient  OnFocusGotListener focusGotListener;
	
	public void setICustomerParameterFeedback(
			ICustomerParameterFeedback customerParameterFeedback) {
		this.customerParameterFeedback = customerParameterFeedback;
	}
	
	public void clearICustomerParameterFeedback() {
		customerParameterFeedback = null;
	}
	
	public enum CustomerParameterFeedbackTypes {
		CORRECT, EMPTY_GENDER, EMPTY_SALUTATION, EMPTY_FIRSTNAME, EMPTY_LASTNAME, 
		EMPTY_EMAIL, EMPTY_FTPCARD, WRONGE_MAILADDRESS, INVALID_NAME, INVALID_PHONENUMBER, CustomerParameterFeedbackTypeFirstNameInvalid,
		CustomerParameterFeedbackTypeLastNameInvalid, CustomerParameterFeedbackTypeEmailDup
	}
	
	public boolean add(PassengerReferenceParameter object) {
		return passengerInfo.add(object);
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassengerInfo(List<PassengerReferenceParameter> passengerInfo) {
		this.passengerInfo = passengerInfo;
	}
	public String getGender() {
		return gender;
	}

	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}

	
	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public CustomerParameter(String gender, 
			String firstName, String lastName, String email,
			List<String> additionalEmails, String telephoneNumberForSMS,
			List<PassengerReferenceParameter> passengerInfo) {
		super();
		this.gender = gender;

		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.additionalEmails = additionalEmails;
		this.passengerInfo = passengerInfo;
		this.telephoneNumberForSMS = telephoneNumberForSMS;
	}
	
	public CustomerParameter() {
			
	}
	
	public List<PassengerReferenceParameter> getPassengerInfo() {
		return passengerInfo;
	}
	
	
	
	public String getTelephoneNumberForSMS() {
		return telephoneNumberForSMS;
	}

	public String getCustomPaymentServiceProviderTemplate() {
		System.out.println("customPaymentServiceProviderTemplate=========" + customPaymentServiceProviderTemplate);
		return customPaymentServiceProviderTemplate;
	}

	public void setTelephoneNumberForSMS(String telephoneNumberForSMS) {
		this.telephoneNumberForSMS = telephoneNumberForSMS;
	}

	public CustomerParameterFeedbackTypes validateCustomerParameter() {
/*		if("".equals(gender)){
			return CustomerParameterFeedbackTypes.EMPTY_GENDER;
		}*/
/*		if("".equals(salutation)){
			return CustomerParameterFeedbackTypes.EMPTY_SALUTATION;
		}*/
		if("".equals(firstName)){
			return CustomerParameterFeedbackTypes.EMPTY_FIRSTNAME;
		}
		if("".equals(lastName)){
			return CustomerParameterFeedbackTypes.EMPTY_LASTNAME;
		}
		

		
		if (firstName.length() < 2 || firstName.length() > 15) {
						
			return CustomerParameterFeedbackTypes.CustomerParameterFeedbackTypeFirstNameInvalid;			
			
		}
		
		if (lastName.length() < 2 || lastName.length() > 28) {

			return CustomerParameterFeedbackTypes.CustomerParameterFeedbackTypeLastNameInvalid;
								
		}
		
		if(this.salutation == null || StringUtils.equalsIgnoreCase("", salutation)){
			
			return CustomerParameterFeedbackTypes.EMPTY_SALUTATION;
		}		
		if("".equals(email)){
			return CustomerParameterFeedbackTypes.EMPTY_EMAIL;
		}
		if (!Utils.checkEmailPattern(email)) {
			
			return CustomerParameterFeedbackTypes.WRONGE_MAILADDRESS;
		}else {
			/*for (int i = 0; i < additionalEmails.size(); i++) {
				String emailString = additionalEmails.get(i);
				if (StringUtils.equalsIgnoreCase(emailString, email)) {
					return CustomerParameterFeedbackTypes.CustomerParameterFeedbackTypeEmailDup;
				}
			}*/
		}

		
		
		if (telephoneNumberForSMS.length() > 0 && telephoneNumberForSMS.length() < 5) {
			return CustomerParameterFeedbackTypes.INVALID_PHONENUMBER;			
		}
		
		return CustomerParameterFeedbackTypes.CORRECT;
	}

	public List<String> getAdditionalEmails() {
		return additionalEmails;
	}

	public void removeEmptyEmail(){

		List <String> filledEmails = new ArrayList<String>();
		for (String emails : additionalEmails) {
			if (emails != null && !StringUtils.isEmpty(emails)) {
				filledEmails.add(emails)		;			
			}			
		}
		additionalEmails = filledEmails;
	}
	
	public CustomerParameterFeedbackTypes validateCustomerParameterEmail(){
		
/*		Map<String, String> map = new LinkedHashMap<String, String>();
		if (email != null) {
			map.put(email, email.toLowerCase());
		}
*/
		if (additionalEmails != null) {
			for (int i = 0; i < additionalEmails.size(); i++) {
				String emailString = additionalEmails.get(i);
/*				if (emailString == null || StringUtils.isEmpty(emailString.trim())) {
					focusGotListener.setOnFocusGotListener(i);
					return CustomerParameterFeedbackTypes.EMPTY_EMAIL;
				}*/
				if (emailString != null && !StringUtils.isEmpty(emailString)) {
					if (!Utils.checkEmailPattern(emailString)) {
						focusGotListener.setOnFocusGotListener(i);
						return CustomerParameterFeedbackTypes.WRONGE_MAILADDRESS;
					}
				}
							
				/*if (map.containsKey(emailString.toLowerCase())) {
					focusGotListener.setOnFocusGotListener(i);
					return CustomerParameterFeedbackTypes.CustomerParameterFeedbackTypeEmailDup;
				}else {
					map.put(emailString, emailString.toLowerCase());
				}	*/		
			}
		}
		
		return CustomerParameterFeedbackTypes.CORRECT;
	}
	
	public void setAdditionalEmails(List<String> additionalEmails) {
		this.additionalEmails = additionalEmails;
	}
	
	public void setOnFocusGotListener(OnFocusGotListener focusGotListener) {
		this.focusGotListener = focusGotListener;
	}

	public OnFocusGotListener getFocusGotListener() {
		return focusGotListener;
	}

	public interface OnFocusGotListener {
		
		public void setOnFocusGotListener(int position);
	}
}
