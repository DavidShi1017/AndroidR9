package com.cfl.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.City;
import com.cfl.model.CityDetailResponse;

import com.cfl.model.CollectionItem;
import com.cfl.model.DeliveryMethod;
import com.cfl.model.FAQCategory;
import com.cfl.model.GeneralSetting;
import com.cfl.model.GeneralSettingResponse;
import com.cfl.model.HomeBannerResponse;
import com.cfl.model.MasterDataResponse;
import com.cfl.model.OriginDestinationRule;
import com.cfl.model.TrainIconResponse;
import com.cfl.model.WeatherInformationResponse;
import com.cfl.model.WizardResponse;

import com.cfl.model.Currency;

import com.cfl.model.Station;



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
	public GeneralSettingResponse getGeneralSettingFromPackage(Context context, String language)throws Exception;

}
