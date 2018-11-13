package com.cflint.dataaccess.restservice;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;

import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RefreshConirmationError;
import com.cflint.exceptions.RequestFail;

import com.cflint.model.DossierResponse;
import com.cflint.model.PromoParameter;


public interface IDossierPromoCodeDataService {

	public DossierResponse executePromoCode(PromoParameter promoParameter, Context context,String languageBeforSetting) throws RequestFail, IOException,
		InvalidJsonError, NumberFormatException, JSONException,ParseException, NoTicket, ConnectionError, BookingTimeOutError,
		RefreshConirmationError, CustomError;
}
