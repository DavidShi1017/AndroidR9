package com.nmbs.services;

/**
 * Control Offer Data and View communicate.
 */
import java.util.List;

import android.content.Context;

import com.nmbs.model.AdditionalConnectionsParameter;
import com.nmbs.model.Connection;

import com.nmbs.model.Offer;
import com.nmbs.model.OfferItem;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.OfferResponse;
import com.nmbs.model.ReductionCard;

import com.nmbs.model.TariffPassenger;

import com.nmbs.services.impl.AsyncCorporateCardResponse;
import com.nmbs.services.impl.AsyncOfferResponse;

public interface IOfferService{

	/**
	 * Call OfferService's SearchOffer method and get OfferResponse asynchronously.
	 * @param: offerRequest
	 * @return: AsyncOfferResponse
	 */
	public AsyncOfferResponse searchOffer(OfferQuery offerRequest,  ISettingService settingService);
	
	/**
	 * Get OfferQuery from OfferService.
	 * @param None
	 * @return OfferQuery
	 */
	public OfferQuery getOfferQuery();
	public void setOfferQuery(OfferQuery offerQuery);
	/**
	 * Clear OfferQuery when the application is destroyed.
	 * @param None
	 */
	public void clearOfferQuery();
	public void deleteLastOfferQuery();
	/**
	 * set the default value of OfferRequest
	 * @param OfferQuery
	 */
	public void setDefaultOfferQuery(OfferQuery offerQuery, Context context, String languageBeforSetting);
	public List<Connection> getAvailableReturnConnectionList(Offer offerData,OfferResponse offerResponse);
	public List<String> getFoundNotCombinableIds(Offer offerData,OfferResponse offerResponse);
	public AsyncOfferResponse searchTrains(AdditionalConnectionsParameter additionalConnectionsParameter,  ISettingService settingService);

	public void clearMyState();
	
	//public String getOfferPrice(Currency currency, double price);//symbol+price value	
	//public String getOfferPrice(ISettingService settingService, IMasterService masterService, String currencyCode);
	public double getOfferPrice(ISettingService settingService, IMasterService masterService, Offer offer);
	public String getOfferSymbol(ISettingService settingService, IMasterService masterService);
	public List<Offer> getOffers(Connection selsetedDepartureConnection, OfferQuery offerQuery);
	
	public AsyncCorporateCardResponse searchGreenPoints(String greenpointNumber, String languageBeforSetting);
	public void saveFtpCardForUser();
	public String getEurostarFtpCardForUser();
	public String getThalysFtpCardForUser();
	public void setDefaultOfferQueryFromLastSelection(OfferQuery offerQuery, String languageBeforSetting);
	public boolean hasReservation(ISettingService settingService, IMasterService masterService, Offer offer);
	public void saveOfferQueryToLastOfferQuery();
	public String getReservationPrice(IOfferService offerService, ISettingService settingService, IMasterService masterService, Offer offer);
	public boolean isAddOptionalReservationsForDeparture();
	public void setAddOptionalReservationsForDeparture(
			boolean addOptionalReservationsForDeparture);
	public boolean isAddOptionalReservationsForReturn();
	public void setAddOptionalReservationsForReturn(
			boolean addOptionalReservationsForReturn);
	public double getTariffPrice(ISettingService settingService, IMasterService masterService, TariffPassenger tariffPassenger);
	public OfferResponse getOfferResponse();
	public void setOfferResponse(OfferResponse offerResponse);
	public boolean isBookingTimeOut();
	public void recordBookingTime();
	public ReductionCard getAppliedReductionCard(TariffPassenger tariffPassenger);
	public String getTariffPriceAndCurrencySymbol(ISettingService settingService, IMasterService masterService, 
			TariffPassenger tariffPassenger);
	public String getOfferItemPriceAndCurrencySymbol(ISettingService settingService, IMasterService masterService, 
			OfferItem OfferItem);
	public boolean isHasWarning(Connection selsetedDepartureConnection, Connection selectedReturnConnection);
	public String getTransferRes(int transferCount);
}
