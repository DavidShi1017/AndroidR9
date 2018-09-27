package com.cfl.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cfl.model.OfferQuery;
import com.cfl.model.OfferQuery.TravelType;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class OfferQueryJsonSerializer implements JsonSerializer<OfferQuery> {
	
	private boolean isSavingLastOfferQuery;
	
	public OfferQueryJsonSerializer(boolean isSavingLastOfferQuery){
		this.isSavingLastOfferQuery = isSavingLastOfferQuery;
	}

    public JsonElement serialize(OfferQuery offerQuery, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("ComfortClass", context.serialize(offerQuery.getComforClass()));
        object.add("DepartureQueryParameters", context.serialize(offerQuery.getDepartureQueryParameters()));
        object.add("DestinationStationName", context.serialize(offerQuery.getDestinationStationDestinationName()));
        object.add("DestinationStationRCode", context.serialize(offerQuery.getDestinationStationRCode()));
        object.add("DestinationStationSynonymeName", context.serialize(offerQuery.getDestinationStationSynonymeName()));
        object.add("OriginStationName", context.serialize(offerQuery.getOriginStationDestinationName()));
        object.add("OriginStationRCode", context.serialize(offerQuery.getOriginStationRCode()));
        object.add("OriginStationSynonymeName", context.serialize(offerQuery.getOriginStationSynonymeName()));
        if (offerQuery.getTravelType() == TravelType.RETURN) {
        	object.add("ReturnQueryParameters", context.serialize(offerQuery.getReturnQueryParameters()));
	     }
        
        if (offerQuery.getTravelPartyMembers() != null && offerQuery.getTravelPartyMembers().size() > 0) {
        	 object.add("TravelParty", context.serialize(offerQuery.getTravelPartyMembers()));
		}else {
			object.add("TravelParty", context.serialize(null));
		}
        if (offerQuery.getListCorporateCards() != null ) {
        	 object.add("CorporateCards", context.serialize(offerQuery.getListCorporateCards()));
		}
       
        object.add("TravelType", context.serialize(offerQuery.getTravelType()));
        object.add("PreferredCurrency", context.serialize(offerQuery.getPreferredCurrency()));
        if (offerQuery.isHasGreenPointsNumber()) {
        	object.add("GreenPointsNumber", context.serialize(offerQuery.getGreenPointsNumber()));
		}   
            
       // object.add("MultiSegment", context.serialize(offerQuery.isMultiSegment()));
        object.add("ODScope", context.serialize(offerQuery.getODScope()));
        String language = "";
        if(offerQuery.getTicketLanguage() != null && !offerQuery.getTicketLanguage().equals("")){
            language = offerQuery.getTicketLanguage().substring(0,2);
            object.add("TicketLanguage", context.serialize(language));
        }
       
        if (isSavingLastOfferQuery) {
        	object.add("IsFavorite", context.serialize(offerQuery.isFavorite()));
        	object.add("CreateDate", context.serialize(offerQuery.getCreateDate()));
            object.add("LastUsedDate", context.serialize(offerQuery.getLastUsedDate()));
		}
        
        return object;
    }

}
