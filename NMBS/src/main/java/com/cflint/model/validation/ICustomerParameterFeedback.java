package com.cflint.model.validation;

import java.io.Serializable;


import com.cflint.model.CustomerParameter.CustomerParameterFeedbackTypes;



public interface ICustomerParameterFeedback extends Serializable{

	public void validationCustomerParameterFeedback(CustomerParameterFeedbackTypes validationFeedback);
	
}
