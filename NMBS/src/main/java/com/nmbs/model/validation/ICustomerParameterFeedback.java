package com.nmbs.model.validation;

import java.io.Serializable;


import com.nmbs.model.CustomerParameter.CustomerParameterFeedbackTypes;



public interface ICustomerParameterFeedback extends Serializable{

	public void validationCustomerParameterFeedback(CustomerParameterFeedbackTypes validationFeedback);
	
}
