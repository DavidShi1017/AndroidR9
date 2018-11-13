package com.cflint.model.validation;

import java.io.Serializable;


import com.cflint.model.PartyMember.FtpCardFeedbackTypes;



public interface IFtpCardFeedback extends Serializable{

	public void validationFtpCardFeedback(FtpCardFeedbackTypes validationFeedback);
	
}
