package com.cflint.model.validation;

import java.io.Serializable;

import com.cflint.model.DossierParameter.InsuranceAndDeliveryMethodRequestFeedbackTypes;


public interface IInsuranceAndDeliveryMethodFeedback extends Serializable{

	public void validationInsuranceAndDeliveryMethodFeedback(InsuranceAndDeliveryMethodRequestFeedbackTypes validationFeedback);
	
}
