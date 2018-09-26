package com.nmbs.model.validation;

import java.io.Serializable;

import com.nmbs.model.DossierParameter.InsuranceAndDeliveryMethodRequestFeedbackTypes;


public interface IInsuranceAndDeliveryMethodFeedback extends Serializable{

	public void validationInsuranceAndDeliveryMethodFeedback(InsuranceAndDeliveryMethodRequestFeedbackTypes validationFeedback);
	
}
