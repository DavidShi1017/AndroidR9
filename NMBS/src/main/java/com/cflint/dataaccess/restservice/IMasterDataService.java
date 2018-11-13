package com.cflint.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.City;
import com.cflint.model.CityDetailResponse;

import com.cflint.model.CollectionItem;
import com.cflint.model.DeliveryMethod;
import com.cflint.model.FAQCategory;
import com.cflint.model.GeneralSetting;
import com.cflint.model.GeneralSettingResponse;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.MasterDataResponse;
import com.cflint.model.OriginDestinationRule;
import com.cflint.model.TrainIconResponse;
import com.cflint.model.WeatherInformationResponse;
import com.cflint.model.WizardResponse;

import com.cflint.model.Currency;

import com.cflint.model.Station;



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
