package com.cfl.model.validation;

import java.io.Serializable;

import com.cfl.model.DossierParameter.InsuranceAndDeliveryMethodRequestFeedbackTypes;


public interface IInsuranceAndDeliveryMethodFeedback extends Serializable{

	public void validationInsuranceAndDeliveryMethodFeedback(InsuranceAndDeliveryMethodRequestFeedbackTypes validationFeedback);
	
}
