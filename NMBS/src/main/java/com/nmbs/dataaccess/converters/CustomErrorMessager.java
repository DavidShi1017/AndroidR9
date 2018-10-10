package com.nmbs.dataaccess.converters;

import android.content.Context;

import com.nmbs.R;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.log.LogUtils;
import com.nmbs.model.Message;
import com.nmbs.model.RestResponse;

import org.apache.commons.lang.StringUtils;

public class CustomErrorMessager {
	
	public void throwErrorMessage(RestResponse restResponse, Context context, String url) throws DBooking343Error,
					CustomError, DBookingNoSeatAvailableError, RequestFail{
		
		for (Message message : restResponse.getMessages()) {
			String responseErrorMessage = null;

			if (StringUtils.equalsIgnoreCase(message.getStatusCode() , "510")) {
				if (StringUtils.equalsIgnoreCase(message.getBackendErrorKey(), "DBooking_343")) {
					
					// Log.d("DossierResponseConverter", "Has DBooking_343...");
					responseErrorMessage = getErrorDescription(message);
					if(responseErrorMessage == null)
					responseErrorMessage = "";
					throw new DBooking343Error(responseErrorMessage);
					
				}
/*				if (StringUtils.equalsIgnoreCase(message.getBackendErrorKey(), "DBooking_CAT_AE001")) {
					responseErrorMessage = getErrorDescription(message);
					if(responseErrorMessage == null)
						responseErrorMessage = "";
					throw new CustomError(responseErrorMessage);
					
				}*/
				
				if (StringUtils.equalsIgnoreCase(message.getBackendErrorKey(), "DBooking_NoSeatsAvailable")) {

					responseErrorMessage = getErrorDescription(message);
					if(responseErrorMessage == null)
						responseErrorMessage = context.getString(R.string.DBooking_NoSeatsAvailable);
					throw new DBookingNoSeatAvailableError(responseErrorMessage);
					
				}
				if (StringUtils.equalsIgnoreCase(message.getBackendErrorKey(), "DBooking_InvalidFtp")) {

					responseErrorMessage = getErrorDescription(message);
					if(responseErrorMessage == null)
						responseErrorMessage = context.getString(R.string.DBooking_InvalidFtp);
					throw new CustomError(responseErrorMessage);
				}
				if (StringUtils.equalsIgnoreCase(url, context.getString(R.string.server_url_get_dossier_detail))) {
					LogUtils.e("UploadDossier", "UploadDossier error----------");
					responseErrorMessage = getErrorDescription(message);
					if(responseErrorMessage == null)
						responseErrorMessage = context.getString(R.string.general_server_unavailable);
					throw new CustomError(responseErrorMessage);
				}
				responseErrorMessage = getErrorDescription(message);
/*				if(responseErrorMessage == null)
					if (isAbortPayment) {
						responseErrorMessage = context.getString(R.string.alert_abort_payment_failed);						
					}else {
						responseErrorMessage = context.getString(R.string.alert_status_service_not_available);
					}*/
					
				throw new RequestFail();
			}		
		}
	}
	
	private String getErrorDescription(Message message){
		String responseErrorMessage = null;
		if (message.getDescription() != null && !StringUtils.isEmpty(message.getDescription())) {
			responseErrorMessage = message.getDescription();
			
		}
		return responseErrorMessage;
	}
	
	public void throwCustomErrorMessage(RestResponse restResponse, Context context, boolean isPaymentFail) throws CustomError{
		for (Message message : restResponse.getMessages()) {
			String responseErrorMessage = null;
			if (StringUtils.equalsIgnoreCase(message.getStatusCode() , "510")) {
				if (message.getDescription() != null && !StringUtils.isEmpty(message.getDescription())) {
					responseErrorMessage = message.getDescription();
					throw new CustomError(responseErrorMessage);
				}
				if (isPaymentFail) {
					responseErrorMessage = context.getString(R.string.general_server_unavailable);
				}else {
					responseErrorMessage = context.getString(R.string.summary_view_promo_code_invalid);					
				}
				
				throw new CustomError(responseErrorMessage);	
			}	
		}
	}
}
