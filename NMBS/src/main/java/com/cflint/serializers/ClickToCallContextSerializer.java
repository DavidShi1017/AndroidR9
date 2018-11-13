package com.cflint.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cflint.model.ClickToCallContext;
import com.cflint.util.DateUtils;

import java.lang.reflect.Type;


/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:David
 */
public class ClickToCallContextSerializer implements JsonSerializer<ClickToCallContext> {

    public JsonElement serialize(ClickToCallContext clickToCallContext, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Dnr", context.serialize(clickToCallContext.getDnr() == null? "" : clickToCallContext.getDnr()));
        object.add("Scenario", context.serialize(clickToCallContext.getScenario() == null? "" : clickToCallContext.getScenario()));
        object.add("Language", context.serialize(clickToCallContext.getLanguage() == null? "" : clickToCallContext.getLanguage()));
        //object.add("OS", context.serialize(clickToCallContext.getOS() == null? "" : clickToCallContext.getOS()));
       // object.add("Device", context.serialize(clickToCallContext.getDevice() == null? "" : clickToCallContext.getDevice()));
        object.add("TimeStamp", context.serialize(clickToCallContext.getTimeStamp() == null? "" : DateUtils.getDateWithTimeZone(clickToCallContext.getTimeStamp())));
        /*if (clickToCallContext.getComfortClass() != null) {
        	object.add("ComfortClass", context.serialize(clickToCallContext.getComfortClass()));
		}*/
        
        /*if (clickToCallContext.getDepartureQueryParameters() != null  && clickToCallContext.getDepartureQueryParameters().getDateTime() != null) {
        	 object.add("DepartureQueryParameters", context.serialize(clickToCallContext.getDepartureQueryParameters()));
		}       
        object.add("DestinationStationName", context.serialize(clickToCallContext.getDestinationStationName() == null? "" : clickToCallContext.getDestinationStationName()));
        object.add("DestinationStationRCode", context.serialize(clickToCallContext.getDestinationStationRCode() == null? "" : clickToCallContext.getDestinationStationRCode()));
        object.add("OriginStationName", context.serialize(clickToCallContext.getOriginStationName() == null? "" : clickToCallContext.getOriginStationName()));
        object.add("OriginStationRCode", context.serialize(clickToCallContext.getOriginStationRCode() == null? "" : clickToCallContext.getOriginStationRCode()));
        if (clickToCallContext.getReturnQueryParameters() != null && clickToCallContext.getReturnQueryParameters().getDateTime() != null) {
        	 object.add("ReturnQueryParameters", context.serialize(clickToCallContext.getReturnQueryParameters()));
		}  
       
        if(clickToCallContext.getGreenPointsNumber() != null && !StringUtils.equalsIgnoreCase("", clickToCallContext.getGreenPointsNumber())){
        	object.add("GreenPointsNumber", context.serialize(clickToCallContext.getGreenPointsNumber()));
        }
        if(clickToCallContext.getCorporateCards() != null && clickToCallContext.getCorporateCards().size() > 0){
        	 object.add("CorporateCards", context.serialize(clickToCallContext.getCorporateCards()));
        }*/
       
        
        /*
        object.add("TravelParty", context.serialize(clickToCallContext.getTravelParty()));
        if (clickToCallContext.getTravelType() != null) {
        	object.add("TravelType", context.serialize(clickToCallContext.getTravelType()));
		}*/
        
        
        return object;
    }

}
