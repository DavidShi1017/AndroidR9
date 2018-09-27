package com.cfl.dataaccess.restservice;

import java.io.IOException;
import org.json.JSONException;
import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.GreenPointsResponse;
import com.cfl.model.OfferQuery;
import com.cfl.model.OfferResponse;
import com.cfl.model.OfferQuery.ComforClass;

/**
 * Call web service get OfferResponse.
 */
public interface IOfferDataService {
	
	/**
	 *  Call web service asynchronously and get OfferResponse.
	 *  @param OfferQuery
	 *  @param Context
	 *  @return OfferResponse
	 */
	public OfferResponse executeSearchOffer(OfferQuery offerQuery, Context context, String languageBeforSetting, 
			ComforClass comforClass) throws Exception;
	
	/**
	 * Call web service asynchronously and get CorporateCardResponse
	 * @param context
	 * @return CorporateCardResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws RequestFail
	 * @throws IOException
	 */
	public GreenPointsResponse executeCorporateCard(String greenpointsNumber,
			String language, Context context) throws InvalidJsonError,
			JSONException, TimeOutError, RequestFail, IOException, ConnectionError, BookingTimeOutError;
}
