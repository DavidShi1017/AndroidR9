package com.cfl.services.impl;

import android.content.Context;

import com.cfl.dataaccess.database.RealTimeInfoDatabaseService;
import com.cfl.dataaccess.restservice.impl.DossierDataService;
import com.cfl.model.CustomerParameter;
import com.cfl.model.DeliveryMethod;
import com.cfl.model.DeliveryMethodParameter;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossierResponse;
import com.cfl.model.OfferQuery;
import com.cfl.model.Passenger;
import com.cfl.model.PassengerReferenceParameter;
import com.cfl.model.PromoParameter;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.RealTimeInfoResponse;
import com.cfl.services.IDossierService;
import com.cfl.services.IOfferService;
import com.cfl.services.ISettingService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DossierService implements IDossierService {

	private Context context;
	public static final int DOSSIER_CREATE = 0;
	public static final int DOSSIER_UPDATE_TRAIN = 1;
	public static final int DOSSIER_UPDATE_INSURANCE_DELIVERYMETHOD = 2;
	public static final int DOSSIER_UPDATE_CUSTOMER_PAYMENT  = 3;
	public static final int DOSSIER_INIT_PAYMENT  = 4;
	public static final int DOSSIER_REFRESH_PAYMENT  = 5;
	public static final int DOSSIER_REFRESH_ORDER_STATE  = 6;
	public static final int DOSSIER_ABORT_PAYMENT  = 7;
	public static final int DOSSIER_DELETE = 8;
	public static final int DOSSIER_REFRESH_CONFIRMATION = 9;
	//private static final String TAG = DossierService.class.getSimpleName();
	public static String GUID;
	public static final String PAYMENT_OK_URL = "https://secure.ogone.com/ncol/test/order_Agree.asp" ;
	public static final String PAYMENT_CANCEL_URL = "https://secure.ogone.com/ncol/test/Order_Cancel.asp" ;
	public static String selectedDeliveryMethod;
	private DossierParameter dossierParameter;
	
	public static final int DOSSIER_ADD_PROMO_CODE = 0;
	public static final int DOSSIER_REMOVE_PROMO_CODE = 1;
	private PromoParameter promoParameter;
	
	public DossierService(Context context) {
		this.context = context;
	}
	
	public  AsyncDossierResponse searchDossier(DossierParameter dossierParameter, ISettingService settingService){
		
		AsyncDossierResponse response = new AsyncDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_CREATE);
		return response;
	}

	public AsyncDossierResponse UpdateDossier(
			DossierParameter dossierParameter, ISettingService settingService, int optionFlag) {
		AsyncDossierResponse response = new AsyncDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), optionFlag);
		return response;
		
	}
	
	public AsyncDossierResponse deleteDossier(
			DossierParameter dossierParameter, ISettingService settingService) {
		AsyncDossierResponse response = new AsyncDossierResponse();
		//response.registerReceiver(context);
		//Log.d(TAG, "START DELETE DOSSIER.....");
		setPromoParameter(null);
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_DELETE);
		return response;
		
	}
	
	public AsyncAbortPaymentDossierResponse AbortPayment(
			DossierParameter dossierParameter, ISettingService settingService) {
		AsyncAbortPaymentDossierResponse response = new AsyncAbortPaymentDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_ABORT_PAYMENT);
		return response;
		
	}

	public AsyncDossierResponse refreshPaymentStatus(
			DossierParameter dossierParameter, ISettingService settingService) {
		AsyncDossierResponse response = new AsyncDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_REFRESH_PAYMENT);
		return response;
		
	}
	public AsyncDossierResponse initPayment(
			DossierParameter dossierParameter, ISettingService settingService) {
		AsyncDossierResponse response = new AsyncDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_INIT_PAYMENT);
		return response;
		
	}
	public AsyncDossierResponse refreshOrderState(
			DossierParameter dossierParameter, ISettingService settingService) {
		AsyncDossierResponse response = new AsyncDossierResponse();
		response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_REFRESH_ORDER_STATE);
		return response;
		
	}
	
	public AsyncDossierPromoCodeResponse addPromoCode(PromoParameter promo,ISettingService settingService) {
		AsyncDossierPromoCodeResponse response = new AsyncDossierPromoCodeResponse();
		response.registerReceiver(context);
		DossierPromoCodeIntentService.startService(context, promo, settingService.getCurrentLanguagesKey(), DOSSIER_ADD_PROMO_CODE);
		return response;
	}

	public AsyncDossierPromoCodeResponse removePromoCode(PromoParameter promo,ISettingService settingService) {
		AsyncDossierPromoCodeResponse response = new AsyncDossierPromoCodeResponse();
		response.registerReceiver(context);
		DossierPromoCodeIntentService.startService(context, promo, settingService.getCurrentLanguagesKey(), DOSSIER_REMOVE_PROMO_CODE);
		return response;
	}

	public void setPromoParameter(PromoParameter promoParameter) {
		this.promoParameter = promoParameter;
		
	}

	public PromoParameter getPromoParameter() {
		
		return promoParameter;
	}
	
	public void refreshConfirmation(
			DossierParameter dossierParameter, ISettingService settingService) {
		//AsyncDossierResponse response = new AsyncDossierResponse();
		//response.registerReceiver(context);
		
		DossierIntentService.startService(context, dossierParameter, settingService.getCurrentLanguagesKey(), DOSSIER_REFRESH_CONFIRMATION);
		//return response;
		
	}
	
	public DossierParameter getDossierParameter() {
		if (this.dossierParameter == null) {
			this.dossierParameter = new DossierParameter();
		}
		return this.dossierParameter;
	}
	
	
	public void setDossierParameter(DossierParameter dossierParameter) {
		this.dossierParameter = dossierParameter;
	}

	/**
	 * Clear OfferQuery when the application is destroyed.
	 *
	 */
	public void clearDossierParameter() {
		this.dossierParameter = null;
	}
	
	public void createDeliveryMethodParameterForDossierParameter(DossierResponse dossierResponse, DeliveryMethod deliveryMethod){
		DeliveryMethodParameter deliveryMethodParameter ;
		if (getDossierParameter().getDeliveryMethod() == null) {
			deliveryMethodParameter = new DeliveryMethodParameter();
		}else {
			deliveryMethodParameter = getDossierParameter().getDeliveryMethod();
		}
		deliveryMethodParameter.setMethod(deliveryMethod.getMethod());
		List<Passenger> passengers = new ArrayList<Passenger>();
		if(dossierResponse != null){
			passengers = dossierResponse.getPassengers();
		}else{
			/*Passenger passenger = new Passenger("AA", PersonType.ADULT, "", "","", new ArrayList<String>(), new ArrayList<FtpCard>());
			passengers.add(passenger);*/
			//passengers = myState.dossierResponse.getPassengers();
		}
		
		List<PassengerReferenceParameter> passengerReferenceParameters;
		if (deliveryMethodParameter.getPassengers() == null) {
			passengerReferenceParameters = new ArrayList<PassengerReferenceParameter>();
		}else {
			passengerReferenceParameters = deliveryMethodParameter.getPassengers();
		}
		CustomerParameter customerParameter = new CustomerParameter();
		if (passengerReferenceParameters.size() == 0) {
			for(Passenger passenger : passengers){
				PassengerReferenceParameter passengerReferenceParameter = new PassengerReferenceParameter(passenger.getId(), null, null, null);
				passengerReferenceParameters.add(passengerReferenceParameter);
			}
		}
		
		
		customerParameter.setPassengerInfo(passengerReferenceParameters);
		deliveryMethodParameter.setPassengers(passengerReferenceParameters);
		
		getDossierParameter().setCustomer(customerParameter);
		//Log.e("hasInsurance is ", getDossierParameter().isInsurance()+"");
		//dossierService.getDossierParameter().setInsurance(Boolean.valueOf(insuranceValue.getText().toString()));
		//Log.e("hasInsurance is ", dossierService.getDossierParameter().isInsurance()+"");
		getDossierParameter().setDeliveryMethod(deliveryMethodParameter);
	}

	public void cleanDossierParameter(IOfferService offService) {
		dossierParameter = null;
		offService.setAddOptionalReservationsForDeparture(false);
		offService.setAddOptionalReservationsForReturn(false);
	}
	
	
	public String buildPassengersText(Passenger passenger, int position, Context context, OfferQuery offerQuery){
		String passengersText = "";
		if (passenger != null) {	
			
			/*passengersText = context.getString(R.string.general_passenger)  
					+ " " 
					+ String.valueOf(position + 1) 
					+ " - " 
					+ offerQuery.getPersonTypeName(passenger.getPassengerType(), context, 0);*/
					
			List<String> reductionCards = passenger.getReductionCards();	
			if (reductionCards != null && reductionCards.size() > 0) {
				String cardNumber = "";
				for (int i = 0; i < reductionCards.size(); i++) {

					if (i == reductionCards.size() - 1) {

						cardNumber += reductionCards.get(i);
					} else {
						if (!StringUtils.isEmpty(reductionCards.get(i))) {
							cardNumber += reductionCards.get(i) + ", ";
						}
						
					}
				}
				if (!StringUtils.isEmpty(cardNumber)) {
					cardNumber = " - " + cardNumber;
				}
				passengersText = passengersText + cardNumber;
			}			
		}
		
		return passengersText ;
	}


}
