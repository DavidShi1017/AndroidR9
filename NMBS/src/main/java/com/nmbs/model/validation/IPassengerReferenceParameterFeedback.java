package com.nmbs.model.validation;

import java.io.Serializable;


import com.nmbs.model.PassengerReferenceParameter.PassengerReferenceParameterFeedbackTypes;


public interface IPassengerReferenceParameterFeedback extends Serializable{

	public void validationPassengerReferenceParameterFeedback(PassengerReferenceParameterFeedbackTypes validationFeedback);
	
}
