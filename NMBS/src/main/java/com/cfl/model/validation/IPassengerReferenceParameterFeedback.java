package com.cfl.model.validation;

import java.io.Serializable;


import com.cfl.model.PassengerReferenceParameter.PassengerReferenceParameterFeedbackTypes;


public interface IPassengerReferenceParameterFeedback extends Serializable{

	public void validationPassengerReferenceParameterFeedback(PassengerReferenceParameterFeedbackTypes validationFeedback);
	
}
