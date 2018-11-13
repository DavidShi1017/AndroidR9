package com.cflint.services;

import android.content.Context;

import com.cflint.model.DeliveryMethod;
import com.cflint.model.DossierParameter;
import com.cflint.model.DossierResponse;
import com.cflint.model.OfferQuery;
import com.cflint.model.Passenger;
import com.cflint.model.PromoParameter;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.services.impl.AsyncAbortPaymentDossierResponse;
import com.cflint.services.impl.AsyncDossierPromoCodeResponse;
import com.cflint.services.impl.AsyncDossierResponse;

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
