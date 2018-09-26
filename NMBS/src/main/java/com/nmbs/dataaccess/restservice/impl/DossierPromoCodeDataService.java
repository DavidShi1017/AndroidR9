package com.nmbs.dataaccess.restservice.impl;

import java.io.IOException;
import java.text.ParseException;


import org.json.JSONException;

import android.content.Context;


import com.nmbs.R;
import com.nmbs.dataaccess.converters.DossierResponseConverter;
import com.nmbs.dataaccess.converters.CustomErrorMessager;

import com.nmbs.dataaccess.restservice.IDossierPromoCodeDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;

import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RefreshConirmationError;
import com.nmbs.exceptions.RequestFail;

import com.nmbs.model.DossierResponse;

import com.nmbs.model.PromoParameter;

import com.nmbs.services.impl.DossierService;

import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.ObjectToJsonUtils;

public class DossierPromoCodeDataService extends CustomErrorMessager implements IDossierPromoCodeDataService {
	
	//private static final String TAG = DossierDataService.class.getSimpleName();
	


	public DossierResponse executePromoCode(PromoParameter promoParameter, Context context,String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,ParseException, NoTicket, ConnectionError, BookingTimeOutError,
			RefreshConirmationError, CustomError {
		
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		DossierResponse dossierResponse = null;
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		String postJson = ObjectToJsonUtils.getPostPromoCodeJson(promoParameter);;
		
		String urlStringOfCreateDossier = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID;
		String responseSecond = httpRestServiceCaller.executeHTTPRequest(context, postJson, urlStringOfCreateDossier, 
				languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 180000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		//Log.i(TAG, "responseSecond is? : " + responseSecond);
		dossierResponse = dossierResponseConverter.parseDossier(responseSecond);
		super.throwCustomErrorMessage(dossierResponse, context, false);
		return dossierResponse;		
	}
	
}
