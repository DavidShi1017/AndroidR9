package com.cflint.dataaccess.converters;

import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.model.CityDetailResponse;
import com.cflint.model.CityResponse;
import com.cflint.model.ClickToCallScenarioResponse;
import com.cflint.model.CollectionResponse;
import com.cflint.model.CurrencyResponse;
import com.cflint.model.DeliveryMethodResponse;
import com.cflint.model.FAQResponse;
import com.cflint.model.GeneralSettingResponse;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.MasterDataResponse;
import com.cflint.model.OriginDestinationResponse;
import com.cflint.model.StationResponse;
import com.cflint.model.TrainIconResponse;
import com.cflint.model.WeatherInformationResponse;
import com.cflint.model.WizardResponse;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter JSON String to Object by GSON.
 *
 * @author David.Shi
 *
 */
public class MasterResponseConverter {

	/**
	 * Converter JSON String to Currency by GSON.
	 *
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public CurrencyResponse parseCurrency(String jsonString)
			throws JSONException, InvalidJsonError {

		//Log.d("parseJsonAndCreateModel", "Starting...");

		Gson gson = new Gson();
		CurrencyResponse currencyResponse = gson.fromJson(jsonString,
				CurrencyResponse.class);

		//Log.d("Currency is null? ",String.valueOf(currencyResponse.getCurrencies() == null));
		//Log.d("parseJsonAndCreateModel", "End...");

		if (currencyResponse.getCurrencies() == null) {
			throw new InvalidJsonError();
		} else {
			return currencyResponse;
		}

	}

	/**
	 * Converter JSON String to CollectionItem by GSON.
	 *
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public CollectionResponse parseCollectionItem(String jsonString)
			throws JSONException, InvalidJsonError {
		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			CollectionResponse collectionResponse = gson.fromJson(jsonString,
					CollectionResponse.class);

			//Log.d("Currency is null? ", String.valueOf(collectionResponse.getCollectionItems() == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			if (collectionResponse.getCollectionItems() == null) {
				Log.d("parseCollectionItem", "null...");
				throw new InvalidJsonError();
			} else {
				return collectionResponse;
			}
		} catch (Exception e) {
			Log.d("parseCollectionItem", "Exception...");
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to DeliveryMethodResponse by GSON.
	 *
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public DeliveryMethodResponse parseDeliverymethods(String jsonString)
			throws JSONException, InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			DeliveryMethodResponse deliveryMethodResponse = gson.fromJson(
					jsonString, DeliveryMethodResponse.class);
			//Log.d("parseJsonAndCreateModel", "End...");

			if (deliveryMethodResponse.getDeliveryMethods() == null) {
				throw new InvalidJsonError();
			} else {
				return deliveryMethodResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to Station by GSON.
	 *
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
/*	public StationResponse parseStation(String jsonString)
			throws JSONException, InvalidJsonError {

		try {
			//Log.d("Station", "Starting...");

			Gson gson = new Gson();
			StationResponse stationResponse = gson.fromJson(jsonString,
					StationResponse.class);

			//Log.d("Station", "Currency is null? " +String.valueOf(stationResponse.getStations() == null));
			//Log.d("Station", "End...");

			if (stationResponse.getStations() == null) {
				throw new InvalidJsonError();
			} else {
				return stationResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}*/
	/**
	 * use the fastjson
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public StationResponse parseStation(String jsonString)
			throws JSONException, InvalidJsonError {

		try {
			//Log.d("Station", "Starting...");

			/*Gson gson = new Gson();
			StationResponse stationResponse = gson.fromJson(jsonString,
					StationResponse.class);*/
			Gson gson = new Gson();
			StationResponse stationResponse = null;
			try {


				stationResponse = gson.fromJson(jsonString,StationResponse.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Log.d("Station", "Currency is null? " +String.valueOf(stationResponse.getStations() == null));
			//Log.d("Station", "End...");

			if (stationResponse.getStations() == null) {
				throw new InvalidJsonError();
			} else {
				return stationResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to OriginDestinationRule by GSON.
	 *
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public OriginDestinationResponse parseOriginDestinationRule(
			String jsonString) throws JSONException, InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			OriginDestinationResponse originDestinationResponse = gson
					.fromJson(jsonString, OriginDestinationResponse.class);

			//Log.d("originDestinationResponse is null? ", String.valueOf(originDestinationResponse.getOriginDestinationRules() == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			if (originDestinationResponse.getOriginDestinationRules() == null) {
				throw new InvalidJsonError();
			} else {
				return originDestinationResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to CityData by GSON.
	 *
	 * @param jsonString
	 * @return CityResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public CityResponse parseCityData(String jsonString) throws JSONException,
			InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			CityResponse cityResponse = gson.fromJson(jsonString,
					CityResponse.class);

			//Log.d("CityResponse is null? ",String.valueOf(cityResponse.getCities() == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			if (cityResponse.getCities() == null) {
				throw new InvalidJsonError();
			} else {
				return cityResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to CityDetailResponse by GSON.
	 *
	 * @param jsonString
	 * @return CityResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public CityDetailResponse parseCityDetail(String jsonString)
			throws JSONException, InvalidJsonError {

		//Log.d("parseJsonAndCreateModel", "Starting...");
		GsonBuilder builder = new GsonBuilder();

		// builder.registerTypeAdapter(Date.class, new
		// DateDeserializer()).setDateFormat("yyyy-MM-dd hh:mm:ss").create();;
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			public Date deserialize(JsonElement json, Type typeOfT,
									JsonDeserializationContext context)
					throws JsonParseException {

				SimpleDateFormat format = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				String date = (json.getAsJsonPrimitive().getAsString().replace(
						"T", " "));
				try {
					return format.parse(date);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Gson gson = builder.create();

		// Gson gson = new Gson();
		try {
			CityDetailResponse cityDetailResponse = gson.fromJson(jsonString,
					CityDetailResponse.class);

			if (cityDetailResponse.getCityDataDetails() == null) {
				throw new InvalidJsonError();
			} else {

				//Log.d("CityDetailResponse is null? ",String.valueOf(cityDetailResponse.getCityDataDetails() == null));
				// Log.d("Start Date is  ",
				// cityDetailResponse.getCityDataDetails().getEvents().get(0).getPromotions().get(0).getStartdate()+"");
				//Log.d("parseJsonAndCreateModel", "End...");
				return cityDetailResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to WeatherInformation data by GSON.
	 *
	 * @param jsonString
	 * @return CityResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */

	public WeatherInformationResponse parseWeatherInformationData(
			String jsonString) throws Exception {

		//Log.d("parseJsonAndCreateModel", "Starting..." + jsonString);

		Gson gson = new Gson();
		WeatherInformationResponse weatherInformationResponse = gson.fromJson(
				jsonString, WeatherInformationResponse.class);

		// Log.d("WeatherInformationResponse is null? ",String.valueOf(weatherInformationResponse.getWeatherInformation()
		// == null));
		// Log.d("parseJsonAndCreateModel", "End...");

		if (weatherInformationResponse == null) {
			return null;
		} else {
			return weatherInformationResponse;
		}

	}

	/**
	 * Converter JSON String to GeneralSettingResponse data by GSON.
	 *
	 * @param jsonString
	 * @return CityResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public GeneralSettingResponse parseGeneralSettingResponseData(
			String jsonString) throws JSONException, InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			GeneralSettingResponse generalSettingResponse = gson.fromJson(jsonString, GeneralSettingResponse.class);

			//Log.d("GeneralSettingResponse is null? ", String.valueOf(generalSettingResponse.getConfiguration() == null));
			//Log.d("parseJsonAndCreateModel", "End...");
			/*
			if (generalSettingResponse.getConfiguration() == null) {
				throw new InvalidJsonError();
			} else {
				return generalSettingResponse;
			}
			*/
			return generalSettingResponse;
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to FAQ DATA by GSON.
	 *
	 * @param jsonString
	 * @return FAQResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public FAQResponse parseFAQData(String jsonString) throws JSONException,
			InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			FAQResponse faqResponse = gson.fromJson(jsonString,
					FAQResponse.class);

			//Log.d("FAQResponse is null? ",String.valueOf(faqResponse.getCategories() == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			if (faqResponse.getCategories() == null) {
				throw new InvalidJsonError();
			} else {
				return faqResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	/**
	 * Converter JSON String to ClickToCallScenarioResponse DATA by GSON.
	 *
	 * @param jsonString
	 * @return ClickToCallScenarioResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public ClickToCallScenarioResponse parseClickToCallScenario(
			String jsonString) throws JSONException, InvalidJsonError {

		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			ClickToCallScenarioResponse clickToCallScenarioResponse = gson
					.fromJson(jsonString, ClickToCallScenarioResponse.class);

			//Log.d("ClickToCallScenarioResponse is null? ", String.valueOf(clickToCallScenarioResponse.getClickToCallScenario() == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			if (clickToCallScenarioResponse.getClickToCallScenario() == null) {
				throw new InvalidJsonError();
			} else {
				return clickToCallScenarioResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}

	public WizardResponse parseWizard(
			String jsonString) throws JSONException, InvalidJsonError {
		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			WizardResponse wizardResponse = gson
					.fromJson(jsonString, WizardResponse.class);
			//Log.d("WizardResponse is null? ", String.valueOf(wizardResponse == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			return wizardResponse;

		} catch (Exception e) {
			throw new InvalidJsonError();
		}
	}

	public HomeBannerResponse parseHomeBanner(
			String jsonString) throws JSONException, InvalidJsonError {
		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			HomeBannerResponse homeBannerResponse = gson
					.fromJson(jsonString, HomeBannerResponse.class);
			//Log.d("WizardResponse is null? ", String.valueOf(homeBannerResponse == null));
			//Log.d("parseJsonAndCreateModel", "End...");
			if (homeBannerResponse != null){
				if(homeBannerResponse.getHomeBanners() == null){
					throw new InvalidJsonError();
				}
			}
			return homeBannerResponse;

		} catch (Exception e) {
			throw new InvalidJsonError();
		}
	}

	public TrainIconResponse parseTrainIcon(
			String jsonString) throws JSONException, InvalidJsonError {
		try {
			//Log.d("parseJsonAndCreateModel", "Starting...");

			Gson gson = new Gson();
			TrainIconResponse trainIconResponse = gson
					.fromJson(jsonString, TrainIconResponse.class);
			//Log.d("WizardResponse is null? ", String.valueOf(homeBannerResponse == null));
			//Log.d("parseJsonAndCreateModel", "End...");

			return trainIconResponse;

		} catch (Exception e) {
			throw new InvalidJsonError();
		}
	}

	public MasterDataResponse parseMasterData(String jsonString)throws JSONException, InvalidJsonError {

		//Log.d("parseJsonAndCreateModel", "Starting...");

		Gson gson = new Gson();
		MasterDataResponse masterDataResponse = gson.fromJson(jsonString, MasterDataResponse.class);

		//Log.d("Currency is null? ",String.valueOf(currencyResponse.getCurrencies() == null));
		//Log.d("parseJsonAndCreateModel", "End...");

		if (masterDataResponse.getPaymentMethods()== null) {
			throw new InvalidJsonError();
		} else {
			return masterDataResponse;
		}

	}
}
