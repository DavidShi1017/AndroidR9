package com.cflint.model.validation;

import java.io.Serializable;


import com.cflint.model.CorporateCard.CorporateCardFeedBackTypes;




public interface ICorporateCardFeedBack extends Serializable{

	public void validationCorporateCardFeedback(CorporateCardFeedBackTypes validationFeedback);
	
}
