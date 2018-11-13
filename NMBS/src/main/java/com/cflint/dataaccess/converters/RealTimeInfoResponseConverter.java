package com.cflint.dataaccess.converters;

import com.cflint.exceptions.InvalidJsonError;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.services.impl.DossierDetailsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse JsonString to Object.
 * 
 */
public class RealTimeInfoResponseConverter {

	
	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return StationBoardResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public List<RealTimeInfoResponse> parseRealTimeResponse(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}
		List<RealTimeInfoResponse> realTimeInfoResponses = new ArrayList<RealTimeInfoResponse>();
		JSONArray responseDataArray = null;
		JSONObject reponseDataItemObject = null;
		JSONObject jsonObject = new JSONObject(jsonString);
		if (jsonObject.has("Responses")) {
			responseDataArray = jsonObject.getJSONArray("Responses");
			RealTimeInfoResponse realTimeInfoResponse = null;
			for (int i = 0; i < responseDataArray.length(); i++) {
				reponseDataItemObject = responseDataArray.getJSONObject(i);

				String id = reponseDataItemObject.getString("Id");
				String success = reponseDataItemObject.getString("CallSuccessfull");
				if(reponseDataItemObject.has("Connection")){
					String content = reponseDataItemObject.getString("Connection");

					realTimeInfoResponse = new RealTimeInfoResponse(id, Boolean.valueOf(success),content, DossierDetailsService.Dossier_Realtime_Connection);
					realTimeInfoResponses.add(realTimeInfoResponse);

				}else if(reponseDataItemObject.has("TravelSegment")){
					String content = reponseDataItemObject.getString("TravelSegment");
					realTimeInfoResponse = new RealTimeInfoResponse(id, Boolean.valueOf(success),content, DossierDetailsService.Dossier_Realtime_Segment);
					realTimeInfoResponses.add(realTimeInfoResponse);
				}else{
					realTimeInfoResponse = new RealTimeInfoResponse(id, Boolean.valueOf(success), "", DossierDetailsService.Dossier_Realtime_Segment);
					realTimeInfoResponses.add(realTimeInfoResponse);
				}
			}
		}
		return realTimeInfoResponses;
	}
}