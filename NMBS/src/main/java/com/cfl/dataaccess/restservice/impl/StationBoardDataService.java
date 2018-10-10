package com.cfl.dataaccess.restservice.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;


import com.cfl.R;

import com.cfl.dataaccess.converters.AssistantConverter;
import com.cfl.dataaccess.converters.CustomErrorMessager;
import com.cfl.dataaccess.converters.MasterResponseConverter;
import com.cfl.dataaccess.converters.StationBoardBulkConverter;
import com.cfl.dataaccess.database.AssistantDatabaseService;
import com.cfl.dataaccess.database.StationBoardDatabaseService;
import com.cfl.dataaccess.restservice.IStationBoardDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.DossierResponse.OrderItemStateType;
import com.cfl.model.SeatLocationForOD.Direction;
import com.cfl.model.Order;
import com.cfl.model.Station;
import com.cfl.model.StationBoard;
import com.cfl.model.StationBoardBulk;
import com.cfl.model.StationBoardBulkResponse;
import com.cfl.model.StationBoardCollection;
import com.cfl.model.StationBoardLastQuery;
import com.cfl.model.StationBoardQuery;
import com.cfl.model.StationBoardResponse;
import com.cfl.model.StationResponse;
import com.cfl.model.TravelRequest.TimePreference;
import com.cfl.model.TravelSegment;
import com.cfl.util.ComparatorDate;
import com.cfl.util.ComparatorStationBoardDate;
import com.cfl.util.DateUtils;
import com.cfl.util.FileManager;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.ObjectToJsonUtils;
import com.cfl.util.TrainTypeConstant;

public class StationBoardDataService extends CustomErrorMessager implements IStationBoardDataService{

	public static final String STATIONBOARD_TYPE_A = "A";
	private static final String STATIONBOARD_TYPE_B = "B";
	private static final String STATIONBOARD_TYPE_C = "C";
	private static final String STATIONBOARD_TYPE_D = "D";
	private static final String STATIONBOARD_TYPE_UNKNOWN = "Unknown";
	
	private static final String CREATE_STATIONBOARD_STATUS = "CreateStationBoardStatus";
	private static final String STATUS = "Status";
	
	private static final String TAG = StationBoardDataService.class.getSimpleName();
	private List<StationBoard> stationBoards;
	private String currentLanguage;
	private List<Station> stationList;
	private Map<String, String> trainMap;

    public void createStationBoard(Context context, DossierAftersalesResponse dossierAftersalesResponse, 
    		String currentLanguage) throws JSONException, InvalidJsonError{
        if (dossierAftersalesResponse == null || dossierAftersalesResponse.getState() != OrderItemStateType.OrderItemStateTypeConfirmed){
            return;
        }

        List<TravelSegment> travelSegmens = dossierAftersalesResponse.getTravelSegments();
        //Log.d(TAG, "travelSegmens size is ..." + travelSegmens.size());
        
        stationBoards = new ArrayList<StationBoard>();

        for (TravelSegment ts : travelSegmens){
            //BulkQueryRequestElementView bulkQueryRequestElementView = null;
        	//Log.d(TAG, "TravelSegment id is....." + ts.getId());
        	StationBoard stationBoard = null;
            if (checkStationCodeInHacoOrNHAFAS(context, ts.getOriginCode(), currentLanguage)){
            	if (StringUtils.isEmpty(ts.getParentId())) {
            		//Log.d(TAG, "ParentId is Empty....." );
					if (isNoChild(ts, travelSegmens)) {
						//Log.d(TAG, "isNoChild....." );
						if (!StringUtils.isEmpty(checkTrainCategory(context, ts.getTrainType()))){
	                        //if (checkTravelSegmentAllData(ts)){
	                            // create A Type
								if (ts.isHasReservation()) {
									stationBoard = createStationBoardTypeA(context, ts, dossierAftersalesResponse.getDnrId());
		                        	
		                            stationBoards.add(stationBoard);
								}else {
									stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId(), travelSegmens);
		                        	
		                            stationBoards.add(stationBoard);
								}
	                        	
	                            
	                        //}
	                    }else{
	                        if (ts.isHasReservation()){
	                            //if (checkTravelSegmentAllData(ts)){
	                                // create D Type
	                            	stationBoard = createStationBoardTypeD(context, ts, dossierAftersalesResponse.getDnrId());
	                            	
	                                stationBoards.add(stationBoard);
	                                
	                            //}
	                        }else{
	                            if (checkTravelSegmentValidityStartDate(ts)){
	                                // create B Type
	                            	stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId(), travelSegmens);
	                            	
	                                stationBoards.add(stationBoard);
	                                
	                            }

	                        }
	                    }
					}else {
						System.out.println("has======" + originCodeDifferentFromAllChildTravelSegments(ts, travelSegmens));
						if(originCodeDifferentFromAllChildTravelSegments(ts, travelSegmens)){
							stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId(), travelSegmens);
                        	
                            stationBoards.add(stationBoard);
						}
					}
				}else {
					
                    	if (!StringUtils.isEmpty(checkTrainCategory(context, ts.getTrainType()))) {
                    		stationBoard = createStationBoardTypeA(context, ts, dossierAftersalesResponse.getDnrId());
                        	
                            stationBoards.add(stationBoard);
    					}else {
    						stationBoard = createStationBoardTypeD(context, ts, dossierAftersalesResponse.getDnrId());
                        	
                            stationBoards.add(stationBoard);
						}
					
                    	if (childDestinationCodeDifferentParentDestination(ts, travelSegmens)){
                    		System.out.println("ts id======" + ts.getId());
                    		System.out.println("childDestinationCodeDifferentParentDestination======" + originCodeDifferentFromAllChildTravelSegments(ts, travelSegmens));
                            if (childDestinationCodeDifferentOtherChildOriginCodeLinkedSameParent(ts, travelSegmens)){
                            	System.out.println("childDestinationCodeDifferentOtherChildOriginCode======" + originCodeDifferentFromAllChildTravelSegments(ts, travelSegmens));
                                // create C Type
                            	stationBoard = createStationBoardTypeC(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                            }
                        }
					
				}
            	

            	
                /*if (ts.getParentId() != null && !StringUtils.isEmpty(ts.getParentId())){
                    if (!StringUtils.isEmpty(checkTrainCategory(context, ts.getTrainType()))){
                        //if (checkTravelSegmentAllData(ts)){
                            // create A Type
                        	stationBoard = createStationBoardTypeA(context, ts, dossierAftersalesResponse.getDnrId());
                        	
                            stationBoards.add(stationBoard);
                            
                        //}
                    }else{
                        if (ts.isHasReservation()){
                            //if (checkTravelSegmentAllData(ts)){
                                // create D Type
                            	stationBoard = createStationBoardTypeD(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                            //}
                        }else{
                            if (checkTravelSegmentValidityStartDate(ts)){
                                // create B Type
                            	stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                            }

                        }
                    }
                } else{
                    if (childOriginCodeSametParentOriginCode(ts, travelSegmens)){
                        if (!StringUtils.isEmpty(checkTrainCategory(context, ts.getTrainType()))){
                            //if (checkTravelSegmentAllData(ts)){
                                // create A Type
                            	stationBoard = createStationBoardTypeA(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                           // }
                        }else{
                            //if (checkTravelSegmentAllData(ts)){
                                // create D Type
                            	stationBoard = createStationBoardTypeD(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                            //}
                        }
                    }else{
                        if (!StringUtils.isEmpty(checkTrainCategory(context, ts.getTrainType()))){
                            //if (checkTravelSegmentAllData(ts)){
                                // create A Type
                        	 
                        	 if (ts.isHasReservation()){
                        		 stationBoard = createStationBoardTypeA(context, ts, dossierAftersalesResponse.getDnrId());
                             	
                                 stationBoards.add(stationBoard);
                        	 }else{
                                 if (checkTravelSegmentValidityStartDate(ts)){
                                     // create B Type
                                 	stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId());
                                 	
                                     stationBoards.add(stationBoard);
                                     
                                 }
                             }                           	                               
                            //}
                        }else{
                            if (ts.isHasReservation()){
                                //if (checkTravelSegmentAllData(ts)){
                                    // create D Type
                                	stationBoard = createStationBoardTypeD(context, ts, dossierAftersalesResponse.getDnrId());
                                	
                                    stationBoards.add(stationBoard);
                                    
                                //}
                            }else{
                                if (checkTravelSegmentValidityStartDate(ts)){
                                    // create B Type
                                	stationBoard = createStationBoardTypeB(context, ts, dossierAftersalesResponse.getDnrId());
                                	
                                    stationBoards.add(stationBoard);
                                    
                                }

                            }
                        }
                    }
                }
            } else{
                // Origin code not StationEnabled
                if (ts.getParentId() != null && !StringUtils.isEmpty(ts.getParentId())){
                    if (checkStationCodeInHacoOrNHAFAS(context, ts.getDestinationCode(), currentLanguage)){
                        if (childDestinationCodeDifferentParentDestination(ts, travelSegmens)){
                            if (childDestinationCodeDifferentOtherChildOriginCode(ts, travelSegmens)){
                                // create C Type
                            	stationBoard = createStationBoardTypeC(context, ts, dossierAftersalesResponse.getDnrId());
                            	
                                stationBoards.add(stationBoard);
                                
                            }else {
                            	stationBoard = createStationBoardTypeUnknownHasReservation(context, ts, dossierAftersalesResponse.getDnrId());
                            	stationBoards.add(stationBoard);
							}
                        }else {
                        	stationBoard = createStationBoardTypeUnknownHasReservation(context, ts, dossierAftersalesResponse.getDnrId());
                        	stationBoards.add(stationBoard);
						}
                    }else {
                    	stationBoard = createStationBoardTypeUnknownHasReservation(context, ts, dossierAftersalesResponse.getDnrId());
                    	stationBoards.add(stationBoard);
					}
                }else {
                	
                	stationBoard = createStationBoardTypeUnknownHasReservation(context, ts, dossierAftersalesResponse.getDnrId());
                	stationBoards.add(stationBoard);
				}*/
            }

            
        }
        
        if (stationBoards.size() > 0) {
        	StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
        	stationBoardDatabaseService.deleteStationBoardBulkById(dossierAftersalesResponse.getDnrId());
        	Map<String, List<StationBoard>> sameStationBoardsMap = stationBoardDatabaseService.insertStationBoards(stationBoards);
        	
        	if (sameStationBoardsMap != null) {
        		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(context);
        		for(Map.Entry<String, List<StationBoard>> entry : sameStationBoardsMap.entrySet())    { 
        			List<StationBoard> sameStationBoards = entry.getValue();
        			for (StationBoard sameStationBoard : sameStationBoards) {
        				assistantDatabaseService.updateOrdersRelationStationboard(sameStationBoard.getTravelSegmentID(), entry.getKey());
					}
        		    //System.out.println(entry.getKey()+": "+entry.getValue());    
        		}
        		
			}
        	
        	//updateOrdersRelationStationboard
		}

    }
    
    private boolean isNoChild(TravelSegment ts, List<TravelSegment> travelSegmens){
    	for (TravelSegment travelSegment : travelSegmens) {
    		if(StringUtils.equalsIgnoreCase(travelSegment.getParentId(), ts.getId())){
    			return false;
    		}
		}
    	return true;
    }
    
    private boolean originCodeDifferentFromAllChildTravelSegments(TravelSegment ts, List<TravelSegment> travelSegmens){
    	boolean has = false;
    	for (TravelSegment travelSegment : travelSegmens) {
			if (StringUtils.equalsIgnoreCase(ts.getId(), travelSegment.getParentId())) {
				if (StringUtils.equalsIgnoreCase(ts.getOriginCode(), travelSegment.getOriginCode())) {
					has = true;
				}
			}
		}
    	
    	return !has;
    }
    
    public StationBoardCollection getStationBoardWithTypeIsA(Context context, String dnr){
    	List<StationBoard> stationBoardList = new ArrayList<StationBoard>();
    	/*if (stationBoards != null) {
    		for (StationBoard stationBoard : stationBoards) {
    			if (StringUtils.equalsIgnoreCase(stationBoard.getType(), STATIONBOARD_TYPE_A)) {
    				stationBoardList.add(stationBoard);
    			}
    		}
		}else {*/
			StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
			stationBoardList = stationBoardDatabaseService.selectStationBoardsCollection(true, dnr);
		//}
    	
    	StationBoardCollection stationBoardCollection = new StationBoardCollection(stationBoardList);
    	return stationBoardCollection;
    }
    
    
	public StationBoardResponse executeStationBoard(StationBoardQuery stationBoardQuery, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError, 
			DBooking343Error, CustomError, DBookingNoSeatAvailableError {
		StationBoardResponse stationBoardResponse = null;
	/*	String postJsonString = "\t\t{\n" +
				"\t\t\t\"StationRCode\":\"BEBMI\",\n" +
				"\t\t\t\t\"DateTime\":\"2016-01-20T00:00:00\",\n" +
				"\t\t\t\t\"TimePreference\":\"Arrival\",\n" +
				"\t\t\t\t\"Trains\":[\n" +
				"\t\t\t{\n" +
				"\t\t\t\t\"Category\":\"THA\",\n" +
				"\t\t\t\t\t\"Number\":\"9198\"\n" +
				"\t\t\t},\n" +
				"\t\t\t{\n" +
				"\t\t\t\t\"Category\":\"THA\",\n" +
				"\t\t\t\t\t\"Number\":\"9192\"\n" +
				"\t\t\t}\n" +
				"\t\t\t]\n" +
				"\t\t}";*/
/*		String postJsonString = "{\n" +
				"   \"StationRCode\":\"BEBMI\",\n" +
				"   \"DateTime\":\"2016-01-22T00:00:00\",\n" +
				"   \"TimePreference\":\"Arrival\",\n" +
				"   \"Trains\":[   ]\n" +
				"}";*/

		String postJsonString = ObjectToJsonUtils.getStationBoardQueryStr(stationBoardQuery);
		
		//Log.d(TAG, "Execute StationBoard.....");
		
		//Log.d(TAG, "postJsonString=======" + postJsonString);
		/*String postJsonString = "{'StationRCode':'BEBMI','DateTime':'2014-03-01T00:00:00','TimePreference':'Arrival','Trains'" +
				":[{'Category':'THA','Number':'9198'},{'Category':'THA','Number':'9192'}]}";*/
		
		String urlString = context.getString(R.string.server_url_get_stationboard_queries);
		
		//FileManager.getInstance().writeToSdCardFromString("/OfferQuery.json", postJsonString);
		//FileManager.getInstance().createExternalStoragePrivateFileFromString(context, "OfferQuery.json", postJsonString); 
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		
		String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString, 
				language, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
		
		//String response = getStationBoardRealTime(context,language,httpRestServiceCaller,postJsonString,urlString);
		
		//Log.d(TAG, "response=======" + response);
		
		AssistantConverter assistantConverter = new AssistantConverter();
		stationBoardResponse = assistantConverter.parsesStationBoard(response);
		super.throwErrorMessage(stationBoardResponse, context, "");
		if(stationBoardResponse.getStationBoardRows() == null || stationBoardResponse.getStationBoardRows().size() == 0){
			throw new TimeOutError();
		}else{
			//saveStationBoardLastQuery(stationBoardQuery, context);
		}
		return stationBoardResponse;		
	}

	public void saveStationBoardLastQuery(StationBoardQuery stationBoardQuery, Context context){
		StationBoardLastQuery lastQuery = new StationBoardLastQuery(stationBoardQuery.getStationRCode(), stationBoardQuery.getDateTime(), stationBoardQuery.getTimePreference(), stationBoardQuery.getTrains());
		lastQuery.setName(stationBoardQuery.getName());
		lastQuery.setSynoniem(stationBoardQuery.getSynoniem());
		//Log.d(TAG, "lastQuery=======" + stationBoardQuery.getName());
		String lastquery = ObjectToJsonUtils.getStationBoardLastQueryStr(lastQuery);
		FileManager.getInstance().createExternalStoragePrivateFileFromString(context, FileManager.FOLDER_STATIONBOARD, FileManager.FILE_STATIONBOARD, lastquery);
	}

/*	public String getStationBoardRealTime(Context context,String language,HTTPRestServiceCaller httpRestServiceCaller,String postJsonString,String urlString) throws ConnectTimeoutException, RequestFail, ConnectionError, BookingTimeOutError{
		InputStream is = null;
		String response = "";
		switch(TestStationBoardActivity.flag){
		case 0:
			response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString, 
					language, HTTPRestServiceCaller.HTTP_POST_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
			break;
		case 1:
			is = context.getResources().openRawResource(R.raw.realtime_200);  
			response = FileManager.getInstance().readFileWithInputStream(is);
			break;
		case 2:
			is = context.getResources().openRawResource(R.raw.realtime_404);  
			response = FileManager.getInstance().readFileWithInputStream(is);
			throw new ConnectionError();
		case 3:
			is = context.getResources().openRawResource(R.raw.realtime_510d);  
			response = FileManager.getInstance().readFileWithInputStream(is);
			break;
		case 4:
			is = context.getResources().openRawResource(R.raw.realtime_510nd);  
			response = FileManager.getInstance().readFileWithInputStream(is);
			break;
		}
		
		return response;
	}*/
	
	public int executeStationBoardBulkQuery(StationBoardCollection stationBoardCollection, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError, 
			DBooking343Error, CustomError, DBookingNoSeatAvailableError {
		StationBoardBulkResponse stationBoardBulkResponse = null;
		String postJsonString = ObjectToJsonUtils.getStationBoardStr(stationBoardCollection);
		
		//Log.d(TAG, "Execute StationBoardBulk.....");
		/*String postJsonString = "{'Elements':[{'Id':'testId1','StationRCode':'BEBMI','DateTime':'2010-11-02T00:00:00'," +
				"'TimePreference':'Departure','TrainCategory':'THA','TrainNumber':'9103'},{'Id':'testId2','StationRCode':" +
				"'BEBMI','DateTime':'2011-08-18T00:00:00','TimePreference':'Arrival','TrainCategory':'THA','TrainNumber':'9104'}]}";*/
		//Log.d(TAG, "postJsonString=======" + postJsonString);
		String urlString = context.getString(R.string.server_url_get_stationboard_bulk_queries);
		
		//FileManager.getInstance().writeToSdCardFromString("/OfferQuery.json", postJsonString);
		//FileManager.getInstance().createExternalStoragePrivateFileFromString(context, "OfferQuery.json", postJsonString); 
		
		
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString, 
				language, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
/*		for (StationBoard stationBoard : stationBoardCollection.getStationBoards()) {
			
			if (StringUtils.equalsIgnoreCase(stationBoard.getDnrStr(), "NNRVJKF")) {
				InputStream is = context.getResources().openRawResource(R.raw.test);
				
				response = FileManager.getInstance().readFileWithInputStream(is);
			}
			
		}*/
		//String response = "{\"Elements\":[{\"Id\":\"SXLGCJX_01-FRAIE-FRPLY\",\"CallSuccessFul\":false,\"Delay\":0.0,\"IsCancelled\":false},{\"Id\":\"SXLGCJX_02-FRPNO-BEBMI\",\"CallSuccessFul\":false,\"Delay\":0.0,\"IsCancelled\":false}],\"Messages\":[],\"DebugMessages\":[]}";
		//Log.d(TAG, "response=======" + response);
		
		StationBoardBulkConverter stationBoardBulkConverter = new StationBoardBulkConverter();
		stationBoardBulkResponse = stationBoardBulkConverter.parsesStationBoardBulk(response);
		super.throwErrorMessage(stationBoardBulkResponse, context, "");

		int stationBoardBulkCallErrorCount = 0;
		for (StationBoardBulk stationBoardBulk : stationBoardBulkResponse.getStationBoardBulks()) {
			if (stationBoardBulk.isCallSuccessFul() == false) {
				stationBoardBulkCallErrorCount ++;
			}
		}
		//Log.d(TAG, "stationBoardBulkCallErrorCount=======" + stationBoardBulkCallErrorCount);
		/*StationBoardBulkRealTimeDatabaseService stationBoardBulkRealTimeDatabaseService = new StationBoardBulkRealTimeDatabaseService(context);
		
		stationBoardBulkRealTimeDatabaseService.insertStationBoardBulkRealTime(stationBoardBulkResponse);*/
		
		StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
		stationBoardDatabaseService.updateStationboardRealtime(stationBoardBulkResponse);
		return stationBoardBulkCallErrorCount;		
	}
    

    private boolean checkStationCodeInHacoOrNHAFAS(Context context, String code, String language) throws JSONException, InvalidJsonError{
    	//Log.d(TAG, "station code in travelSemen is ..." + code);
    	if (stationList == null) {
			readHafasStations(context, language);
		}
    	for (Station station : stationList) {
    		//Log.e(TAG, "stationList..." + stationList.size());
			if (StringUtils.equalsIgnoreCase(code, station.getCode())) {
				//Log.d(TAG, "station code is same with...NHAFAS..." + code + ".........." + station.getCode());
				return true;
			}
		}
        return false;
    }
    
    private String checkTrainCategory(Context context, String trainType){
    	//Log.d(TAG, "trainType is ..." + trainType );
    	String trainCategory = "";
    	
    	if (trainMap == null) {
    		trainMap = readHafasTrainCategories(context);
		}
    	
    	if (TrainTypeConstant.TRAIN_TGD.equalsIgnoreCase(trainType) 				
				|| TrainTypeConstant.TRAIN_RHT.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_FR_DE.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_DE_FR.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_LYRIA.equalsIgnoreCase(trainType)) {
			
    		trainType = TrainTypeConstant.TRAIN_TGV;			
			
		} else if ( TrainTypeConstant.TRAIN_IC.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_BENELUX.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_INTERCITY.equalsIgnoreCase(trainType)) {
			
			trainType = TrainTypeConstant.TRAIN_IC;
			
		}else if ( TrainTypeConstant.TRAIN_IXB.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_IXK.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_RHI.equalsIgnoreCase(trainType)) {
			
			trainType = TrainTypeConstant.TRAIN_ICE;
			
		}  else if(StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUROSTAR)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUR)){
			
			trainType = TrainTypeConstant.TRAIN_EST;;
		
		}	else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_RE)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_R)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_RB)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_REX)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_R84)) {
			
			trainType = TrainTypeConstant.TRAIN_RE;;
		}
    	Iterator<String> iter = trainMap.keySet().iterator();

    	while (iter.hasNext()) {

    	    String key = iter.next();
    	    //Log.d(TAG, "key..."+ key);
    	    if (StringUtils.equalsIgnoreCase(key.trim(), trainType.trim())) {
				trainCategory = key;
				//Log.d(TAG, "trainCategory is same with...NHAFAS..." + trainCategory + ".........." + key);
			}
    	}   	
        return trainCategory;

    }
    
/*    private boolean checkTravelSegmentAllData(TravelSegment ts){
    	String trainNr = ts.getTrainNr();
    	String OriginCode = ts.getOriginCode();
    	
        return trainNr != null && !StringUtils.isEmpty(trainNr) &&
        		OriginCode != null && !StringUtils.isEmpty(OriginCode) &&
        		ts.getValidityStartDate() != null;
    }*/
    
    private boolean checkTravelSegmentValidityStartDate(TravelSegment ts){
        return ts.getValidityStartDate() != null;
    }
    
    private boolean childOriginCodeSametParentOriginCode(TravelSegment ts, List<TravelSegment> travelSegments){
    	
    	for (TravelSegment travelSegment : travelSegments) {
			if (StringUtils.equalsIgnoreCase(ts.getParentId(), travelSegment.getId())) {
				if (StringUtils.equalsIgnoreCase(ts.getOriginCode(), travelSegment.getOriginCode())) {
					return true;
				}
			}
		}
    	return false;
    	
        /*var firstOrDefault = testtravelSegments.FirstOrDefault(tts => tts.Id == ts.ParentId);
        return firstOrDefault != null && firstOrDefault.Origin == ts.Origin;*/
    }
    
    private boolean childDestinationCodeDifferentParentDestination(TravelSegment ts, List<TravelSegment> travelSegments){
    	
    	for (TravelSegment travelSegment : travelSegments) {
			if (StringUtils.equalsIgnoreCase(ts.getParentId(), travelSegment.getId())) {
				if (StringUtils.equalsIgnoreCase(ts.getDestinationCode(), travelSegment.getDestinationCode())) {
					return false;
				}
			}
		}
    	return true;
       /* var firstOrDefault = testtravelSegments.FirstOrDefault(tts => tts.Id == ts.ParentId);
        return firstOrDefault != null && firstOrDefault.Destination != ts.Destination;*/
    }
    
    private boolean childDestinationCodeDifferentOtherChildOriginCodeLinkedSameParent(TravelSegment ts, List<TravelSegment> travelSegments){
    	for (TravelSegment travelSegment : travelSegments) {
    		if (StringUtils.equalsIgnoreCase(ts.getParentId(), travelSegment.getParentId())) {
    			if (StringUtils.equalsIgnoreCase(ts.getDestinationCode(), travelSegment.getOriginCode())) {
    				return false;
    			}
			}
    		
    	}
    	return true;
    }
    
    private StationBoard getRealTimeTravelSegmentsById(Context context, TravelSegment ts){
    	StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
    	StationBoard stationBoard = stationBoardDatabaseService.selectRealTimeTravelSegmentsById(ts.getId());
    	return stationBoard;
    }
    
    private StationBoard createStationBoardTypeA(Context context, TravelSegment ts, String dnr){
    	
    	//Log.d(TAG, "StationBoard Type is A");
    	
    	Date departureDate = uniteDateAndTime(ts.getDepartureDate(), ts.getDepartureTime());

    	String trainCategory = checkTrainCategory(context, ts.getTrainType());
        
    	StationBoard oldStationBoard = getRealTimeTravelSegmentsById(context, ts);
    	
    	if (oldStationBoard != null) {
    		return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), departureDate, TimePreference.DEPARTURE, 
        			trainCategory, ts.getTrainNr(), STATIONBOARD_TYPE_A, departureDate, dnr, ts.getOrigin(), ts.getDestination(), 
        			oldStationBoard.isCallSuccessFul(), oldStationBoard.getDelay(), oldStationBoard.isCancelled(), ts.getParentId(), ts.getId());
		}else {
			return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), departureDate, TimePreference.DEPARTURE, 
	    			trainCategory, ts.getTrainNr(), STATIONBOARD_TYPE_A, departureDate, dnr, ts.getOrigin(), ts.getDestination(), 
	    			true, "", false, ts.getParentId(), ts.getId());
		}
    }
    
    private StationBoard createStationBoardTypeB(Context context, TravelSegment ts, String dnr, List<TravelSegment> travelSegmens){
    	
    	//Log.d(TAG, "StationBoard Type is B");
    	
    	Direction direction = ts.getDirection();
    	String sortDepartureDateString = "";
    	Date sortDepartureDate = null;
    	if (isNoChild(ts, travelSegmens)) {
    		if (direction == Direction.Outward) {
    			sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 00:01:00";
    		}else if (direction == Direction.Return) {
    			sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 23:58:00";
    		}else {
    			sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 23:59:00";
    		}		
    		sortDepartureDate = DateUtils.stringToDateTime(sortDepartureDateString);
		}else {
			Date departureDate = null;
			for (TravelSegment travelSegment : travelSegmens) {
				//Log.d(TAG, "travelSegment====" + travelSegment.getParentId());
				//Log.d(TAG, "ts.getId()=======" + ts.getId());
				if (StringUtils.equalsIgnoreCase(ts.getId(), travelSegment.getParentId())) {
					departureDate = uniteDateAndTime(travelSegment.getDepartureDate(), travelSegment.getDepartureTime());
					break;
				}				
			}
			
			sortDepartureDate = DateUtils.getSecondBeforeTime(departureDate, 1);
//			Log.d(TAG, "sortDepartureDate====" + sortDepartureDate);
			//sortDepartureDateString = DateUtils.dateToString(date);
		}
			
		
		//Date sortDepartureDate = DateUtils.stringToDateTime(sortDepartureDateString);
		StationBoard oldStationBoard = getRealTimeTravelSegmentsById(context, ts);
		if (oldStationBoard != null) {
			return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), ts.getValidityStartDate(), TimePreference.DEPARTURE, 
					null, null, STATIONBOARD_TYPE_B, sortDepartureDate, dnr, ts.getOrigin(), ts.getDestination(), 
	    			oldStationBoard.isCallSuccessFul(), oldStationBoard.getDelay(), oldStationBoard.isCancelled(), ts.getParentId(), ts.getId());
		}else {
			return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), ts.getValidityStartDate(), TimePreference.DEPARTURE, 
	    			null, null, STATIONBOARD_TYPE_B, sortDepartureDate, dnr, ts.getOrigin(), ts.getDestination(), 
	    			true, "", false, ts.getParentId(), ts.getId());
		}
    	
    }
    
    private StationBoard createStationBoardTypeC(Context context, TravelSegment ts, String dnr){
    	
    	//Log.d(TAG, "StationBoard Type is C");
    	
    	Date arrivalDate = uniteDateAndTime(ts.getArrivalDate(), ts.getArrivalTime());
    	StationBoard oldStationBoard = getRealTimeTravelSegmentsById(context, ts);
    	if (oldStationBoard != null) {
    		return new StationBoard(dnr + "_" + ts.getId(), ts.getDestinationCode(), arrivalDate, TimePreference.ARRIVAL, 
        			null, null, STATIONBOARD_TYPE_C, arrivalDate, dnr, ts.getDestination(), ts.getOrigin(), 
        			oldStationBoard.isCallSuccessFul(), oldStationBoard.getDelay(), oldStationBoard.isCancelled(), ts.getParentId(), ts.getId());
    	}else {
    		return new StationBoard(dnr + "_" + ts.getId(), ts.getDestinationCode(), arrivalDate, TimePreference.ARRIVAL, 
        			null, null, STATIONBOARD_TYPE_C, arrivalDate, dnr, ts.getDestination(), ts.getOrigin(), true, "", false, 
        			ts.getParentId(), ts.getId());
		}

    }
    
    private StationBoard createStationBoardTypeD(Context context, TravelSegment ts, String dnr){
    	
//    	Log.d(TAG, "StationBoard Type is D");
    	
    	Date departureDate = uniteDateAndTime(ts.getDepartureDate(), ts.getDepartureTime());
    	String trainCategory = checkTrainCategory(context, ts.getTrainType());
    	StationBoard oldStationBoard = getRealTimeTravelSegmentsById(context, ts);
    	if (oldStationBoard != null) {
    		return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), departureDate, TimePreference.DEPARTURE, 
        			trainCategory, ts.getTrainNr(), STATIONBOARD_TYPE_D, departureDate, dnr, ts.getOrigin(), 
        			ts.getDestination(), oldStationBoard.isCallSuccessFul(), oldStationBoard.getDelay(), 
        			oldStationBoard.isCancelled(), ts.getParentId(), ts.getId());
    	}else {
    		return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), departureDate, TimePreference.DEPARTURE, 
        			trainCategory, ts.getTrainNr(), STATIONBOARD_TYPE_D, departureDate, dnr, ts.getOrigin(), ts.getDestination(), true, 
        			"", false, ts.getParentId(), ts.getId());
		}
    	
    }
    
    
    private StationBoard createStationBoardTypeUnknownHasReservation(Context context, TravelSegment ts, String dnr){
    	
    	if (ts.isHasReservation()) {
    		Date departureDate = uniteDateAndTime(ts.getDepartureDate(), ts.getDepartureTime());
        	String trainCategory = checkTrainCategory(context, ts.getTrainType());
        	
        	return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), departureDate, TimePreference.DEPARTURE, 
        			trainCategory, ts.getTrainNr(), STATIONBOARD_TYPE_UNKNOWN, departureDate, dnr, ts.getOrigin(), ts.getDestination(), 
        			false, "", false, ts.getParentId(), ts.getId());
		}else {
			Direction direction = ts.getDirection();
	    	String sortDepartureDateString = "";
			if (direction == Direction.Outward) {
				sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 00:01:00";
			}else if (direction == Direction.Return) {
				sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 23:58:00";
			}else {
				sortDepartureDateString = DateUtils.dateToString(ts.getValidityStartDate()) + " 23:59:00";
			}			
					
			Date sortDepartureDate = DateUtils.stringToDateTime(sortDepartureDateString);
	    	return new StationBoard(dnr + "_" + ts.getId(), ts.getOriginCode(), ts.getValidityStartDate(), TimePreference.DEPARTURE, 
	    			null, null, STATIONBOARD_TYPE_UNKNOWN, sortDepartureDate, dnr, ts.getOrigin(), ts.getDestination(), false, "", false, ts.getParentId(), ts.getId());
		}
    	
    }
    
    public List<StationBoard> getRealTimeForTravelSegments(Context context, String travelSegmentId, String dnr, boolean isAll){
    	List<StationBoard> stationBoardsWithTypeA = new ArrayList<StationBoard>();
    	List<StationBoard> stationBoards = new ArrayList<StationBoard>();
    	StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
    	
    	stationBoardsWithTypeA = stationBoardDatabaseService.selectRealTimeOfChildTravelSegmentsByParentIdWithTypeA(travelSegmentId, dnr, isAll);
    	//Log.e(TAG, "travelSegmentId ..." + travelSegmentId);
    	if (stationBoardsWithTypeA.size() == 0) {
    		//Log.e(TAG, "No ChildTravelSegmentsByParentId ...");
    		StationBoard stationBoard = getParentTravelSegment(context, travelSegmentId);
    		if (stationBoard != null) {
    			stationBoardsWithTypeA.add(stationBoard);
			}
    		
		}else {
			stationBoards = stationBoardDatabaseService.selectRealTimeOfChildTravelSegmentsByParentIdWithNotTypeA(travelSegmentId, dnr, isAll);
		}
    	if (stationBoardsWithTypeA != null && stationBoardsWithTypeA.size() > 0) {
    		stationBoards.addAll(stationBoardsWithTypeA);
		}
    	Comparator<StationBoard> comp = new ComparatorStationBoardDate();

		Collections.sort(stationBoards, comp);
    	//Log.e(TAG, "stationBoardsWithTypeA Count is ..." + stationBoardsWithTypeA.size());
    	//Log.e(TAG, "stationBoards Count is ..." + stationBoards.size());
    	return stationBoards;
    }
    
    public StationBoard getParentTravelSegment(Context context, String travelSegmentId){
    	StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
    	StationBoard stationBoard = stationBoardDatabaseService.selectRealTimeOfSelfTravelSegmentsById(travelSegmentId);
    	return stationBoard;
    }
    public List<StationBoard> getDuplicatedStationBoard(Context context, String id){
    	StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
    	List<StationBoard> stationBoards = stationBoardDatabaseService.selectDuplicatedStationBoardById(id);
    	return stationBoards;
    }
    
    @SuppressWarnings("deprecation")
	private Date uniteDateAndTime(Date departureDate, Date departureTime){
    	
    	departureDate.setHours(departureTime.getHours());
    	departureDate.setMinutes(departureTime.getMinutes());
    	departureDate.setSeconds(departureTime.getSeconds());
    	return departureDate;
    }

	public List<StationBoard> getStationBoards(Context context) {
		StationBoardDatabaseService stationBoardDatabaseService = new StationBoardDatabaseService(context);
		List<StationBoard> stationBoards = stationBoardDatabaseService.selectStationBoardsCollection(false, null);
		return stationBoards;
	}
	
	
	public List<Station> readHafasStations(Context context, String language) throws JSONException, InvalidJsonError{
		if(stationList == null||!language.equals(currentLanguage)){
			currentLanguage = language;
			MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
			int resourcesId = 0;
			
			if (StringUtils.equalsIgnoreCase(language, "EN_GB")) {
				resourcesId = R.raw.station_en;
			}else if(StringUtils.equalsIgnoreCase(language, "FR_BE")){
				resourcesId = R.raw.station_fr;
			}else if(StringUtils.equalsIgnoreCase(language, "NL_BE")){
				resourcesId = R.raw.station_nl;
			}else {resourcesId = R.raw.station_de;
			}
			
			InputStream is = context.getResources().openRawResource(resourcesId);  
			String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
			
			StationResponse stationResponse = masterResponseConverter.parseStation(stringHttpResponse);
			
			if (stationResponse != null) {
				stationList = stationResponse.getStations();
				
				
			}
		}
		
		return stationList;
	}
	
	public Map<String, String> readHafasTrainCategories(Context context){
		Map<String, String> trainMap = new TreeMap<String, String>(
			new Comparator<String>() {
				public int compare(String obj1, String obj2) {
					// 降序排序
					return obj1.compareTo(obj2);
				}
			});
		//Map<String, String> trainMap = new HashMap<String, String>();
		trainMap.put("", context.getString(R.string.reduction_card_view_none));
		trainMap.put(TrainTypeConstant.TRAIN_TGV, context.getString(R.string.traincategory_tgv));
		trainMap.put(TrainTypeConstant.TRAIN_EST, context.getString(R.string.traincategory_est));
		trainMap.put(TrainTypeConstant.TRAIN_THA, context.getString(R.string.traincategory_tha));
		trainMap.put(TrainTypeConstant.TRAIN_ICE, context.getString(R.string.traincategory_ice));
		trainMap.put(TrainTypeConstant.TRAIN_IC, context.getString(R.string.traincategory_ic));
		trainMap.put(TrainTypeConstant.TRAIN_IR, context.getString(R.string.traincategory_ir));
		trainMap.put(TrainTypeConstant.TRAIN_RE, context.getString(R.string.traincategory_re));
		trainMap.put(TrainTypeConstant.TRAIN_EC, context.getString(R.string.traincategory_ec));
		trainMap.put(TrainTypeConstant.TRAIN_INT, context.getString(R.string.traincategory_int));
		trainMap.put(TrainTypeConstant.TRAIN_CNL, context.getString(R.string.traincategory_cnl));
		return trainMap;
	}
	
	
	public void saveCreateStationBoardStatus(Context context, boolean isSucced){
			
		//Log.e(TAG, "saveCreateStationBoardStatus...." + isSucced);
		SharedPreferences settings = context.getSharedPreferences(CREATE_STATIONBOARD_STATUS, 0);
		settings.edit().putBoolean(STATUS, isSucced).commit();
		
	}
	public boolean getCreateStationBoardStatus(Context context){
				
		SharedPreferences settings = context.getSharedPreferences(CREATE_STATIONBOARD_STATUS, 0);
		
		boolean isSucced = settings.getBoolean(STATUS, false);
		//Log.e(TAG, "getCreateStationBoardStatus...." + isSucced);
		return isSucced;
	}
}
