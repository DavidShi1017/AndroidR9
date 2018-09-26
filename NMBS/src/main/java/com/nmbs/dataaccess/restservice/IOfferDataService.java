package com.nmbs.dataaccess.restservice;

import java.io.IOException;
import org.json.JSONException;
import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.GreenPointsResponse;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.OfferResponse;
import com.nmbs.model.OfferQuery.ComforClass;

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
