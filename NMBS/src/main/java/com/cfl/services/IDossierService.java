package com.cfl.services;

import android.content.Context;

import com.cfl.model.DeliveryMethod;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossierResponse;
import com.cfl.model.OfferQuery;
import com.cfl.model.Passenger;
import com.cfl.model.PromoParameter;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.RealTimeInfoResponse;
import com.cfl.services.impl.AsyncAbortPaymentDossierResponse;
import com.cfl.services.impl.AsyncDossierPromoCodeResponse;
import com.cfl.services.impl.AsyncDossierResponse;

import java.util.List;

public interface IDossierService {
	public  AsyncDossierResponse searchDossier(DossierParameter dossierParameter, ISettingService settingService);
	public  AsyncDossierResponse UpdateDossier(DossierParameter dossierParameter, ISettingService settingService, int optionFlag);
	
	public AsyncDossierResponse initPayment(
			DossierParameter dossierParameter, ISettingService settingService);
	public DossierParameter getDossierParameter();
	public void setDossierParameter(DossierParameter dossierParameter);
	public void cleanDossierParameter(IOfferService offService);
	public AsyncDossierResponse refreshPaymentStatus(
			DossierParameter dossierParameter, ISettingService settingService);
	public void createDeliveryMethodParameterForDossierParameter(DossierResponse dossierResponse, DeliveryMethod deliveryMethod);
	public AsyncDossierResponse refreshOrderState(
			DossierParameter dossierParameter, ISettingService settingService);
	public AsyncAbortPaymentDossierResponse AbortPayment(
			DossierParameter dossierParameter, ISettingService settingService);
	public AsyncDossierResponse deleteDossier(
			DossierParameter dossierParameter, ISettingService settingService);
	public void refreshConfirmation(
			DossierParameter dossierParameter, ISettingService settingService);
	
	public String buildPassengersText(Passenger passenger, int position, Context context, OfferQuery offerQuery);
	
	public  AsyncDossierPromoCodeResponse addPromoCode(PromoParameter promo, ISettingService settingService);
	public  AsyncDossierPromoCodeResponse removePromoCode(PromoParameter promo, ISettingService settingService);
	public void setPromoParameter(PromoParameter promoParameter);
	public PromoParameter getPromoParameter();

}
