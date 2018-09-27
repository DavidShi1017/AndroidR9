package com.cfl.dataaccess.restservice.impl;

import java.io.IOException;
import java.text.ParseException;


import org.json.JSONException;

import android.content.Context;


import com.cfl.R;
import com.cfl.dataaccess.converters.DossierResponseConverter;
import com.cfl.dataaccess.converters.CustomErrorMessager;

import com.cfl.dataaccess.restservice.IDossierPromoCodeDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;

import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RefreshConirmationError;
import com.cfl.exceptions.RequestFail;

import com.cfl.model.DossierResponse;

import com.cfl.model.PromoParameter;

import com.cfl.services.impl.DossierService;

import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.ObjectToJsonUtils;

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
