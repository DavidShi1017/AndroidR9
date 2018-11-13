package com.cflint.dataaccess.restservice;

import java.io.IOException;
import org.json.JSONException;
import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.GreenPointsResponse;
import com.cflint.model.OfferQuery;
import com.cflint.model.OfferResponse;
import com.cflint.model.OfferQuery.ComforClass;

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
