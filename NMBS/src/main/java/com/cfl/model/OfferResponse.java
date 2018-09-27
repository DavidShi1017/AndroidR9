package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * The class OfferResponse is a sub-class of the RestResponse class. 
 * This class contains the actual response payload sent by the server. 
 */
public class OfferResponse extends RestResponse implements Serializable{
	
	private static final long serialVersionUID = 1186166853507065555L;
	private List<Travel> travelList;
	private CombinationMatrix combinationRestrictions;
	private List<String> flexInfoKeys;
	private List<String> flexInfoValues;
	private List<UserMessage> userMessages;
	private String OriginStationName;
	private String DestinationStationName;
	private boolean showDefaultCollapsed;
	public List<Travel> getTravelList() {
		return travelList;
	}
	public CombinationMatrix getCombinationRestrictions() {
		return combinationRestrictions;
	}
	
	public OfferResponse(List<Travel> travelList,
			CombinationMatrix combinationRestrictions, List<UserMessage> userMessages, boolean showDefaultCollapsed, String OriginStationName, String DestinationStationName) {		
		this.travelList = travelList;
		this.combinationRestrictions = combinationRestrictions;
		this.userMessages = userMessages;
		this.OriginStationName = OriginStationName;
		this.DestinationStationName = DestinationStationName;
		this.showDefaultCollapsed = showDefaultCollapsed;
	}
	
	public OfferResponse() {		
		
	}
	
	
	public List<UserMessage> getUserMessages() {
		return userMessages;
	}
	public boolean addTravel(Travel object) {
		return travelList.add(object);
	}
	
	public void convertMapToList(Tariff tariff){
		Map<String, String> flexInfo = tariff.getFlexInfo();
		
		
	/*		Iterator<String> iter = tariff.getFlexInfo().keySet()
			.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				
				if (!flexInfo.containsKey(key)) {
					flexInfo.put(key, tariff.getFlexInfo().get(key));
					Log.d("DepartureDetailActivity",
							"HashMap - key is " + key);
				}				
			}*/
		
		/*flexInfo = selectedOffer.getOfferItems().get(0)
		.getTariffs().get(0).getFlexInfo();*/
		
		Iterator<String> iter = tariff.getFlexInfo().keySet().iterator();
		flexInfoKeys = new ArrayList<String>();
		flexInfoValues = new ArrayList<String>();
		
		while (iter.hasNext()) {	
			String key = iter.next();
			flexInfoKeys.add(key);
			flexInfoValues.add(flexInfo.get(key));
			Log.d("DepartureDetailActivity",
					"HashMap - key is " + key
							+ ", value is "
							+ flexInfo.get(key));

		}

	}
	public List<String> getFlexInfoKeys() {
		return flexInfoKeys;
	}
	public List<String> getFlexInfoValues() {
		return flexInfoValues;
	}
	public String getOriginStationName() {
		return OriginStationName;
	}
	public String getDestinationStationName() {
		return DestinationStationName;
	}
	
	public boolean isShowDefaultCollapsed() {
		return showDefaultCollapsed;
	}
}
