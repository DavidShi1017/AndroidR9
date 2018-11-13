package com.cflint.model.validation;

import com.cflint.model.Account.RegisterFeedBackTypes;

public interface IRegisterFeedback {

	public void validationFeedback(RegisterFeedBackTypes registerFeedback);
	//public void validationHttpStatusFeedback(HttpStatusCodes httpStatusCodes);
	
}
