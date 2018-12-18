package com.cflint.dataaccess.restservice;

import android.content.Context;


import com.cflint.R;
import com.cflint.dataaccess.converters.DossierUpToDateConverter;
import com.cflint.model.DossiersUpToDateParmeters;
import com.cflint.model.DossiersUpToDateResponse;
import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.ObjectToJsonUtils;

public class DossiersUpToDateDateService {
	private static final String TAG = DossiersUpToDateDateService.class.getSimpleName();
	
	public DossiersUpToDateResponse executeDossiersUpToDate(DossiersUpToDateParmeters dossiersUpToDateParmeters,
			Context context, String languageBeforSetting) throws Exception{
		
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();

		String postJsonString = ObjectToJsonUtils.getDossiersUpToDateParmeters(dossiersUpToDateParmeters);
		//Log.d(TAG, "DossiersUpToDate...Parmeters...postJsonString.... " + postJsonString);
		String urlString = context.getString(R.string.server_url_dossier_updodate);
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		//Log.d(TAG, "DossiersUpToDate...Parmeters...response.... " + response);

		DossierUpToDateConverter dossierUpToDateConverter = new DossierUpToDateConverter();
		DossiersUpToDateResponse dossiersUpToDateResponse = dossierUpToDateConverter.parse(response);
		//Log.d(TAG, "Converter DossierUpToDate is finished... ");
		return dossiersUpToDateResponse;
	}
}
