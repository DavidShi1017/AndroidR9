package com.nmbs.model.validation;

import java.io.Serializable;


import com.nmbs.model.CorporateCard.CorporateCardFeedBackTypes;




public interface ICorporateCardFeedBack extends Serializable{

	public void validationCorporateCardFeedback(CorporateCardFeedBackTypes validationFeedback);
	
}
