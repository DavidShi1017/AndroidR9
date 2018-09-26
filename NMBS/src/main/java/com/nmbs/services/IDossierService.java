package com.nmbs.services;

import android.content.Context;

import com.nmbs.model.DeliveryMethod;
import com.nmbs.model.DossierParameter;
import com.nmbs.model.DossierResponse;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.Passenger;
import com.nmbs.model.PromoParameter;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.RealTimeInfoResponse;
import com.nmbs.services.impl.AsyncAbortPaymentDossierResponse;
import com.nmbs.services.impl.AsyncDossierPromoCodeResponse;
import com.nmbs.services.impl.AsyncDossierResponse;

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
