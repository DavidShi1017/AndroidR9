package com.cflint.services.impl;

import android.content.Context;


import com.cflint.R;

import com.cflint.activities.WizardActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.converters.MasterResponseConverter;

import com.cflint.dataaccess.database.ClickToCallScenarioDatabaseService;
import com.cflint.dataaccess.database.CollectionItemDatabaseService;
import com.cflint.dataaccess.database.CurrencyDatabaseService;
import com.cflint.dataaccess.database.DeliveryMethodDatabaseService;
import com.cflint.dataaccess.database.FavoriteDatabaseService;
import com.cflint.dataaccess.database.FavoriteStationsDatabaseService;
import com.cflint.dataaccess.database.GeneralSettingDatabaseService;
import com.cflint.dataaccess.database.OriginDestinationRuleDatabaseService;
import com.cflint.dataaccess.database.StationDatabaseService;
import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.dataaccess.restservice.impl.MasterDataService;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.log.LogUtils;
import com.cflint.model.City;
import com.cflint.model.ClickToCallScenario;
import com.cflint.model.CollectionItem;
import com.cflint.model.Currency;
import com.cflint.model.DeliveryMethod;
import com.cflint.model.DeliveryOption;
import com.cflint.model.FavoriteStation;
import com.cflint.model.GeneralSetting;
import com.cflint.model.GeneralSettingResponse;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.PaymentOption;
import com.cflint.model.Station;
import com.cflint.model.StationResponse;
import com.cflint.model.WizardResponse;
import com.cflint.services.IMasterService;
import com.cflint.services.ISettingService;
import com.cflint.util.AppUtil;
import com.cflint.util.ComparatorStationName;
import com.cflint.util.FileManager;
import com.cflint.util.LocaleChangedUtils;
import com.cflint.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Control Master Data and View communicate.
 */
public class MasterService implements IMasterService {
	private final static String TAG = MasterService.class.getSimpleName();
	private Context applicationContext;
	private CollectionItemDatabaseService collectionItemDatabaseService;
	private CurrencyDatabaseService currencyDatabaseService;
	private StationDatabaseService stationDatabaseService;

	private FavoriteDatabaseService favoriteDatabaseService;
	private DeliveryMethodDatabaseService deliveryMethodDatabaseService;
	private ClickToCallScenarioDatabaseService clickToCallScenarioDatabaseService;
	private OriginDestinationRuleDatabaseService originDestinationRuleDatabaseService;
	private static List<Station> beStations = null;

	public final static String HOME_WIZARD = "Home";

	public enum RequiredMasterDataMissingType {
		RequiredMasterDataMissingTypeStation, 
		RequiredMasterDataMissingTypeORRules,
		RequiredMasterDataMissingTypeDeliveryMethod,
		RequiredMasterDataMissingTypePaymentMethod,
		RequiredMasterDataMissingTypeCorrect,
		RequiredMasterDataMissingTypeTitle
	}
	
	public MasterService(Context context){
		this.applicationContext = context;
	}
	
	public List<FavoriteStation> getFavoriteStationCode(Context context){
		
		/*FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		try {
			return database.readStationCodeList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}*/
		return null;
	}

	public boolean insertStationCode(FavoriteStation favoriteStation,Context context){
		FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		return database.insertStationCode("");
	}
	
	public boolean deleteStationCode(String code,Context context){
		FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		return database.deleteStationCode(code);
	}
	
	
	
	/**
	 * Start new thread and load MasterData from web Service.
	 * @return AsyncMasterDataResponse
	 */
	public AsyncMasterDataResponse getMasterData(ISettingService settingService, boolean isMastdataWorking) {
		
		//Log.d(TAG, "enter MasterService");
		AsyncMasterDataResponse asyncMasterDataResponse = new AsyncMasterDataResponse();
		asyncMasterDataResponse.registerReceiver(applicationContext);
		//Offload processing to IntentService
		MasterIntentService.startService(applicationContext, settingService.getCurrentLanguagesKey(), isMastdataWorking);
		
		// Return the async response who will receive the final return
		return asyncMasterDataResponse;
		//return null;
	}

	/**
	 * Load all Collection from sqLite
	 * @param tableName it contains:DB_TABLE_LANGUAGE, DB_TABLE_TITLE, DB_TABLE_COUNTRY
	 */
	public List<CollectionItem> loadCollectionResponse(String tableName) {

		collectionItemDatabaseService = new CollectionItemDatabaseService(applicationContext);
		List<CollectionItem> collectionItems = collectionItemDatabaseService.selectCollectionResponse(tableName);
		return collectionItems;
	}
	
	public List<CollectionItem> loadCollectionResponseByKey(String tableName, List<String> keyList) {

		collectionItemDatabaseService = new CollectionItemDatabaseService(applicationContext);
		List<CollectionItem> collectionItems = collectionItemDatabaseService.selectCollectionResponseByKey(tableName, keyList);
		return collectionItems;
	}
	/**
	 * Load all Currency from sqLite
	 */
	public List<Currency> loadCurrencyCollection() {
		currencyDatabaseService = new CurrencyDatabaseService(applicationContext);
		List<Currency> currencies = currencyDatabaseService.selectCurrencyCollection();
		return currencies;
	}
	
	/**
	 * Load all Station from sqLite
	 * @throws InvalidJsonError 
	 * @throws JSONException 
	 */
	public List<Station> loadStationCollection(int fromOrTo, String stationFromCode, String language)throws Exception{
		//Log.d(TAG, "stationFromCode...." + stationFromCode);
		stationDatabaseService = new StationDatabaseService(applicationContext);
/*		if (stationFromCode != null && !StringUtils.isEmpty(stationFromCode)) {
			for (Station beStation : getBeStations(language)) {
				if (StringUtils.equalsIgnoreCase(stationFromCode, beStation.getCode())) {
					stationFromCode = Station.BE_CODE;
				}
			}
		}*/
		
		String beLongToCodeString = "";
		List<Station> stations = stationDatabaseService.selectStationCollection(fromOrTo, stationFromCode);
		if (fromOrTo == 2) {
			beLongToCodeString = beLongTo(stationFromCode);
			//Log.d(TAG, "beLongToCodeString...." + beLongToCodeString);
			List<Station> extraStations = stationDatabaseService.selectStationCollection(fromOrTo, beLongToCodeString);
			Map<String, Station> stationMap = new LinkedHashMap<String, Station>();
			if (stations != null) {
				for (Station station : stations) {
					stationMap.put(station.getCode(), station);
				}
			}
			if (extraStations != null) {
				for (Station extraStation : extraStations) {
					stationMap.put(extraStation.getCode(), extraStation);
				}
			}
			List<Station> allStations = new ArrayList<Station>(stationMap.values());
			Comparator<Station> comp = new ComparatorStationName();
		    Collections.sort(allStations, comp);
		    return allStations;
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.BE_CODE);
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.FR_CODE);
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.DE_CODE);
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.NL_CODE);
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.LU_CODE);
		}
		if (stations == null || stations.size() == 0) {
			stations = stationDatabaseService.selectStationCollection(fromOrTo, Station.GB_CODE);
		}
		return stations;
	}
	
	private String beLongTo(String fromCode){
		if (fromCode != null && fromCode.length() > 2) {
			fromCode = fromCode.substring(0, 2);
			if (Station.BE_CODE.equalsIgnoreCase(fromCode)) {
				return Station.BE_CODE;
			}
			if (Station.FR_CODE.equalsIgnoreCase(fromCode)) {
				return Station.FR_CODE;
			}
			if (Station.DE_CODE.equalsIgnoreCase(fromCode)) {
				return Station.DE_CODE;
			}
			if (Station.NL_CODE.equalsIgnoreCase(fromCode)) {
				return Station.NL_CODE;
			}
			if (Station.LU_CODE.equalsIgnoreCase(fromCode)) {
				return Station.LU_CODE;
			}
			if (Station.GB_CODE.equalsIgnoreCase(fromCode)) {
				return Station.GB_CODE;
			}
		}
		return "";
	}
	
	public List<Station> joinBeStation(List<Station> stations, String language) throws Exception{
				
		Map<String, Station> stationMap = new LinkedHashMap<String, Station>();
		List<Station> beStations = joinStation(stations, language, Station.BE_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		beStations = joinStation(stations, language, Station.FR_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		beStations = joinStation(stations, language, Station.DE_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		beStations = joinStation(stations, language, Station.NL_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		beStations = joinStation(stations, language, Station.LU_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		beStations = joinStation(stations, language, Station.GB_CODE);
		if (beStations != null) {
			for (Station beStation : beStations) {
				stationMap.put(beStation.getCode(), beStation);
			}
		}
		
		if (stationMap.size() > 0) {
			for (Station station : stations) {
				if (!stationMap.containsKey(station.getCode()) 
						&& !StringUtils.equalsIgnoreCase(Station.BE_CODE, station.getCode())
						&& !StringUtils.equalsIgnoreCase(Station.FR_CODE, station.getCode())
						&& !StringUtils.equalsIgnoreCase(Station.DE_CODE, station.getCode())
						&& !StringUtils.equalsIgnoreCase(Station.NL_CODE, station.getCode())
						&& !StringUtils.equalsIgnoreCase(Station.LU_CODE, station.getCode())
						&& !StringUtils.equalsIgnoreCase(Station.GB_CODE, station.getCode())) {
					stationMap.put(station.getCode(), station);
				}
			}
		}else {
			return stations;
		}
		
		
		
	/*	}else {
			for (Station station : stations) {
				for (Station beStation : getBeStations(language)) {
					if (StringUtils.equalsIgnoreCase(beStation.getCode(), station.getCode())) {
						station.setSynoniem(beStation.getSynoniem());
						station.setQuery(beStation.getQuery());
					}
				}
			}
			return stations;
		}	*/	
		List<Station> allStations = new ArrayList<Station>(stationMap.values());
		Comparator<Station> comp = new ComparatorStationName();
	    Collections.sort(allStations, comp);
		return allStations;
	}
	
	private List<Station> joinStation(List<Station> stations, String language, String countryCode) throws JSONException, InvalidJsonError{
		boolean hasBeStation = false;
		List<Station> beStations = null;
		for (Station station : stations) {	
			
			if (StringUtils.equalsIgnoreCase(countryCode, station.getCode())) {
				hasBeStation = true;			
				break;
			}	
		}
		if (hasBeStation) {
			beStations = getBeStations(language, countryCode);
			
		}
		return beStations;
	}
	
	private List<Station> getBeStations(String language, String countryCode) throws JSONException, InvalidJsonError{
		
		MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
		int resourcesId = 0;
		
		if (countryCode.equalsIgnoreCase(Station.BE_CODE)) {
			//resourcesId = getHardCodeBeStationResource(language);
		}else if (countryCode.equalsIgnoreCase(Station.FR_CODE)) {
			//resourcesId = getHardCodeFrStationResource(language);
		}else if (countryCode.equalsIgnoreCase(Station.DE_CODE)) {
			//resourcesId = getHardCodeDeStationResource(language);
		}else if (countryCode.equalsIgnoreCase(Station.NL_CODE)) {
			//resourcesId = getHardCodeNlStationResource(language);
		}else if (countryCode.equalsIgnoreCase(Station.LU_CODE)) {
			//resourcesId = getHardCodeLuStationResource(language);
		}else if (countryCode.equalsIgnoreCase(Station.GB_CODE)) {
			//resourcesId = getHardCodeGbStationResource(language);
		}
		
		InputStream is = applicationContext.getResources().openRawResource(resourcesId);  
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		StationResponse stationResponse = masterResponseConverter.parseStation(stringHttpResponse);
		if (stationResponse != null) {
			beStations = stationResponse.getStations();
		}
		
		return beStations;
	}
	
	/*private int getHardCodeBeStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.be_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.be_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.be_station_nl;
		}else {				
			resourcesId = R.raw.be_station_de;
		}
		return resourcesId;
	}
	
	private int getHardCodeFrStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.fr_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.fr_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.fr_station_nl;
		}else {				
			resourcesId = R.raw.fr_station_de;
		}
		return resourcesId;
	}
	
	private int getHardCodeDeStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.de_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.de_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.de_station_nl;
		}else {				
			resourcesId = R.raw.de_station_de;
		}
		return resourcesId;
	}
	
	private int getHardCodeNlStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.nl_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.nl_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.nl_station_nl;
		}else {				
			resourcesId = R.raw.nl_station_de;
		}
		return resourcesId;
	}
	
	private int getHardCodeLuStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.lu_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.lu_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.lu_station_nl;
		}else {				
			resourcesId = R.raw.lu_station_de;
		}
		return resourcesId;
	}
	
	private int getHardCodeGbStationResource(String language){
		int resourcesId = 0;
		if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.gb_station_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.gb_station_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.gb_station_nl;
		}else {				
			resourcesId = R.raw.gb_station_de;
		}
		return resourcesId;
	}*/
	
	/**
	 * Load one Station by station code from sqLite
	 */
	public Station loadStationByStationCode(String stationCode) {

		stationDatabaseService = new StationDatabaseService(applicationContext);
		Station station = stationDatabaseService.selectStationByStationCode(stationCode);

		return station;
	}

	@Override
	public void insertCity(City city) {

	}

	public Station loadBeStationByStationCode(String stationCode, String language) throws JSONException, InvalidJsonError{

		/*for (Station beStation : getBeStations(language)) {
			if(beStation != null){
				if (StringUtils.equalsIgnoreCase(beStation.getCode(), stationCode)) {
					return beStation;
				}
			}

		}*/

		return null;
	}
	
	/**
	 * Load Station By StationCode from sqLite
	 */
	public List<Station> loadStationCollectionByStationCode(List<String> StationCodes) {

		stationDatabaseService = new StationDatabaseService(applicationContext);
		List<Station> stations = stationDatabaseService.selectStationCollectionByStationCode(StationCodes);

		return stations;
	}
	public Currency searchCurrencyByCode(String currencyCode){
		Currency currency = null;
		currencyDatabaseService = new CurrencyDatabaseService(applicationContext);
		currency = currencyDatabaseService.selectCurrencyCollectionByCode(currencyCode);
		return currency;
	}

	@Override
	public List<City> loadCityCollection(List<City> listFavorites) {
		return null;
	}

	@Override
	public List<City> loadFavoritesCollection() {
		return null;
	}


	/**
	 * Load all CityData from sqLite
	 */
	public City loadFavoriteByMainStationCode(String stationCode) {
		favoriteDatabaseService = new FavoriteDatabaseService(applicationContext);
		City city = favoriteDatabaseService.selectCityByMainStationCode(stationCode);
		//Log.d(TAG, "cities size is." + cities.size());	
		return city;
	}

	@Override
	public boolean updateEventIDs(City city) {
		return false;
	}

	@Override
	public boolean updatePOIIDs(City city) {
		return false;
	}

	@Override
	public boolean updateRestoIDs(City city) {
		return false;
	}

	@Override
	public boolean isNewID(String id, int eprFlag) {
		return false;
	}


	/**

	 * Load all DeliveryMethod from sqLite
	 */
	public List<DeliveryMethod> loadDeliveryMethodsCollection(List<DeliveryOption> deliveryOptions) {
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		//Log.d(TAG, "deliveryOptions size is." + deliveryOptions.size());
		deliveryMethodDatabaseService = new DeliveryMethodDatabaseService(applicationContext);
		deliveryMethods = deliveryMethodDatabaseService.selectDeliveryMethodCollectionByDeliveryMethod(deliveryOptions);
		//Log.d(TAG, "get deliveryMethods size is." + deliveryMethods.size());	
		return deliveryMethods;
	}
	
	/**

	 * Load all DeliveryMethod from sqLite
	 */
	public List<DeliveryMethod> loadDeliveryMethodsCollection() {
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		
		deliveryMethodDatabaseService = new DeliveryMethodDatabaseService(applicationContext);
		deliveryMethods = deliveryMethodDatabaseService.selectDeliveryMethodCollection();
		//Log.d(TAG, "get deliveryMethods size is." + deliveryMethods.size());	
		return deliveryMethods;
	}
	
	/**

	 * Load all DeliveryMethod from sqLite
	 */
	public List<DeliveryMethod> loadDeliveryMethodsCollectionByDeliveryMethodName(String deliveryMethodName) {
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		
		deliveryMethodDatabaseService = new DeliveryMethodDatabaseService(applicationContext);
		deliveryMethods = deliveryMethodDatabaseService.selectDeliveryMethodCollectionByDeliveryMethodName(deliveryMethodName);
		//Log.d(TAG, "get deliveryMethods size is." + deliveryMethods.size());	
		return deliveryMethods;
	}
	public List<String> loadCountryCodeByDeliveryMethod(String deliveryMethod){
		List<String> countryCodeList = new ArrayList<String>();
		//Log.d(TAG, "countryCodeList size is." + countryCodeList.size());
		deliveryMethodDatabaseService = new DeliveryMethodDatabaseService(applicationContext);
		countryCodeList = deliveryMethodDatabaseService.selectAllowedCountriesForMailByDeliveryMethod(deliveryMethod);
		//Log.d(TAG, "get countryCodeList size is." + countryCodeList.size());	
		return countryCodeList;
	}
	public boolean addFavorite(City city) {
		favoriteDatabaseService = new FavoriteDatabaseService(applicationContext);
		boolean isAdded = favoriteDatabaseService.insertOneFavorite(city);
		
		return isAdded;
	}
	
	/**
	 * delete a CityData for Favorite
	 */
	public void deleteFavorite(City favorite) {
		favoriteDatabaseService = new FavoriteDatabaseService(applicationContext);
		favoriteDatabaseService.deleteOneFavorite(favorite);
		
		//return cities;
	}

	@Override
	public void deleteCity(City city) {

	}

	@Override
	public void deleteCity(List<City> cities) {

	}

	/**
	 * delete Favorite table
	 */
	public void deleteFavoriteTable() {
		favoriteDatabaseService = new FavoriteDatabaseService(applicationContext);
		favoriteDatabaseService.deleteMasterData(FavoriteDatabaseService.DB_TABLE_FAVORITE);
		
		//return cities;
	}

	@Override
	public void deleteCityTable() {

	}

	/**
	 * Start new thread and load CityDetail from web Service.
	 * @return AsyncMasterDataResponse
	 */
	public AsyncCityDetailResponse getCityDetail(City city, ISettingService settingService) {
		
		
		AsyncCityDetailResponse asyncCityDetailResponse = new AsyncCityDetailResponse();
		
		asyncCityDetailResponse.registerReceiver(applicationContext);
		//Offload processing to IntentService

		
		// Return the async response who will receive the final return
		return asyncCityDetailResponse;		
	}
	public GeneralSetting loadGeneralSetting() {
		GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(applicationContext);
		GeneralSetting generalSetting = generalSettingDatabaseService.selectGeneralSetting();
		IMasterDataService iMasterDataService = new MasterDataService();
		if(iMasterDataService.isTestBooking(applicationContext)){
			generalSetting = null;
			//iMasterDataService.storeGeneralSettings(applicationContext, "EN_GB");
		}
		if(generalSetting == null || generalSetting.getBookingUrl() == null || generalSetting.getBookingUrl().isEmpty()){

            LogUtils.e("loadGeneralSetting", "loadGeneralSetting form package");
			try {
				GeneralSettingResponse generalSettingResponse = iMasterDataService.getGeneralSettingFromPackage(applicationContext, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
				if(generalSettingResponse != null){
					generalSetting = generalSettingResponse.getGeneralSetting();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return generalSetting;
	}

	@Override
	public City loadCityByMainStationCode(String stationCode) {
		return null;
	}

	public ClickToCallScenario loadClickToCallScenarioById(int CallScenarioId) {
		clickToCallScenarioDatabaseService = new ClickToCallScenarioDatabaseService(applicationContext);
		ClickToCallScenario clickToCallScenario = clickToCallScenarioDatabaseService.loadClickToCallScenarioById(CallScenarioId);	
		return clickToCallScenario;
	}
	
	public List<ClickToCallScenario> loadClickToCallScenarios() {
		clickToCallScenarioDatabaseService = new ClickToCallScenarioDatabaseService(applicationContext);
		List<ClickToCallScenario> clickToCallScenarios = clickToCallScenarioDatabaseService.loadClickToCallScenarios();	
		return clickToCallScenarios;
	}
	
	public List<String> loadOriginDestinationRules() {
		originDestinationRuleDatabaseService = new OriginDestinationRuleDatabaseService(applicationContext);
		
		List<String> originDestinationRules = originDestinationRuleDatabaseService.selectFromStationCodes();	
		return originDestinationRules;
	}
	
	public Station loadStationByStationName(String stationName) {
		StationDatabaseService stationDatabaseService = new StationDatabaseService(applicationContext);
		Station originStation = stationDatabaseService.selectStationByStationName(stationName);
		return originStation;
	}
	
	public List<Station> loadStationsByStationName(List<String> stationsName) {
		StationDatabaseService stationDatabaseService = new StationDatabaseService(applicationContext);
		List<Station> stations = new ArrayList<Station>();
		for (String stationName : stationsName) {
			Station originStation = stationDatabaseService.selectStationByStationName(stationName);
			if (originStation != null) {
				stations.add(originStation);
			}			
		}
		
		return stations;
	}
	public List<CollectionItem> loadPaymentOptions(List<PaymentOption> paymentOptions){
		List<CollectionItem> collectionItems = null;
		CollectionItemDatabaseService collectionItemDatabaseService = new CollectionItemDatabaseService(applicationContext);
		collectionItems = collectionItemDatabaseService.selectCollectionResponseByMethodOfPaymentOptions(CollectionItemDatabaseService.DB_TABLE_PAYMENTMETHOD, paymentOptions);
		return collectionItems;
	}
	public WizardResponse loadWizardResponse(String whichContext, String language) {
		MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
		int resourcesId = getHomeWizardResource(language);

		if(whichContext.equalsIgnoreCase(WizardActivity.Wizard_MyTickets)){
			resourcesId = getMyTicketWizardResource(language);
		}
		
		InputStream is = applicationContext.getResources().openRawResource(resourcesId);  
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);

		WizardResponse wizardResponse = null;
		try {
			wizardResponse = masterResponseConverter.parseWizard(stringHttpResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidJsonError invalidJsonError) {
			invalidJsonError.printStackTrace();
		}

		return wizardResponse;
	}

	private int getHomeWizardResource(String language){
		int resourcesId = 0;
		if (LocaleChangedUtils.LANGUAGE_EN.contains(language.toUpperCase())) {
			resourcesId = R.raw.home_en;
		}else if(LocaleChangedUtils.LANGUAGE_FR.contains(language.toUpperCase())){
			resourcesId = R.raw.home_fr;
		}else if(LocaleChangedUtils.LANGUAGE_NL.contains(language.toUpperCase())){
			resourcesId = R.raw.home_en;
		}else if(LocaleChangedUtils.LANGUAGE_DE.contains(language.toUpperCase())){
			resourcesId = R.raw.home_de;
		}else {
			resourcesId = R.raw.home_en;
		}
		return resourcesId;
	}

	private int getMyTicketWizardResource(String language){
		int resourcesId = 0;
		if (LocaleChangedUtils.LANGUAGE_EN.contains(language.toUpperCase())) {
			resourcesId = R.raw.my_tickets_en;
		}else if(LocaleChangedUtils.LANGUAGE_FR.contains(language.toUpperCase())){
			resourcesId = R.raw.my_tickets_fr;
		}else if(LocaleChangedUtils.LANGUAGE_NL.contains(language.toUpperCase())){
			resourcesId = R.raw.my_tickets_en;
		}else if(LocaleChangedUtils.LANGUAGE_DE.contains(language.toUpperCase())){
			resourcesId = R.raw.my_tickets_de;
		}else {
			resourcesId = R.raw.my_tickets_en;
		}
		return resourcesId;
	}
	
	public HomeBannerResponse loadHomeBannerResponse() throws JSONException, InvalidJsonError {
		MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
		HomeBannerResponse homeBannerResponse = masterResponseConverter.parseHomeBanner(FileManager.getInstance().readExternalStoragePrivateFile(applicationContext, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER));
				
		return homeBannerResponse;
	}

	@Override
	public boolean updateEPRIDs(String id, int eprFlag, City city) {
		return false;
	}

	public RequiredMasterDataMissingType checkRequiredData()throws Exception {
		List<Station> stations = loadStationCollection(0, null, null);
		
		if (stations == null || stations.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeStation;
		}
		List<String> originDestinationRules = loadOriginDestinationRules();
		
		if (originDestinationRules == null || originDestinationRules.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeORRules;
		}
		List<DeliveryMethod> deliveryMethodsInDataBase = loadDeliveryMethodsCollection();
		
		if (deliveryMethodsInDataBase == null || deliveryMethodsInDataBase.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeDeliveryMethod;
		}
		List<CollectionItem> collectionItems = loadCollectionResponse(CollectionItemDatabaseService.DB_TABLE_PAYMENTMETHOD);
		
		if (collectionItems == null || collectionItems.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypePaymentMethod;
		}
/*		collectionItems = loadCollectionResponse(CollectionItemDatabaseService.DB_TABLE_TITLE);
		
		if (collectionItems == null || collectionItems.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeTitle;
		}*/
		
		List<ClickToCallScenario> clickToCallScenarios = loadClickToCallScenarios();
		
		if (clickToCallScenarios == null || clickToCallScenarios.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeTitle;
		}
		collectionItems = loadCollectionResponse(CollectionItemDatabaseService.DB_TABLE_REDUCTION_CARD);
		
		if (collectionItems == null || collectionItems.size() == 0) {
			return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeTitle;
		}

		return RequiredMasterDataMissingType.RequiredMasterDataMissingTypeCorrect;
	}
	
	public void cleanLastModifiedTime(){
		new MasterDataService().cleanLastModifiedTime(applicationContext);
	}
	
	public boolean shouldShowWizard(){
		String oldVersionString = AppUtil.getAppVersionName(applicationContext);
		//Log.d(TAG, "oldVersionString...." + oldVersionString);
		String currentVersionString = Utils.getAppVersion(applicationContext);
		//Log.d(TAG, "currentVersionString...." + currentVersionString);
		if (oldVersionString.isEmpty()) {
			return true;
		}else {

			if (!StringUtils.equalsIgnoreCase(oldVersionString, currentVersionString) && !Utils.isBigDecimal(currentVersionString)) {
				return true;
			}
			return false;
		}
	}	
}
