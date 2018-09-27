package com.cfl.model.validation;

import java.io.Serializable;


import com.cfl.model.CorporateCard.CorporateCardFeedBackTypes;




public interface ICorporateCardFeedBack extends Serializable{

	public void validationCorporateCardFeedback(CorporateCardFeedBackTypes validationFeedback);
	
}
