package com.cflint.dataaccess.restservice.impl;

import java.io.IOException;
import java.text.ParseException;


import org.json.JSONException;

import android.content.Context;


import com.cflint.R;
import com.cflint.dataaccess.converters.DossierResponseConverter;
import com.cflint.dataaccess.converters.CustomErrorMessager;

import com.cflint.dataaccess.restservice.IDossierPromoCodeDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;

import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RefreshConirmationError;
import com.cflint.exceptions.RequestFail;

import com.cflint.model.DossierResponse;

import com.cflint.model.PromoParameter;

import com.cflint.services.impl.DossierService;

import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.ObjectToJsonUtils;

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
