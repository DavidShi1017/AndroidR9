package com.nmbs.model.validation;

import java.io.Serializable;


import com.nmbs.model.PartyMember.FtpCardFeedbackTypes;



public interface IFtpCardFeedback extends Serializable{

	public void validationFtpCardFeedback(FtpCardFeedbackTypes validationFeedback);
	
}
