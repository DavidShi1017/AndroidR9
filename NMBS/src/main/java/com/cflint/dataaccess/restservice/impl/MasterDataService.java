package com.cflint.dataaccess.restservice.impl;



import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;





import com.cflint.R;
import com.cflint.activity.SettingsActivity;
import com.cflint.dataaccess.converters.MasterResponseConverter;
import com.cflint.dataaccess.database.ClickToCallScenarioDatabaseService;
import com.cflint.dataaccess.database.CollectionItemDatabaseService;

import com.cflint.dataaccess.database.DeliveryMethodDatabaseService;
import com.cflint.dataaccess.database.GeneralSettingDatabaseService;
import com.cflint.dataaccess.database.OriginDestinationRuleDatabaseService;
import com.cflint.dataaccess.database.StationDatabaseService;
import com.cflint.dataaccess.database.TrainIconsDatabaseService;
import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.log.LogUtils;
import com.cflint.model.City;
import com.cflint.model.ClickToCallScenario;
import com.cflint.model.ClickToCallScenarioResponse;
import com.cflint.model.CollectionItem;
import com.cflint.model.CollectionResponse;
import com.cflint.model.DeliveryMethod;
import com.cflint.model.Destination;
import com.cflint.model.GeneralSetting;
import com.cflint.model.GeneralSettingResponse;
import com.cflint.model.MasterDataResponse;
import com.cflint.model.Origin;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.OriginDestinationResponse;
import com.cflint.model.OriginDestinationRule;
import com.cflint.model.Station;
import com.cflint.model.TrainIcon;
import com.cflint.model.TrainIconResponse;
import com.cflint.model.WizardResponse;
import com.cflint.services.impl.SettingService;
import com.cflint.util.AppUtil;
import com.cflint.util.DateUtils;
import com.cflint.util.FileManager;
import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.HttpRetriever;
import com.cflint.util.LocaleChangedUtils;
import com.cflint.util.SharedPreferencesUtils;
import com.cflint.util.Utils;


/**
 * @Description: Call web service asynchronously and get json file
 * @author David.Shi
 *
 */
public class MasterDataService implements IMasterDataService{

	
	public static final String TAG = MasterDataService.class.getSimpleName();
	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
	
	
	
	public final static String LAST_MODIFIED_TIME = "last_modified_time";  
	//public final static String LAST_MODIFIED_MESSAGE = "last_modified_message";
	public final static String LAST_MODIFIED_HOMEBANNER = "last_modified_homebanner";


	private String originDestinationRuleLastModified = "OriginDestinationRuleLastModified";
	private String masterDataLastModified = "MasterDataLastModified";
	private String clickToCallScenarioLastModified = "ClickToCallScenarioLastModified";
	private String gengeralSettingLastModified = "GengeralSettingLastModified";

	/**
	 * Call web service asynchronously and get List<CollectionItem>.
	 * @param context
	 * @param languageBeforSetting
	 * @return CityDetailResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 * @throws ConnectionError 
	 */
	public List<CollectionItem> executeLanguageCollection(Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		//Log.i(TAG, "User would like to download data in language = "+languageBeforSetting);
		//serverLastModifiedString = "LanguageLastModified";
		
		String stringHttpResponse = null;
		
		//operate languages part.
		
		//Log.d(TAG, "Connect to lnaguages service....");	   
		/*Date date = new Date();
		String dateString = DateUtils.timeToString(date);
		String urlStringOfLanguages = context.getString(R.string.server_url_get_languages) + "?id=" + dateString;
		
		String lastModifiedInPreferences = readLastModifiedTime(context);
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfLanguages, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 10000, true, lastModifiedInPreferences);
		saveLastModifiedTime(context, httpRestServiceCaller.getLastModified());*/
		
		
		/*if (!StringUtils.equalsIgnoreCase("304", stringHttpResponse)) {
			CollectionResponse collectionResponse = masterResponseConverter.parseCollectionItem(stringHttpResponse);
			if(collectionResponse != null){
				return collectionResponse.getCollectionItems();
			}
		}*/
		
		InputStream is = context.getResources().openRawResource(R.raw.languages);
		stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		//System.out.println("stringHttpResponse======" + stringHttpResponse);
		CollectionResponse collectionResponse = masterResponseConverter.parseCollectionItem(stringHttpResponse);
		if(collectionResponse != null){
			return collectionResponse.getCollectionItems();
		}
		
		return null;	
	}


	
	/**
	 * Call web service asynchronously and get List<Station>.
	 * @param context
	 * @param languageBeforSetting
	 * @return CityDetailResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 * @throws ConnectionError 
	 */
	public List<Station> executeStationCollection(Context context, String languageBeforSetting, List<OriginDestinationRule> originDestinationRules)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		/*serverLastModifiedString = "StationLastModified";
		
		//operate stations part.
		String stringHttpResponse = null;
		//Log.d("Station", "Connect to stations service....");	
		Date date = new Date();
		String dateString = DateUtils.timeToString(date);
		String urlStringOfStations = context.getString(R.string.server_url_get_stations) + "?id=" + dateString;
		
		String lastModifiedInPreferences = readLastModifiedTime(context);
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfStations, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 10000, true, lastModifiedInPreferences);
		
		//Log.d("Station", "Connect to stations stringHttpResponse...." + stringHttpResponse);	
		//Log.d("Station", "Connect to stations masterResponseConverter....");	
		
		stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(urlStringOfStations, languageBeforSetting, lastModifiedInPreferences, 10000);
		StationResponse stationResponse = masterResponseConverter.parseStation(stringHttpResponse);
		//Log.d("Station", "Connect to stations getLastModified...." + httpRestServiceCaller.getLastModified());	
		saveLastModifiedTime(context, httpRestServiceCaller.getLastModified());
		if(stationResponse != null){
			return stationResponse.getStations();
		}*/
		
		List<Station> tempStations = new ArrayList<Station>();
		
		Map<String, Station> stationMap = new HashMap<String, Station>();
		for (OriginDestinationRule originDestinationRule : originDestinationRules) {
			
			List<Origin> origins = originDestinationRule.getOrigins();
			List<Destination> destinations = originDestinationRule.getDestinations();
			for (Origin origin : origins){
				Station station = new Station(origin.getCode(), origin.getName(), "", "", origin.getSynoniem(), "");
				tempStations.add(station);
			}	
			
			for (Destination destination : destinations) {
				Station station = new Station(destination.getCode(), destination.getName(), "", "", destination.getSynoniem(), "");
				tempStations.add(station);
			}			
			
		}
		for (Station station : tempStations) {
			if (!stationMap.containsKey(station.getCode())) {
				stationMap.put(station.getCode(), station);
			}
		}
		
		List<Station> stations = new ArrayList<Station>(stationMap.values());
/*		Iterator<Entry<String, Station>> iter = stationMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			
		    Entry<String, Station> entry = iter.next(); 
		    stations.add(entry.getValue());			    			   
		} */
		
		return stations;	
	}

	
	/**
	 * Call web service asynchronously and get List<OriginDestinationRule>.
	 * @param context
	 * @param languageBeforSetting
	 * @return CityDetailResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 * @throws ConnectionError 
	 */
	public List<OriginDestinationRule> executeOriginDestinationRules(Context context, String languageBeforSetting)
		throws InvalidJsonError, JSONException, TimeOutError,
		ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		
		
		//operate titles part.
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to OriginDestinationRule service....");	  
		
		//String dateString = DateUtils.timeToString(date);
		String urlStringOfRules = context.getString(R.string.server_url_get_origin_destination_rules);
		
		String lastModifiedInPreferences = readLastModifiedTime(context, originDestinationRuleLastModified);
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfRules, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 10000, true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_4);
		
		/*stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(urlStringOfRules, languageBeforSetting, 
				lastModifiedInPreferences, 10000, HTTPRestServiceCaller.API_VERSION_VALUE_3);*/
		
		if (!"304".equalsIgnoreCase(stringHttpResponse)) {
			Map<String, String> lastModified = httpRestServiceCaller.getLastModified();
			if (lastModified != null) {
				saveLastModifiedTime(context, lastModified.get(urlStringOfRules), originDestinationRuleLastModified);
			}
			
			//FileManager.getInstance().writeToSdCardFromString("/OriginDestinationRules.json", stringHttpResponse);
			//Log.d(TAG, "OriginDestinationRule......" + stringHttpResponse);
			OriginDestinationResponse originDestinationResponse = masterResponseConverter.parseOriginDestinationRule(stringHttpResponse);
			if(originDestinationResponse != null){
				
				//Log.d(TAG, "OriginDestinationRule......" + originDestinationResponse.getOriginDestinationRules());
				//Log.d(TAG, "OriginDestinationRule......getOrigins....name" + originDestinationResponse.getOriginDestinationRules().get(0).getOrigins().get(0).getName());
				return originDestinationResponse.getOriginDestinationRules();
			}
		}

		return null;
	}

	


	public void executeClickToCallScenario(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		
		
		// operate titles part.
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to ClickToCallScenarioResponse service....");
/*		Date date = new Date();
		String dateString = DateUtils.timeToString(date);*/
		String urlString = context.getString(R.string.server_url_get_click_to_call_scenarios);// + "?id=" + dateString;
		
		String lastModifiedInPreferences = readLastModifiedTime(context, clickToCallScenarioLastModified);
		stringHttpResponse = httpRestServiceCaller
				.executeHTTPRequest(
						context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 15000, 
						true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_3);
		
		
		
		/*stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(context, urlString, languageBeforSetting, 
				lastModifiedInPreferences, 10000, HTTPRestServiceCaller.API_VERSION_VALUE_3);*/
	
		if (!"304".equalsIgnoreCase(stringHttpResponse)) {
			Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

			
			ClickToCallScenarioResponse clickToCallScenarioResponse = masterResponseConverter.parseClickToCallScenario(stringHttpResponse);
			if (clickToCallScenarioResponse != null) {

				//Log.d(TAG, "ClickToCallScenarioResponse......"+ clickToCallScenarioResponse.getClickToCallScenario());
				if (lastModified != null) {
					saveLastModifiedTime(context, lastModified.get(urlString), clickToCallScenarioLastModified);
				}
				insertclickToCallScenarios(context, clickToCallScenarioResponse.getClickToCallScenario());
			}
		}
		
		
	}
	/**
	 * Call web service asynchronously and get GeneralSettingResponse.
	 * @param context
	 * @param languageBeforSetting
	 * @return GeneralSettingResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 * @throws ConnectionError
	 */

	public GeneralSetting executeGeneralSetting(Context context, String languageBeforSetting)throws Exception{
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to CityDetailResponse service....");
		String urlString = context.getString(R.string.server_url_get_general);
		String lastModifiedInPreferences = readLastModifiedTime(context, gengeralSettingLastModified);
		if(isTestBooking(context)){
			lastModifiedInPreferences = "";
		}
		try {
			stringHttpResponse = httpRestServiceCaller
                    .executeHTTPRequest(
							context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 4000,
							true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_7);
			LogUtils.d("GeneralSetting", "stringHttpResponse-------->" + stringHttpResponse);

			if (!"304".equalsIgnoreCase(stringHttpResponse)) {
				Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

				GeneralSettingResponse generalSettingResponse = masterResponseConverter.parseGeneralSettingResponseData(stringHttpResponse);
				if (generalSettingResponse != null) {
					if (lastModified != null) {
						saveLastModifiedTime(context, lastModified.get(urlString), gengeralSettingLastModified);
					}
					LogUtils.d("GeneralSetting", "AutoLogonSalt-------->" + generalSettingResponse.getGeneralSetting().getAutoLogonSalt());
					insertGeneralSetting(generalSettingResponse.getGeneralSetting(), context);

					String fileName = null;
					try {
						fileName = Utils.sha1(generalSettingResponse.getGeneralSetting().getInsuranceConditionsPdf()) + ".pdf";
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					boolean isFinished = downloadPdf(context, generalSettingResponse.getGeneralSetting().getInsuranceConditionsPdf(), "GeneralSetting", fileName);

				}else{
					storeGeneralSettings(context, languageBeforSetting);
				}
			}else{
				storeGeneralSettings(context, languageBeforSetting);
			}
		} catch (Exception e) {
			storeGeneralSettings(context, languageBeforSetting);
		}
		return null;
	}

	public GeneralSettingResponse getGeneralSettingFromPackage(Context context, String language) throws Exception{
		int resourcesId = 0;
		if (SettingService.LANGUAGE_EN.contains(language.toUpperCase())) {
			resourcesId = R.raw.general_settings_en;
			//pdfIs = context.getResources().getAssets().open("touring_conditions_en.pdf");
		}else if(SettingService.LANGUAGE_FR.contains(language.toUpperCase())){
			resourcesId = R.raw.general_settings_fr;
			//pdfIs = context.getResources().getAssets().open("touring_conditions_fr.pdf");
		}else if(SettingService.LANGUAGE_NL.contains(language.toUpperCase())){
			resourcesId = R.raw.general_settings_en;
			//pdfIs = context.getResources().getAssets().open("touring_conditions_nl.pdf");
		}else if(SettingService.LANGUAGE_DE.contains(language.toUpperCase())){
			resourcesId = R.raw.general_settings_de;
			//pdfIs = context.getResources().getAssets().open("touring_conditions_de.pdf");
		} else {
			resourcesId = R.raw.general_settings_en;
			//pdfIs = context.getResources().getAssets().open("touring_conditions_en.pdf");
		}
		InputStream is = context.getResources().openRawResource(resourcesId);
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		//Log.e(TAG, "stringHttpResponse...." + stringHttpResponse);

		GeneralSettingResponse generalSettingResponse = masterResponseConverter.parseGeneralSettingResponseData(stringHttpResponse);

		return generalSettingResponse;
	}

	public void storeGeneralSettings( Context context, String language) throws Exception {

		//Log.e(TAG, "storeGeneralSettings....");
		GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(context);

		GeneralSetting generalSettingInDataBase = generalSettingDatabaseService.selectGeneralSetting();
		InputStream pdfIs = null;

		if(generalSettingInDataBase == null){
			//Log.e(TAG, "storeGeneralSettings....generalSettingInDataBase...");
			GeneralSettingResponse generalSettingResponse = getGeneralSettingFromPackage(context, language);
			//Log.e(TAG, "generalSettingResponse...." + generalSettingResponse);
			if (generalSettingResponse != null) {
				insertGeneralSetting(generalSettingResponse.getGeneralSetting(), context);
				String fileName = null;
				try {
					fileName = Utils.sha1(generalSettingResponse.getGeneralSetting().getInsuranceConditionsPdf()) + ".pdf";
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				FileManager.getInstance().createExternalStoragePrivateFile(context, pdfIs, "GeneralSetting", fileName);
				//boolean isFinished = downloadPdf(context, generalSettingResponse.getGeneralSetting().getInsuranceConditionsPdf(), "GeneralSetting", fileName);
			}
		}
	}



	private boolean downloadPdf(Context context, String url, String folderName, String fileName){
		boolean hasError = false;
		InputStream inputStream = null;
		try {
			inputStream = HttpRetriever.getInstance().retrieveStream(url);
			//Log.d(TAG, "download InsuranceConditionsPdf......");
			//InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
			//InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
							/*httpDownloader.downloadNetworkFile(urlString, FileManager.getInstance().getFilePath("/pdfpath/"),fileName + ".pdf");  */
			//Log.d(TAG, "The folder name is......" + folderName);
			//Log.d(TAG, "The File name is......" + fileName);
			FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, folderName, fileName);

		} catch (Exception e) {
			hasError = true;
			e.printStackTrace();
		}

		return hasError;
	}

	
	public WizardResponse executeWizard(
			Context context, String languageBeforSetting, String whichContext)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{	
		String serverLastModifiedString = "WizardLastModified" + whichContext;
		
		// operate titles part.
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to ClickToCallScenarioResponse service....");
		/*Date date = new Date();
		String dateString = DateUtils.timeToString(date);*/
		//String versionName = Utils.getAppVersion(context);
		String versionName = "6.2";
		String urlString = context.getString(R.string.server_url_get_wizard) + whichContext + "?appVersion="+versionName;// + "?id=" + dateString;
		
		String lastModifiedInPreferences = readLastModifiedTime(context, serverLastModifiedString);
		stringHttpResponse = httpRestServiceCaller
				.executeHTTPRequest(
						context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 10000,
						true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_3);
		
		
		/*stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(context, urlString, languageBeforSetting, 
				lastModifiedInPreferences, 10000, HTTPRestServiceCaller.API_VERSION_VALUE_3);*/
		if (!"304".equalsIgnoreCase(stringHttpResponse)) {
			Map<String, String> lastModified = httpRestServiceCaller.getLastModified();
			if (lastModified != null) {
				saveLastModifiedTime(context, lastModified.get(urlString), serverLastModifiedString);
			}
			
			WizardResponse wizardResponse = masterResponseConverter.parseWizard(stringHttpResponse);
			if (wizardResponse != null) {
				
				FileManager.getInstance().createExternalStoragePrivateFileFromString(context, null, whichContext + ".json", stringHttpResponse);
				return wizardResponse;
			}
		}

		return null;
	}
	
	public HomeBannerResponse executeHomeBanner(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{		
		// operate titles part.
		String serverLastModifiedString = LAST_MODIFIED_HOMEBANNER;
		
		
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to executeHomeBanner service...." + languageBeforSetting);
/*		Date date = new Date();
		String dateString = DateUtils.timeToString(date);*/
		String urlString = context.getString(R.string.server_url_get_homebanner);// + "?id=" + dateString;
		
		String lastModifiedInPreferences = readLastModifiedTime(context, serverLastModifiedString);
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(
				context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 6000,
				true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_6);
/*		stringHttpResponse = "{\n" +
				"  \"MobileBanners\": [\n" +
				"    {\n" +
				"      \"Id\": \"3D43A685-C0E5-4BDF-937C-A869A91C5480\",\n" +
				"      \"Title\": \"Thalys Neige\",\n" +
				"      \"SubTitle\": \"Les Alpes fran莽aises\",\n" +
				"      \"HighResImage\": \"/-/media/MediaRepository/Images_LowRes/Promotions/Thalys/20170914_ThalysSnow/140917_1080x354_thalys_neige.ashx\",\n" +
				"      \"LowResImage\": \"/-/media/MediaRepository/Images_LowRes/Promotions/Thalys/20170914_ThalysSnow/140917_1080x354_thalys_neige.ashx?sc=0.5&hash=DC352F53B4D2B7563519ACAE67DFF32EE4C94C40\",\n" +
				"      \"IncludeActionButton\": true,\n" +
				"      \"ActionButtonText\": \"R脡SERVEZ\",\n" +
				"      \"Hyperlink\": \"http://www.accept.b-europe.com/FR/Acheter/Promos/Thalys/Thalys-Neige?affiliation=App&utm_campaign=cfl_app&utm_medium=referral_app&utm_source=cfl_app\",\n" +
				"      \"NavigationInNormalWebView\": false\n" +
				"    }\n" +
				"  ],\n" +
				"  \"Messages\": [],\n" +
				"  \"DebugMessages\": []\n" +
				"}";*/
		/*stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(context, urlString, languageBeforSetting, 
				lastModifiedInPreferences, 10000, HTTPRestServiceCaller.API_VERSION_VALUE_3);*/
		if (!"304".equalsIgnoreCase(stringHttpResponse)) {
			Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

			HomeBannerResponse homeBannerResponse = masterResponseConverter.parseHomeBanner(stringHttpResponse);
			
			if (homeBannerResponse != null) {
				if (lastModified != null) {
					saveLastModifiedTime(context, lastModified.get(urlString), serverLastModifiedString);
				}
				FileManager.getInstance().deleteExternalStoragePrivateFile(context, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER);
				FileManager.getInstance().createExternalStoragePrivateFileFromString(context, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER, stringHttpResponse);
				return homeBannerResponse;
			}
		}

		return null;
	}
	
	
	public TrainIconResponse executeTrainIcons(
			Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{		
		// operate titles part.
		try{

		}catch (Exception e){

		}
		String serverLastModifiedString = "TrainIconsLastModified";
		String stringHttpResponse = null;
		String urlString = context.getString(R.string.server_url_get_trainIcons);// + "?id=" + dateString;
		String lastModifiedInPreferences = readLastModifiedTime(context, serverLastModifiedString);
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(
						context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 6000,
						true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_6);
		//Log.e("TrainIcons", "stringHttpResponse...." + stringHttpResponse);
		TrainIconsDatabaseService db = new TrainIconsDatabaseService(context);
		TrainIconResponse trainIconResponse = null;
		if (!"304".equalsIgnoreCase(stringHttpResponse)) {
			Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

			trainIconResponse = masterResponseConverter.parseTrainIcon(stringHttpResponse);
			if(trainIconResponse != null && trainIconResponse.getTrainIcons() != null){
				if (lastModified != null) {
					saveLastModifiedTime(context, lastModified.get(urlString), serverLastModifiedString);
				}
				db.deleteData(TrainIconsDatabaseService.DB_TRAIN_ICONS);
				db.insertTrainIcons(trainIconResponse.getTrainIcons());
				//Log.e("TrainIcons", "Insert train icons finished");
				//db.selectTrainIcons();
			}else{
				//Log.e("TrainIcons", "get TrainIcons in raw....");
				trainIconResponse = storeTrainIcon(context);
			}
			if (trainIconResponse != null) {
				return trainIconResponse;
			}
		}else{
			trainIconResponse = storeTrainIcon(context);
		}

		return trainIconResponse;
	}

	public TrainIconResponse storeTrainIcon(Context context) throws InvalidJsonError, JSONException {
		TrainIconsDatabaseService db = new TrainIconsDatabaseService(context);
		List<TrainIcon> trainIcons = db.selectTrainIcons();
		TrainIconResponse trainIconResponse = null;
		if(trainIcons == null || trainIcons.size() == 0){
			InputStream is = context.getResources().openRawResource(R.raw.train_icon);
			String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
			trainIconResponse = masterResponseConverter.parseTrainIcon(stringHttpResponse);
			if(trainIconResponse != null && trainIconResponse.getTrainIcons() != null){
				db.deleteData(TrainIconsDatabaseService.DB_TRAIN_ICONS);
				db.insertTrainIcons(trainIconResponse.getTrainIcons());
				//Log.e("TrainIcons", "Insert train icons finished");
				//db.selectTrainIcons();
			}
		}
		return trainIconResponse;
	}
	public List<TrainIcon> getTrainIcons(Context context){
		TrainIconsDatabaseService trainIconsDatabaseService = new TrainIconsDatabaseService(context);
		return trainIconsDatabaseService.selectTrainIcons();
	}
	
	private String cutLastModified(String lastModified){
		if (lastModified != null && lastModified.indexOf(':') != -1) {
			lastModified = lastModified.substring(lastModified.indexOf(':') + 2);
		}	
		return lastModified;
	}
	
	private void saveLastModifiedTime(Context context, String lastModified, String whichServer){
		lastModified = cutLastModified(lastModified);
		//Log.e(TAG, "saveLastModifiedTime...." + lastModified);
		if (lastModified != null){
			SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
			settings.edit().putString(whichServer, lastModified.trim()).commit();
		}
	}

	private void saveCityLastModifiedTime(Context context, String lastModified, City city, String sharedPreferencesName){
		if (lastModified != null && lastModified.indexOf(':') != -1) {
			lastModified = lastModified.substring(lastModified.indexOf(':') + 2);
		}			
		String cityNameString = "";
		if (city != null) {
			cityNameString = city.getName();
		}
		SharedPreferences settings = context.getSharedPreferences(sharedPreferencesName, 0);
		settings.edit().putString(cityNameString, lastModified.trim()).commit();
		
	}
	
	
	
	
	private String readLastModifiedTime(Context context, String whichServer){
		
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
		//String defLastModified = "";

		
		String lastModified = settings.getString(whichServer, "").toString();
		
		if (lastModified.isEmpty()) {
			if (masterDataLastModified.equalsIgnoreCase(whichServer)) {
				lastModified = context.getResources().getString(R.string.masterdata_last_modify);
				lastModified = cutLastModified(lastModified);
			}else if("ClickToCallScenarioLastModified".equalsIgnoreCase(whichServer)){
				lastModified = context.getResources().getString(R.string.c2c_last_modify);
				lastModified = cutLastModified(lastModified);
			}
		}
		return lastModified;
	}

	
	public void cleanLastModifiedTime(Context context){
		
		//Log.d("Station" ,"cleanLastModifiedTime......... "  );
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
		//settings.getAll().clear();
		settings.edit().clear().commit();
		cleanLastModifiedHomeBanner(context);
		cleanGengeralSetting(context);
		cleanClickToCallScenario(context);
	}
	public void cleanLastModifiedHomeBanner(Context context){
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_HOMEBANNER, 0);
		settings.edit().clear().commit();
	}
	public void cleanGengeralSetting(Context context){
		SharedPreferences settings = context.getSharedPreferences(gengeralSettingLastModified, 0);
		settings.edit().clear().commit();
	}
	public void cleanClickToCallScenario(Context context){
		SharedPreferences settings = context.getSharedPreferences(masterDataLastModified, 0);
		settings.edit().clear().commit();
	}
	
	public void encryptDatabase(Context context){

		//}
	}//
	
	
	/**
	 * Call insertCities method in DatabaseService. 
	 * @param
	 */
	private void insertclickToCallScenarios(Context context, List<ClickToCallScenario> clickToCallScenarios){
		//Log.d(TAG, "Converter ClickToCallScenario succeed....");	
		ClickToCallScenarioDatabaseService clickToCallScenarioDatabaseService = new ClickToCallScenarioDatabaseService(context);
				
		ClickToCallScenario clickToCallScenarioInDataBase = clickToCallScenarioDatabaseService.loadClickToCallScenarioById(1);
		if(clickToCallScenarioInDataBase != null){
			
			//Log.d(TAG, "Start delete data for FAQ table....");
				
			boolean isDeleted = clickToCallScenarioDatabaseService.deleteMasterData(ClickToCallScenarioDatabaseService.DB_TABLE_CLICK_TO_CALL_SCENARIO);
				
			//Log.d(TAG, "isDeleted...." + isDeleted);
			if(isDeleted){
				//Log.d(TAG, "Delete FAQ succeed....");
				//Log.d(TAG, "Start insert data to FAQ table....");
				clickToCallScenarioDatabaseService.deleteMasterData(ClickToCallScenarioDatabaseService.DB_TABLE_PROVIDER_SETTING);
				clickToCallScenarioDatabaseService.insertClickToCallScenarios(clickToCallScenarios);
			}			
		}else{
			//Log.d(TAG, "Start insert data to FAQ table....");
			clickToCallScenarioDatabaseService.insertClickToCallScenarios(clickToCallScenarios);
		}
		
	}
	
	/**
	 * Call insertStations method in DatabaseService. 
	 * @param
	 */
	public void insertDeliveryMethod(Context context, List<DeliveryMethod> deliveryMethods){
		//Log.d(TAG, "Converter DeliveryMethods succeed....");	
		if (deliveryMethods == null) {
			return; 
		}
		DeliveryMethodDatabaseService deliveryMethodDatabaseService = new DeliveryMethodDatabaseService(context);
		List<DeliveryMethod> deliveryMethodsInDataBase = deliveryMethodDatabaseService.selectDeliveryMethodCollection();
		if(deliveryMethodsInDataBase != null){
			if(deliveryMethodsInDataBase.size() != 0){
				//Log.d(TAG, "Start delete data for DeliveryMethod table....");
				boolean isDeleted = deliveryMethodDatabaseService.deleteMasterData(DeliveryMethodDatabaseService.DB_TABLE_DELIVERY_METHODS);
				
				//Log.d(TAG, "isDeleted...." + isDeleted);
				if(isDeleted){
					//Log.d(TAG, "Delete DeliveryMethods succeed....");
					//Log.d(TAG, "Start insert data to DeliveryMethod table....");										
					deliveryMethodDatabaseService.insertDeliveryMethodCollection(deliveryMethods);
				}
			}else{
				//Log.d(TAG, "Start insert data to DeliveryMethod table....");
				deliveryMethodDatabaseService.insertDeliveryMethodCollection(deliveryMethods);
			}
		}
		
	}
	
	/**
	 * Call insertCollectionItems method in DatabaseService. 
	 * @param
	 */
	public void insertCollectionItems(Context context, List<CollectionItem> collectionItem, String tableName){
		if (collectionItem == null) {
			return;
		}
		//Log.d(TAG, "Converter " + tableName + " succeed....");
		CollectionItemDatabaseService collectionItemDatabaseService  = new CollectionItemDatabaseService(context);
		List<CollectionItem> collectionItemInDataBase = collectionItemDatabaseService.selectCollectionResponse(tableName);
		if(collectionItemInDataBase != null && collectionItem != null){
			if(collectionItemInDataBase.size() != 0){
				//Log.d(TAG, "Starting delete data from "+tableName+"  table....");
				boolean isDeleted = collectionItemDatabaseService.deleteMasterData(tableName);
				if(isDeleted){
					//Log.d(TAG, "Delete "+tableName+" succeed....");
					//Log.d(TAG, "Starting insert data to "+tableName+" table....");
					collectionItemDatabaseService.insertCollectionResponse(collectionItem, tableName);
				}else {
					collectionItemDatabaseService.insertCollectionResponse(collectionItem, tableName);
				}
			}else{
				collectionItemDatabaseService.insertCollectionResponse(collectionItem, tableName);
			}
		}
	
	}

	
	/**
	 * Call insertStations method in DatabaseService. 
	 * @param
	 */
	public void insertStations(Context context, List<Station> stations){
		//Log.d(TAG, "Converter stations succeed....");		
		if (stations == null) {
			return;
		}
		StationDatabaseService stationDatabaseService = new StationDatabaseService(context);
		List<Station> stationsInDataBase = stationDatabaseService.selectStationCollection(1, null);
		if(stationsInDataBase != null){
			if(stationsInDataBase.size() != 0){
				//Log.d(TAG, "Start delete data for STATION table....");
				boolean isDeleted = stationDatabaseService.deleteMasterData(StationDatabaseService.DB_TABLE_STATION);
				//Log.d(TAG, "isDeleted...." + isDeleted);
				if(isDeleted){
					//Log.d(TAG, "Delete stations succeed....");
					//Log.d(TAG, "Start insert data to STATION table....");
					stationDatabaseService.insertStationCollection(stations);
				}
			}else{
				//Log.d(TAG, "Start insert data to STATION table....");
				stationDatabaseService.insertStationCollection(stations);
			}
		}
		
	}
	
	/**
	 * Call insertStations method in DatabaseService. 
	 * @param
	 */
	public void insertStationMatrix(Context context, List<OriginDestinationRule> originDestinationRules){
		//Log.d(TAG, "Converter stations succeed....");	
		if (originDestinationRules == null) {
			return;
		}
		OriginDestinationRuleDatabaseService originDestinationRuleDatabaseService = new OriginDestinationRuleDatabaseService(context);
		List<String> fromStationCodes = originDestinationRuleDatabaseService.selectFromStationCodes();
		if(fromStationCodes != null){
			if(fromStationCodes.size() != 0){
				//Log.d(TAG, "Start delete data for STATION table....");
				boolean isDeleted = originDestinationRuleDatabaseService.deleteMasterData(OriginDestinationRuleDatabaseService.DB_TABLE_STATIONMATRIX);
				//Log.d(TAG, "isDeleted...." + isDeleted);
				if(isDeleted){
					//Log.d(TAG, "Delete stations succeed....");
					//Log.d(TAG, "Start insert data to STATION table....");
					originDestinationRuleDatabaseService.insertStationMatrix(originDestinationRules);
				}
			}else{
				//Log.d(TAG, "Start insert data to STATION table....");
				originDestinationRuleDatabaseService.insertStationMatrix(originDestinationRules);
			}
		}
		
	}
	
	public void storeDefaultData(Context context, boolean isChangeLanguage){
		String sharedLocale = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_LANGUAGE, context);
		//storeMasterData(context, sharedLocale, isChangeLanguage);
		storeClickToCallScenariosData(context, sharedLocale, isChangeLanguage);
		
	}
	
	private void storeClickToCallScenariosData(Context context, String language, boolean isChangeLanguage){
		
		//Log.e(TAG, "storeClickToCallScenariosData....");
		int resourcesId = getClickToCallScenariosResource(language);
		InputStream is = context.getResources().openRawResource(resourcesId);
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		
		ClickToCallScenarioResponse clickToCallScenarioResponse = null;
		try {
			clickToCallScenarioResponse = masterResponseConverter.parseClickToCallScenario(stringHttpResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if (clickToCallScenarioResponse != null) {

			//Log.d(TAG, "ClickToCallScenarioResponse......"+ clickToCallScenarioResponse.getClickToCallScenario());
			ClickToCallScenarioDatabaseService clickToCallScenarioDatabaseService = new ClickToCallScenarioDatabaseService(context);
			
			ClickToCallScenario clickToCallScenarioInDataBase = clickToCallScenarioDatabaseService.loadClickToCallScenarioById(1);
			if (clickToCallScenarioInDataBase == null) {
				insertclickToCallScenarios(context, clickToCallScenarioResponse.getClickToCallScenario());
			}else{
				if (isShouldUpdateDataBase(context, clickToCallScenarioLastModified, clickToCallScenarioLastModified, isChangeLanguage)) {
					insertclickToCallScenarios(context, clickToCallScenarioResponse.getClickToCallScenario());
					String lastModified = context.getResources().getString(R.string.c2c_last_modify);
					lastModified = cutLastModified(lastModified);
					saveLastModifiedTime(context, lastModified, clickToCallScenarioLastModified);
				}
			}
			
		}
	}

	private int getClickToCallScenariosResource(String language){
		int resourcesId = 0;
		if (LocaleChangedUtils.LANGUAGE_EN.contains(language)) {
			resourcesId = R.raw.clicktocall_en;
		}else if(LocaleChangedUtils.LANGUAGE_FR.contains(language)){
			resourcesId = R.raw.clicktocall_fr;
		}else if(LocaleChangedUtils.LANGUAGE_NL.contains(language)){
			resourcesId = R.raw.clicktocall_en;
		}else if(LocaleChangedUtils.LANGUAGE_DE.contains(language)){
			resourcesId = R.raw.clicktocall_de;
		}else {
			resourcesId = R.raw.clicktocall_en;
		}
		return resourcesId;
	}
	
	private boolean isShouldUpdateDataBase(Context context, String oldLastModifiedKey, String newLastModifiedKey, boolean isChangeLanguage){
		boolean isShould = false;
		if (isChangeLanguage) {
			return true;
		}
		
		String masterDataLastModifiedString = readLastModifiedTime(context, newLastModifiedKey);
		//masterDataLastModifiedString = cutLastModified(masterDataLastModifiedString);
		int oldVersion = AppUtil.getAppVersionCodeInSharedPreferences(context);
		if (oldVersion == 0) {
			//Log.e(TAG, "oldVersion is...." + oldVersion + ", start compare lastModifi with.." + oldLastModifiedKey);
			
			String deliveryLastModifiedString = readLastModifiedTime(context, oldLastModifiedKey);
			Date newLastModified = DateUtils.getFullDate(masterDataLastModifiedString);
			Date oldLastModified = DateUtils.getFullDate(deliveryLastModifiedString);
			//Log.e(TAG, "newLastModified is...." + newLastModified);
			//Log.e(TAG, "oldLastModified is...." + oldLastModified);
			if (newLastModified == null || oldLastModified == null || newLastModified.after(oldLastModified)) {
				isShould = true;				
			}
		}else {
			int newVersion = AppUtil.getAppVersionCode(context);
			if (oldVersion != newVersion) {
				//Log.e(TAG, "oldVersion is...." + oldVersion + ", start compare lastModifi with.." + oldLastModifiedKey);
				String lastModified = context.getResources().getString(R.string.masterdata_last_modify);
				lastModified = cutLastModified(lastModified);
				Date newLastModified = DateUtils.getFullDate(lastModified);
				Date oldLastModified = DateUtils.getFullDate(oldLastModifiedKey);
				//Log.e(TAG, "newLastModified is...." + newLastModified);
				//Log.e(TAG, "oldLastModified is...." + oldLastModified);
				if (newLastModified == null || oldLastModified == null || newLastModified.after(oldLastModified)) {
					isShould = true;										
				}
			}
		}
		//Log.e(TAG, "isShould is...." + isShould + ", oldLastModifiedKey.." + oldLastModifiedKey);
		return isShould;
	}
	
	private void storeMasterData(Context context, String language, boolean isChangeLanguage){
		int resourcesId = getMasterDataResource(language);
		
		InputStream is = context.getResources().openRawResource(resourcesId);
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		
		
		//Log.e(TAG, "storeMasterData....");
		try {
			MasterDataResponse masterDataResponse = masterResponseConverter.parseMasterData(stringHttpResponse);
			
			if (masterDataResponse != null) {

				if (masterDataResponse.getOriginDestinationRules() != null) {
					List<Station> stations = executeStationCollection(context, language, masterDataResponse.getOriginDestinationRules());
					StationDatabaseService stationDatabaseService = new StationDatabaseService(context);
					List<Station> stationsInDataBase = stationDatabaseService.selectStationCollection(1, null);
					if (stationsInDataBase == null || stationsInDataBase.size() == 0) {
						//Log.e(TAG, "OriginDestinationRulesInDataBase initialize, insert Data....");
						insertStations(context, stations);
						insertStationMatrix(context, masterDataResponse.getOriginDestinationRules());
					}if (isShouldUpdateDataBase(context, originDestinationRuleLastModified, masterDataLastModified, isChangeLanguage)) {
						insertStations(context, stations);
						insertStationMatrix(context, masterDataResponse.getOriginDestinationRules());
						String lastModified = context.getResources().getString(R.string.masterdata_last_modify);
						//lastModified = cutLastModified(lastModified);
						saveLastModifiedTime(context, lastModified, masterDataLastModified);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	private int getMasterDataResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			//resourcesId = R.raw.masterdata_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			//resourcesId = R.raw.masterdata_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			//resourcesId = R.raw.masterdata_nl;
		}else {				
			//resourcesId = R.raw.masterdata_de;
		}
		return resourcesId;
	}
	
	public MasterDataResponse executeMasterData(Context context, String languageBeforSetting)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		
		
		//operate contries part.
		String stringHttpResponse = null;
		//Log.d(TAG, "Connect to countries service....");	
		/*Date date = new Date();
		String dateString = DateUtils.timeToString(date);*/
		
		String urlStringOfMasterData = context.getString(R.string.server_url_get_masterdata);// + "?id=" + dateString;;
		
		String lastModifiedInPreferences = readLastModifiedTime(context, masterDataLastModified);
		
		stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context,null, urlStringOfMasterData, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 15000, true, lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_4);
/*		stringHttpResponse = httpRestServiceCaller.getGetResponseWithHttpClient(urlStringOfCountries, languageBeforSetting, 
				lastModifiedInPreferences, 10000, HTTPRestServiceCaller.API_VERSION_VALUE_3);*/
		Map<String, String> lastModified = httpRestServiceCaller.getLastModified();
		if (lastModified != null) {
			saveLastModifiedTime(context, lastModified.get(urlStringOfMasterData), masterDataLastModified);
		}
		
		
		MasterDataResponse masterDataResponse = masterResponseConverter.parseMasterData(stringHttpResponse);
		if(masterDataResponse != null && masterDataResponse.getPaymentMethods() != null){
			return masterDataResponse;
		}
		return null;
	}

	public boolean  isTestBooking(Context context){
		GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(context);
		GeneralSetting generalSettingInDataBase = generalSettingDatabaseService.selectGeneralSetting();
		if(generalSettingInDataBase != null && generalSettingInDataBase.getBookingUrl() != null){
			if(generalSettingInDataBase.getBookingUrl().contains("accept")){
				return true;
			}
		}
		return false;
	}

	private void insertGeneralSetting(GeneralSetting generalSetting, Context context) {
		//Log.d(TAG, "Converter GeneralSetting succeed....");
		GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(
				context);
		GeneralSetting generalSettingInDataBase = generalSettingDatabaseService.selectGeneralSetting();
		if (generalSettingInDataBase != null) {
			//Log.d(TAG, "Start delete data for GeneralSetting table....");
			boolean isDeleted = generalSettingDatabaseService.deleteMasterData(GeneralSettingDatabaseService.DB_GENERAL_SETTINGS);
			//Log.d(TAG, "isDeleted...." + isDeleted);
			if (isDeleted) {
				//Log.d(TAG, "Delete GeneralSetting succeed....");
				//Log.d(TAG, "Start insert data to GeneralSetting table....");
				generalSettingDatabaseService.insertStationCollection(generalSetting);
			}
		} else {
			//Log.d(TAG, "Start insert data to GeneralSetting table....");
			generalSettingDatabaseService.insertStationCollection(generalSetting);
		}

	}
}


