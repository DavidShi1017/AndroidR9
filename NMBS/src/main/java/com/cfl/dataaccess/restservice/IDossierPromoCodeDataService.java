package com.cfl.dataaccess.restservice;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;

import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RefreshConirmationError;
import com.cfl.exceptions.RequestFail;

import com.cfl.model.DossierResponse;
import com.cfl.model.PromoParameter;


public interface IDossierPromoCodeDataService {

	public DossierResponse executePromoCode(PromoParameter promoParameter, Context context,String languageBeforSetting) throws RequestFail, IOException,
		InvalidJsonError, NumberFormatException, JSONException,ParseException, NoTicket, ConnectionError, BookingTimeOutError,
		RefreshConirmationError, CustomError;
}
