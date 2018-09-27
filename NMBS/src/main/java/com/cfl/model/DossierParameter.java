package com.cfl.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import com.cfl.model.PassengerReferenceParameter.OnFocusGotListener;
import com.cfl.model.PassengerReferenceParameter.PassengerReferenceParameterFeedbackTypes;
import com.cfl.model.validation.IInsuranceAndDeliveryMethodFeedback;

/**
 * Dossier Parameter, This is the wrapper containing the different parts  
 *@author: Tony
 */
public class DossierParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("TrainSelectionParameter")
	private TrainSelectionParameter trainSelectionParameter;
	
	private boolean insurance  ;
	private DeliveryMethodParameter deliveryMethod;
	private CustomerParameter customer  ;
	private PaymentMethodParameter paymentMethod ;
	
	@SuppressWarnings("unused")
	transient private IInsuranceAndDeliveryMethodFeedback insuranceAndDeliveryMethodFeedback;
	
	public enum InsuranceAndDeliveryMethodRequestFeedbackTypes {
		CORRECT, EMPTY_DELIVERY_METHOD
	}
	
	public enum CustomerAndPaymentMethodRequestFeedbackTypes {
		CORRECT, EMPTY_CUSTOMER, EMPTY_PAYMENT_METHOD
	}
	public void setIInsuranceAndDeliveryMethodFeedback(
			IInsuranceAndDeliveryMethodFeedback insuranceAndDeliveryMethodFeedback) {
		this.insuranceAndDeliveryMethodFeedback = insuranceAndDeliveryMethodFeedback;
	}
	
	public void clearInsuranceAndDeliveryMethodFeedback() {
		insuranceAndDeliveryMethodFeedback = null;
	}
	
	public TrainSelectionParameter getTrainSelection() {
		return trainSelectionParameter;
	}
	public void setTrainSelection(TrainSelectionParameter trainSelection) {
		this.trainSelectionParameter = trainSelection;
	}

	public boolean isInsurance() {
		return insurance;
	}
	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}
	public DeliveryMethodParameter getDeliveryMethod() {
		return deliveryMethod;
	}
	public void setDeliveryMethod(DeliveryMethodParameter deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	public CustomerParameter getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerParameter customer) {
		this.customer = customer;
	}
	public PaymentMethodParameter getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(PaymentMethodParameter paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public InsuranceAndDeliveryMethodRequestFeedbackTypes validateInsuranceAndDeliveryMethod() {
		if(deliveryMethod == null){
			return InsuranceAndDeliveryMethodRequestFeedbackTypes.EMPTY_DELIVERY_METHOD;
		}
		return InsuranceAndDeliveryMethodRequestFeedbackTypes.CORRECT;
	}
	
	
}
