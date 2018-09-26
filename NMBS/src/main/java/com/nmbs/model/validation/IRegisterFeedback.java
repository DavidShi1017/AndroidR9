package com.nmbs.model.validation;

import com.nmbs.model.Account.RegisterFeedBackTypes;

public interface IRegisterFeedback {

	public void validationFeedback(RegisterFeedBackTypes registerFeedback);
	//public void validationHttpStatusFeedback(HttpStatusCodes httpStatusCodes);
	
}
