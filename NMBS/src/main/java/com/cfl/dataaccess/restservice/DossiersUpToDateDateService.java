package com.cfl.dataaccess.restservice;

import android.content.Context;


import com.cfl.R;
import com.cfl.dataaccess.converters.DossierUpToDateConverter;
import com.cfl.model.DossiersUpToDateParmeters;
import com.cfl.model.DossiersUpToDateResponse;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.ObjectToJsonUtils;

public class DossiersUpToDateDateService {
	private static final String TAG = DossiersUpToDateDateService.class.getSimpleName();
	
	public DossiersUpToDateResponse executeDossiersUpToDate(DossiersUpToDateParmeters dossiersUpToDateParmeters,
			Context context, String languageBeforSetting) throws Exception{
		
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();

		String postJsonString = ObjectToJsonUtils.getDossiersUpToDateParmeters(dossiersUpToDateParmeters);
		//Log.d(TAG, "DossiersUpToDate...Parmeters...postJsonString.... " + postJsonString);
		String urlString = context.getString(R.string.server_url_dossier_updodate);
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		//Log.d(TAG, "DossiersUpToDate...Parmeters...response.... " + response);

		DossierUpToDateConverter dossierUpToDateConverter = new DossierUpToDateConverter();
		DossiersUpToDateResponse dossiersUpToDateResponse = dossierUpToDateConverter.parse(response);
		//Log.d(TAG, "Converter DossierUpToDate is finished... ");
		return dossiersUpToDateResponse;
	}
}
