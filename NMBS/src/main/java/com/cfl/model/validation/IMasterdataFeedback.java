package com.cfl.model.validation;

import com.cfl.services.impl.MasterService.RequiredMasterDataMissingType;



public interface IMasterdataFeedback {
	
	public void validationFeedback(RequiredMasterDataMissingType requiredMasterDataMissingType);
}
