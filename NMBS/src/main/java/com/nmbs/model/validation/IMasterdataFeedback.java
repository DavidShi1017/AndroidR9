package com.nmbs.model.validation;

import com.nmbs.services.impl.MasterService.RequiredMasterDataMissingType;



public interface IMasterdataFeedback {
	
	public void validationFeedback(RequiredMasterDataMissingType requiredMasterDataMissingType);
}
