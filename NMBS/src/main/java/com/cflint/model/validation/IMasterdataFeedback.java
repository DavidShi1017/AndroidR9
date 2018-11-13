package com.cflint.model.validation;

import com.cflint.services.impl.MasterService.RequiredMasterDataMissingType;



public interface IMasterdataFeedback {
	
	public void validationFeedback(RequiredMasterDataMissingType requiredMasterDataMissingType);
}
