package com.nmbs.services;

import android.content.Context;

import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.model.City;
import com.nmbs.model.ClickToCallScenario;
import com.nmbs.model.CollectionItem;
import com.nmbs.model.Currency;
import com.nmbs.model.DeliveryMethod;
import com.nmbs.model.DeliveryOption;
import com.nmbs.model.FavoriteStation;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.HomeBannerResponse;
import com.nmbs.model.PaymentOption;
import com.nmbs.model.Station;
import com.nmbs.model.WizardResponse;
import com.nmbs.services.impl.AsyncCityDetailResponse;
import com.nmbs.services.impl.AsyncMasterDataResponse;
import com.nmbs.services.impl.MasterService.RequiredMasterDataMissingType;

import org.json.JSONException;

import java.util.List;
/**
 * Control Master Data and View communicate.
 */
public interface IMasterService {
	/**
	 * Start new thread and load MasterData from web Service.
	 * @return AsyncMasterDataResponse
	 */
	public AsyncMasterDataResponse getMasterData(ISettingService settingService, boolean isMastdataWorking);
	/**
	 * Load all Collection from sqLite
	 * @param tableName it contains:DB_TABLE_LANGUAGE, DB_TABLE_TITLE, DB_TABLE_COUNTRY
	 */
	public List<CollectionItem> loadCollectionResponse(String tableName);
	/**
	 * Load all Currency from sqLite
	 */
	public List<Currency> loadCurrencyCollection();
	/**
	 * Load all Station from sqLite
	 * @throws InvalidJsonError 
	 * @throws JSONException 
	 */
	public List<Station> loadStationCollection(int fromOrTo, String stationFromCode, String language)throws Exception;
	public List<CollectionItem> loadCollectionResponseByKey(String tableName, List<String> keyList);
	public Currency searchCurrencyByCode(String currencyCode);
	
	
	public List<City> loadCityCollection(List<City> listFavorites);
	public List<City> loadFavoritesCollection();
	public boolean addFavorite(City city);
	public void deleteFavorite(City favorite);
	public void deleteCity(City city);
	public void deleteCity(List<City> cities);
	public List<DeliveryMethod> loadDeliveryMethodsCollection(List<DeliveryOption> deliveryOptions);
	public List<String> loadCountryCodeByDeliveryMethod(String deliveryMethod);
	public List<Station> loadStationCollectionByStationCode(List<String> StationCodes);
	public Station loadStationByStationCode(String stationCode);
	public void insertCity(City city);
	public AsyncCityDetailResponse getCityDetail(City city, ISettingService settingService);
	public GeneralSetting loadGeneralSetting();
	public City loadCityByMainStationCode(String stationCode);
	public City loadFavoriteByMainStationCode(String stationCode);
	public boolean updateEventIDs(City city);
	public boolean updatePOIIDs(City city);
	public boolean updateRestoIDs(City city);
	public boolean isNewID(String id, int eprFlag);
	public void deleteFavoriteTable() ;
	public void deleteCityTable();
	public Station loadStationByStationName(String stationName);
	public ClickToCallScenario loadClickToCallScenarioById(int CallScenarioId);
	public List<DeliveryMethod> loadDeliveryMethodsCollectionByDeliveryMethodName(String deliveryMethodName);
	public List<Station> loadStationsByStationName(List<String> stationsName);
	public List<CollectionItem> loadPaymentOptions(List<PaymentOption> paymentOptions);

	public WizardResponse loadWizardResponse(String whichContext, String language);
	public RequiredMasterDataMissingType checkRequiredData()throws Exception;
	public HomeBannerResponse loadHomeBannerResponse() throws JSONException, InvalidJsonError;
	public boolean updateEPRIDs(String id, int eprFlag, City city);
	public void cleanLastModifiedTime();
	public List<Station> joinBeStation(List<Station> stations, String language) throws Exception;
	public Station loadBeStationByStationCode(String stationCode, String language) throws JSONException, InvalidJsonError;
	
	public List<FavoriteStation> getFavoriteStationCode(Context context);
	
	public boolean insertStationCode(FavoriteStation favoriteStation,Context context);
	
	public boolean deleteStationCode(String code,Context context);
	public boolean shouldShowWizard();
}
