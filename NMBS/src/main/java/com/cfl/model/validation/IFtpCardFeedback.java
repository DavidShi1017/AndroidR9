package com.cfl.model.validation;

import java.io.Serializable;


import com.cfl.model.PartyMember.FtpCardFeedbackTypes;



public interface IFtpCardFeedback extends Serializable{

	public void validationFtpCardFeedback(FtpCardFeedbackTypes validationFeedback);
	
}
