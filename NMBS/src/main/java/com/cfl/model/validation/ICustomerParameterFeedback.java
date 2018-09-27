package com.cfl.model.validation;

import java.io.Serializable;


import com.cfl.model.CustomerParameter.CustomerParameterFeedbackTypes;



public interface ICustomerParameterFeedback extends Serializable{

	public void validationCustomerParameterFeedback(CustomerParameterFeedbackTypes validationFeedback);
	
}
