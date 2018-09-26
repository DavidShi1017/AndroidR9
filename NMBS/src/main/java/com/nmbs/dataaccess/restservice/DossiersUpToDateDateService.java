package com.nmbs.dataaccess.restservice;

import android.content.Context;


import com.nmbs.R;
import com.nmbs.dataaccess.converters.DossierUpToDateConverter;
import com.nmbs.model.DossiersUpToDateParmeters;
import com.nmbs.model.DossiersUpToDateResponse;
import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.ObjectToJsonUtils;

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
