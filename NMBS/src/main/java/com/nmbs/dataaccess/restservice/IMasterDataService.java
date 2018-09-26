package com.nmbs.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.City;
import com.nmbs.model.CityDetailResponse;

import com.nmbs.model.CollectionItem;
import com.nmbs.model.DeliveryMethod;
import com.nmbs.model.FAQCategory;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.HomeBannerResponse;
import com.nmbs.model.MasterDataResponse;
import com.nmbs.model.OriginDestinationRule;
import com.nmbs.model.TrainIconResponse;
import com.nmbs.model.WeatherInformationResponse;
import com.nmbs.model.WizardResponse;

import com.nmbs.model.Currency;

import com.nmbs.model.Station;



import android.content.Context;

/**
 * Call web service and Support Master Data for MasterService.
 */
public interface IMasterDataService {
	
	
	
	/**
	 *  Call web service asynchronously and get Language Response. 
	 * @param context
	 * @return List<CollectionItem>
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 */
	
	public List<CollectionItem> executeLanguageCollection(Context context, String languageBeforSetting) 
			throws InvalidJsonError, JSONException, TimeOutError, ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;

	
	/**
	 * Call web service asynchronously and get Station Response.
	 * @param context
	 * @return List<Station>
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 */
	public List<Station> executeStationCollection(Context context, String languageBeforSetting, List<OriginDestinationRule> originDestinationRules) 
	throws InvalidJsonError, JSONException, TimeOutError, ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	
	public List<OriginDestinationRule> executeOriginDestinationRules(Context context, String languageBeforSetting) 
	throws InvalidJsonError, JSONException, TimeOutError, ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;

	public GeneralSetting executeGeneralSetting(Context context, String languageBeforSetting)throws Exception;

	public void executeClickToCallScenario(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	public WizardResponse executeWizard(
			Context context, String languageBeforSetting, String whichContext)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	
	public HomeBannerResponse executeHomeBanner(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	
	public TrainIconResponse executeTrainIcons(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;

	public void encryptDatabase(Context context);
	public void storeDefaultData(Context context, boolean isChangeLanguage);
	public void storeGeneralSettings( Context context, String language) throws IOException, Exception;
	public MasterDataResponse executeMasterData(Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	
	public void insertStationMatrix(Context context, List<OriginDestinationRule> originDestinationRules);
	public void insertStations(Context context, List<Station> stations);

	public void cleanLastModifiedHomeBanner(Context context);
	public TrainIconResponse storeTrainIcon(Context context) throws InvalidJsonError, JSONException;

}
