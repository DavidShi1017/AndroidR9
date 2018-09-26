package com.nmbs.dataaccess.restservice;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;

import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RefreshConirmationError;
import com.nmbs.exceptions.RequestFail;

import com.nmbs.model.DossierResponse;
import com.nmbs.model.PromoParameter;


public interface IDossierPromoCodeDataService {

	public DossierResponse executePromoCode(PromoParameter promoParameter, Context context,String languageBeforSetting) throws RequestFail, IOException,
		InvalidJsonError, NumberFormatException, JSONException,ParseException, NoTicket, ConnectionError, BookingTimeOutError,
		RefreshConirmationError, CustomError;
}
