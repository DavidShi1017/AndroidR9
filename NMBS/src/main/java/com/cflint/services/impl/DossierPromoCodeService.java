package com.cflint.services.impl;


import android.content.Context;



import com.cflint.model.PromoParameter;



import com.cflint.services.ISettingService;

/*public class DossierPromoCodeService implements IDossierPromoCodeService {

	private Context context;
	public static final int DOSSIER_ADD_PROMO_CODE = 0;
	public static final int DOSSIER_REMOVE_PROMO_CODE = 1;
	private PromoParameter promoParameter;
	//private static final String TAG = DossierService.class.getSimpleName();

	
	public DossierPromoCodeService(Context context) {
		this.context = context;
	}

	public AsyncDossierPromoCodeResponse addPromoCode(PromoParameter promo,ISettingService settingService) {
		AsyncDossierPromoCodeResponse response = new AsyncDossierPromoCodeResponse();
		response.registerReceiver(context);
		DossierPromoCodeIntentService.startService(context, promo, settingService.getCurrentLanguage(), DOSSIER_ADD_PROMO_CODE);
		return response;
	}

	public AsyncDossierPromoCodeResponse removePromoCode(PromoParameter promo,ISettingService settingService) {
		AsyncDossierPromoCodeResponse response = new AsyncDossierPromoCodeResponse();
		response.registerReceiver(context);
		DossierPromoCodeIntentService.startService(context, promo, settingService.getCurrentLanguage(), DOSSIER_REMOVE_PROMO_CODE);
		return response;
	}

	public void setPromoParameter(PromoParameter promoParameter) {
		this.promoParameter = promoParameter;
		
	}

	public PromoParameter getPromoParameter() {
		
		return promoParameter;
	}

}*/
