package com.cflint.model.validation;

import java.io.Serializable;


import com.cflint.model.PassengerReferenceParameter.PassengerReferenceParameterFeedbackTypes;


public interface IPassengerReferenceParameterFeedback extends Serializable{

	public void validationPassengerReferenceParameterFeedback(PassengerReferenceParameterFeedbackTypes validationFeedback);
	
}
