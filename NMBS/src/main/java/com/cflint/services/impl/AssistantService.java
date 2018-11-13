package com.cflint.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cflint.activity.MyTicketsActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.ExecuteDossierAftersalesTask;
import com.cflint.async.RealTimeAsyncTask;

import com.cflint.dataaccess.converters.AssistantConverter;
import com.cflint.dataaccess.converters.MasterResponseConverter;
import com.cflint.dataaccess.converters.StationBoardConverter;
import com.cflint.dataaccess.database.AssistantDatabaseService;

import com.cflint.dataaccess.database.DossierDatabaseService;
import com.cflint.dataaccess.database.FavoriteStationsDatabaseService;

import com.cflint.dataaccess.restservice.IAssistantDataService;
import com.cflint.dataaccess.restservice.IStationBoardDataService;
import com.cflint.dataaccess.restservice.impl.AssistantDataService;
import com.cflint.dataaccess.restservice.impl.StationBoardDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.Connection;
import com.cflint.model.DossierAftersalesResponse;
import com.cflint.model.DossierDetailParameter;
import com.cflint.model.DossierParameter;
import com.cflint.model.DossierResponse;
import com.cflint.model.DossierResponse.OrderItemStateType;
import com.cflint.model.DossierSummary;
import com.cflint.model.FAQCategory;
import com.cflint.model.FavoriteStation;
import com.cflint.model.GeneralSetting;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.HomePrintTicket;
import com.cflint.model.OfferQuery;
import com.cflint.model.StationBoardCollection;
import com.cflint.model.OfferQuery.ComforClass;
import com.cflint.model.OfferQuery.TravelType;
import com.cflint.model.Order;
import com.cflint.model.SeatLocationForOD.Direction;
import com.cflint.model.Station;
import com.cflint.model.StationBoard;
import com.cflint.model.StationBoardLastQuery;
import com.cflint.model.StationBoardQuery;

import com.cflint.services.IAssistantService;
import com.cflint.services.ISettingService;
import com.cflint.util.AESUtils;
import com.cflint.util.ComparatorDate;
import com.cflint.util.DateUtils;
import com.cflint.util.FileManager;
import com.cflint.util.TickesHelper;
import com.cflint.util.Utils;

public class AssistantService implements IAssistantService {

	private static final String TAG = AssistantService.class.getSimpleName();
	private Context applicationContext;
	public static final int SEARCH_DOSSIER_AFTERSALE_RESPONSE_FOR_ORDER = 0;
	public static final int SEARCH_DOSSIER_AFTERSALE_RESPONSE = 1;
	public final static String LAST_REFRESH_TIME = "last_refresh_time";
	private String currentTicketDnr;
	List<TickesHelper> tickesHelpers = new ArrayList<TickesHelper>();
	List<TickesHelper> tickesHistoryHelpers = new ArrayList<TickesHelper>();
	List<TickesHelper> tickesCanceledHelpers = new ArrayList<TickesHelper>();
	private GeneralSetting generalSetting;
	private File createPdfFile;
	private List<Order> orderList;
	private List<Order> orderHistoryList;
	private List<Order> orderCanceledList;
	public static final int REQUEST_CODE = 0x0000c0de;
	private File decryptFile;
	private List<Station> stationList;
	private List<List<Station>> allStations;
	
	
	private List<FavoriteStation> favoriteStationCodeList;
	public AssistantService(Context context) {
		this.applicationContext = context;
	}

	public String getCurrentTicketDnr() {
		return currentTicketDnr;
	}

	public void setCurrentTicketDnr(String currentTicketDnr) {
		this.currentTicketDnr = currentTicketDnr;

	}

	public GeneralSetting getGeneralSetting() {
		return generalSetting;
	}

	public void setGeneralSetting(GeneralSetting generalSetting) {
		this.generalSetting = generalSetting;
	}

	public List<FavoriteStation> getFavoriteStationCode(Context context){
		
		FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		try {
			//if(favoriteStationCodeList == null){
				//favoriteStationCodeList = database.readStationCodeList();
			//}
			return favoriteStationCodeList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean insertStationCode(FavoriteStation favoriteStation,Context context){
		favoriteStationCodeList = null;
		FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		return database.insertStationCode("");
	}
	
	public boolean deleteStationCode(String code,Context context){
		favoriteStationCodeList = null;
		FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(context);
		return database.deleteStationCode(code);
	}
	
	public List<Station> getHafasStations(String language) throws JSONException, InvalidJsonError{
		if(stationList == null){
			IStationBoardDataService stationBoardDataService = new StationBoardDataService();
			stationList = stationBoardDataService.readHafasStations(applicationContext, language);
		}
		
		return stationList;
	}
	
	public boolean insertOrder(OfferQuery offerQuery, Order order,
			Connection selectedReturnConnection) {

		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(
				applicationContext);

		if (offerQuery != null) {
			// Log.e("TravelType", "insertOrder ...  getTravelType......" +
			// offerQuery.getTravelType());
			if (offerQuery.getTravelType() == TravelType.RETURN) {
				// Log.e(TAG, "insertOrder ...  is return......");
				assistantDatabaseService.insertOrder(order);
				Date returnDate = null;
				if (selectedReturnConnection != null) {
					returnDate = selectedReturnConnection.getDeparture();
				}

				assistantDatabaseService.insertOrder(new Order(order
						.getTrainType(), order.getPersonNumber(), order.getTrainNr(), order.getOrderState(), order.getDNR(),
						order.getDestination(), order.getDestinationCode(), order.getOrigin(), order.getOriginCode(), order.getPnr(), returnDate, order.getDossierGUID(),
						order.getTravelSegmentID(), order.isIncludesEBS(), order.getTravelclass(), order.getDirection(), 
						order.isHasDepartureTime(), returnDate, order.getSortFirstChildDepartureDate(), false, 
						order.getEmail(), order.getRefundable(), order.getExchangeable(), order.getRulfillmentFailed(), order.isHasDuplicatedStationboard(), order.getDuplicatedStationboardId()));
			} else {
				assistantDatabaseService.insertOrder(order);
			}
		}
		return true;
	}

	/*
	 * public boolean isTimeToRefresh(){ SharedPreferences settings =
	 * applicationContext.getSharedPreferences(LAST_REFRESH_TIME, 0); String
	 * lastRefreshTimeStr = settings.getString("RefreshTime", ""); //Log.d(TAG,
	 * "lastRefreshTimeStr ..." + lastRefreshTimeStr); Date lastRefreshTime; if
	 * (StringUtils.isEmpty(lastRefreshTimeStr)) { lastRefreshTime = new Date();
	 * }else { lastRefreshTime = DateUtils.stringToDate(lastRefreshTimeStr); }
	 * Date oneHourLaterTime = DateUtils.getOneHourLaterTime(lastRefreshTime);
	 * Date nowDate = new Date(); String nowDateString =
	 * DateUtils.dateTimeToString(nowDate); //Log.d(TAG, "nowDateString ..." +
	 * nowDateString); settings.edit().putString("RefreshTime",
	 * nowDateString).commit(); return nowDate.after(oneHourLaterTime); }
	 */

	private List<String> filterDnrForOrders(List<Order> orderList) {
		List<String> dnrs = new ArrayList<String>();
		for (int i = 0; i < orderList.size(); i++) {
			if (dnrs.size() == 0) {
				dnrs.add(orderList.get(i).getDNR());
			} else {
				if (!dnrs.contains(orderList.get(i).getDNR())) {
					dnrs.add(orderList.get(i).getDNR());
				}
			}
		}
		return dnrs;
	}

	private List<TickesHelper> groupingOrders(List<String> dnrs, List<Order> orderList, int flag) {

		List<Order> ordersOfDnr = null;
		int orderState = 0;
		List<TickesHelper> tickesHelpers = new ArrayList<TickesHelper>();
		Date firstTravelSegmentDate = null;
		if (orderList != null && orderList.size() > 0) {
			for (int i = 0; i < dnrs.size(); i++) {
				ordersOfDnr = new ArrayList<Order>();

				for (int j = 0; j < orderList.size(); j++) {

					if (StringUtils.equalsIgnoreCase(dnrs.get(i), orderList.get(j).getDNR())) {
						if (j == 0) {
							firstTravelSegmentDate = orderList.get(j).getDepartureDate();
						}
						ordersOfDnr.add(orderList.get(j));
					}
					orderState = orderList.get(j).getOrderState();
				}
				boolean isOpen = false;
				if (flag == MyTicketsActivity.FLAG_FIND_ORDER) {
					isOpen = getOrderExpandState(isOpen, dnrs, i);
				} else if (flag == MyTicketsActivity.FLAG_FIND_ORDER_HISTORY) {
					isOpen = getOrderHistoryExpandState(isOpen, dnrs, i);
					
				} else if (flag == MyTicketsActivity.FLAG_FIND_ORDER_CANCELED) {
					isOpen = getOrderCanceledExpandState(isOpen, dnrs, i);					
				}
/*				ordersOfDnr = rsetSortDepartureDate(ordersOfDnr);
				Comparator<Order> comp = new ComparatorDate(true);

				Collections.sort(orderList, comp);*/
				// Log.e(TAG, "isOpen ..." + isOpen);
/*				for (Order order : ordersOfDnr) {
					Log.e(TAG, "order ..." + order.getDNR());
				}*/
				TickesHelper tickesHelper = new TickesHelper(dnrs.get(i), isOpen, ordersOfDnr, orderState, firstTravelSegmentDate);
				tickesHelpers.add(tickesHelper);
			}
		}

		if (flag == MyTicketsActivity.FLAG_FIND_ORDER) {
			currentTicketDnr = null;
		}
		return tickesHelpers;
	}
	
	private boolean getOrderExpandState(boolean isOpen, List<String> dnrs, int index){
		if (this.tickesHelpers != null && this.tickesHelpers.size() > 0) {

			for (TickesHelper tickesHelper : this.tickesHelpers) {

				if (StringUtils.equalsIgnoreCase(tickesHelper.getDnr(), dnrs.get(index))) {
					isOpen = tickesHelper.isOpen();
				}
				if (currentTicketDnr != null) {
					if (StringUtils.equalsIgnoreCase(dnrs.get(index), currentTicketDnr)) {
						isOpen = true;
					} else {
						isOpen = false;
					}
				}
			}
		} else {
			if (currentTicketDnr != null) {
				if (StringUtils.equalsIgnoreCase(dnrs.get(index), currentTicketDnr)) {

					isOpen = true;
				} else {
					isOpen = false;
				}

			} else {
				isOpen = true;
			}
			//
		}
		return isOpen;
	}
	private boolean getOrderHistoryExpandState(boolean isOpen, List<String> dnrs, int index){
		if (this.tickesHistoryHelpers != null
				&& this.tickesHistoryHelpers.size() > 0) {
			for (TickesHelper tickesHelper : this.tickesHistoryHelpers) {
				// Log.e(TAG, "tickesHistoryHelpers size ..." +
				// tickesHistoryHelpers.size());
				if (StringUtils.equalsIgnoreCase(
						tickesHelper.getDnr(), dnrs.get(index))) {
					isOpen = tickesHelper.isOpen();
				}
			}

		}
		return isOpen;
	}
	
	private boolean getOrderCanceledExpandState(boolean isOpen, List<String> dnrs, int index){
		if (this.tickesCanceledHelpers != null
				&& this.tickesCanceledHelpers.size() > 0) {
			for (TickesHelper tickesHelper : this.tickesCanceledHelpers) {
				// Log.e(TAG, "tickesHistoryHelpers size ..." +
				// tickesHistoryHelpers.size());
				if (StringUtils.equalsIgnoreCase(tickesHelper.getDnr(), dnrs.get(index))) {
					isOpen = tickesHelper.isOpen();
				}
			}
		}
		return isOpen;
	}
	
	private List<Order> readOrders(int flag, String dossierAftersalesLifetime){
		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(
				applicationContext);
		List<Order> orderList = assistantDatabaseService.selectOrdersCollection(flag, dossierAftersalesLifetime);
		return orderList;
	}

	public List<Order> searchOrders(int flag, String dossierAftersalesLifetime) {
		// Log.e(TAG, "tickes flag ..." + flag);
		
		List<Order> orderList = readOrders(flag, dossierAftersalesLifetime);

		if (flag == MyTicketsActivity.FLAG_FIND_ORDER_HISTORY) {
			orderList = rsetSortDepartureDate(orderList);
			Comparator<Order> comp = new ComparatorDate(false);

			Collections.sort(orderList, comp);
		}
		List<String> dnrs = filterDnrForOrders(orderList);
		// Log.d(TAG, "dnrs ..." + dnrs.size());

		List<TickesHelper> tickesHelpers = groupingOrders(dnrs, orderList, flag);

		for (int i = 0; i < tickesHelpers.size(); i++) {
			// Log.d(TAG, "tickesHelpers ...dnr...." +
			// tickesHelpers.get(i).getDnr());
			for (int j = 0; j < tickesHelpers.get(i).getOrdersOfDnr().size(); j++) {
				// Log.d(TAG, "tickesHelpers ...dnr...." +
				// tickesHelpers.get(i).getOrdersOfDnr().get(j).getTrainNr());

			}
		}
		if (flag == MyTicketsActivity.FLAG_FIND_ORDER) {
			this.tickesHelpers = tickesHelpers;
		} else if (flag == MyTicketsActivity.FLAG_FIND_ORDER_HISTORY) {
			// Log.e(TAG, "tickes history Helpers ..." + tickesHelpers.size());
			this.tickesHistoryHelpers = tickesHelpers;
		} else if (flag == MyTicketsActivity.FLAG_FIND_ORDER_CANCELED) {
			// Log.e(TAG, "tickes history Helpers ..." + tickesHelpers.size());
			this.tickesCanceledHelpers = tickesHelpers;
		}
		if (this.tickesHelpers != null && tickesHistoryHelpers != null && tickesCanceledHelpers != null) {
			// Log.e(TAG, "tickesHelpers ..." + this.tickesHelpers.size());
			// Log.e(TAG, "tickesHistoryHelpers ..." +
			// tickesHistoryHelpers.size());
			// Log.e(TAG, "tickesCanceledHelpers ..." +
			// tickesCanceledHelpers.size());
		}

		return orderList;
	}

	private List<Order> rsetSortDepartureDate(List<Order> orderList) {
		// List<Order> newOrderList = new ArrayList<Order>();
		String sortDepartureDateString = "";
		Date sortDepartureDate = null;
		// Log.e(TAG, "orderList ...zie...." + orderList.size());
		if (orderList != null) {
			for (Order order : orderList) {
				if (order != null) {
					if (!order.isHasDepartureTime() && order.getDirection() == Direction.Single
							|| order.getDirection() == Direction.unknown) {
						sortDepartureDateString = DateUtils.dateToString(order.getDepartureDate()) + " 00:01:00";
						sortDepartureDate = DateUtils.stringToDateTime(sortDepartureDateString);
						order.setSortDepartureDate(sortDepartureDate);
					}
				}
			}
		}
		return orderList;
	}

	public List<TickesHelper> getTickesHelpers() {
		return tickesHelpers;
	}

	public void setTickesHelpers(List<TickesHelper> tickesHelpers) {
		this.tickesHelpers = tickesHelpers;
	}

	public List<TickesHelper> getTickesHistoryHelpers() {
		return tickesHistoryHelpers;
	}

	public List<Order> searchHistoryOrders() {

		return null;
	}

	public List<Order> uniteOrders(List<Order> orders,
			List<Order> historyOrders, List<Order> orderCanceledList) {
		if (orders != null && historyOrders != null) {
			for (Order order : historyOrders) {
				orders.add(order);
			}

		}
		if (orders != null && orderCanceledList != null) {
			for (Order order : orderCanceledList) {
				orders.add(order);
			}
		}
		return orders;
	}

	/**
	 * Call AssistantService's searchDossierAfterSale method and get
	 * OfferResponse asynchronously.
	 * 
	 * @param: DNR
	 * @param: settingService
	 * @param: hasConnection
	 * @return: AsyncDossierAfterSaleResponse
	 */

	public AsyncDossierAfterSaleResponse searchDossierAfterSale(Order order, List<Order> orders, ISettingService settingService,
			boolean hasConnection, boolean isRefreshAllRealTime) {
		Log.d(TAG, "searchDossierAfterSale ...");

		List<Order> newOrders = null;
		Map<String, Order> ordersMap = new HashMap<String, Order>();

		if (orders != null && orders.size() > 0) {

			// Log.d(TAG, "orders size is ..." + orders.size());
			for (int i = 0; i < orders.size(); i++) {

				if (!ordersMap.containsKey(orders.get(i).getDNR())) {
					ordersMap.put(orders.get(i).getDNR(), orders.get(i));
				}

			}
			newOrders = new ArrayList<Order>(ordersMap.values());
/*			List list =  new  ArrayList(ordersMap.values());    
			Iterator<Entry<String, Order>> iter = ordersMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Order> entry = iter.next();
				newOrders.add(entry.getValue());
			}*/
			// Log.d(TAG, "newOrders size is ..." + newOrders.size());
		}

		AsyncDossierAfterSaleResponse aresponse = new AsyncDossierAfterSaleResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		if (newOrders != null && newOrders.size() == 1) {
			order = newOrders.get(0);
			newOrders = null;
		}
		DossierAfterSaleIntentService.startService(applicationContext, order,
				settingService.getCurrentLanguagesKey(), hasConnection, newOrders, isRefreshAllRealTime);
		// Return the async response who will receive the final return
		return aresponse;
	}

	public AsyncStationBoardResponse searchStationBoard(StationBoardQuery stationBoardQuery, ISettingService settingService) {

		AsyncStationBoardResponse aresponse = new AsyncStationBoardResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		StationBoardIntentService.startService(applicationContext, stationBoardQuery, settingService.getCurrentLanguagesKey());
		// Return the async response who will receive the final return
		return aresponse;

	}

	public StationBoardLastQuery getLastQuery(){
		StationBoardConverter stationBoardConverter = new StationBoardConverter();
		String response = FileManager.getInstance().readExternalStoragePrivateFile(applicationContext, FileManager.FOLDER_STATIONBOARD, FileManager.FILE_STATIONBOARD);
		StationBoardLastQuery stationBoardLastQuery = null;
		try {
			stationBoardLastQuery = stationBoardConverter.parsesStationBoardLastQuery(response);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidJsonError invalidJsonError) {
			invalidJsonError.printStackTrace();
		}
		return stationBoardLastQuery;
	}

	public AsyncStationDetailResponse searchStation(String stationCode,
			ISettingService settingService) {

		AsyncStationDetailResponse aresponse = new AsyncStationDetailResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		StationDetailIntentService.startService(applicationContext,
				stationCode, settingService.getCurrentLanguagesKey());
		// Return the async response who will receive the final return
		return aresponse;

	}



	/**
	 * Build a order, save order info
	 * 
	 */
	public Order buildBookData(Connection selsetedDepartureConnection,
			DossierResponse dossierResponse, OfferQuery offerQuery,
			DossierParameter dossierParameter) {
		// Log.d(TAG, "buildBookData..... ");

		String originStationCode = "";
		String destinationStationNameCode = "";
		String originStationName = "";
		String destinationStationName = "";
		String trainType = "";
		Date departureTime = null;
		int personNumber = 0;
		ComforClass travelclass = null;
		if (selsetedDepartureConnection != null) {
			departureTime = selsetedDepartureConnection.getDeparture();
			trainType = selsetedDepartureConnection.getLegData().get(0)
					.getTrainType();
		}

		if (offerQuery != null) {
			originStationCode = offerQuery.getOriginStationRCode();
			destinationStationNameCode = offerQuery
					.getDestinationStationRCode();

			originStationName = offerQuery.getOriginStationDestinationName();
			destinationStationName = offerQuery
					.getDestinationStationDestinationName();
			travelclass = offerQuery.getComforClass();
			if (offerQuery.getTravelPartyMembers() != null) {
				personNumber = offerQuery.getTravelPartyMembers().size();
			}
		}
		Order order = null;
		String pnrString = "";
		if (dossierResponse != null) {

			if (dossierResponse.getPnrIds() != null) {
				for (int i = 0; i < dossierResponse.getPnrIds().size(); i++) {
					if (i == dossierResponse.getPnrIds().size() - 1) {
						pnrString += dossierResponse.getPnrIds().get(i);
					} else {
						pnrString += dossierResponse.getPnrIds().get(i) + ", ";
					}
					pnrString = pnrString.replaceAll("null,", "");
					pnrString = pnrString.replaceAll(", null", "");
					// Log.d(TAG, "pnrString: " + pnrString);
				}
			}
			String email = null;
			if (dossierParameter != null
					&& dossierParameter.getCustomer() != null) {
				email = dossierParameter.getCustomer().getEmail();
			}
			order = new Order(trainType, personNumber,
					selsetedDepartureConnection.getLegData().get(0)
							.getTrainNr(), 0, dossierResponse.getDnrId(),
					originStationName, originStationCode,
					destinationStationName, destinationStationNameCode,
					pnrString, departureTime, DossierService.GUID, "", false,
					travelclass, Direction.unknown, true, departureTime, new Date(), false,
					email, null, null, null, false, null);
		}
		return order;
	}

	public void deleteDataByDNR(String dnr) {
		Log.e("MigrateDossier", "deleteDataByDNR::::" + dnr);
		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(
				applicationContext);
		assistantDatabaseService.deleteOrder(dnr);
		FileManager.getInstance().deleteExternalStoragePrivateFile(
				applicationContext, dnr, dnr + ".json");
	}

	public void openPDF(String dnr, String pdfId) {
		String[] params = { dnr, pdfId };
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				String dnr = params[0];
				String pdfId = params[1];
				
				String fileName = "";		
				String decryptFileName = "";
				fileName = dnr + "-" + pdfId + ".pdf";	
				decryptFileName = dnr + "-" + pdfId + "-" +"decrypt"+ ".pdf";
		        File file = FileManager.getInstance().getExternalStoragePrivateFile(applicationContext, dnr, fileName);
				if(file != null && file.exists()){
					openPdf(file);
				}
		        /*InputStream encrypttIs;
		        InputStream decryptInputStream = null;
		        InputStream encryptInputStream = null;
		       
				try {
					encrypttIs = new FileInputStream(file);
					try{
						decryptInputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(dnr,encrypttIs)); 
						
					} catch(Exception e){
						
						if(file.exists()){
							encrypttIs = new FileInputStream(file);						
						}
						encryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dnr,encrypttIs)); 
						file.delete();
						FileManager.getInstance().createExternalStoragePrivateFile(applicationContext, encryptInputStream, dnr, fileName);
						openPDF(dnr, pdfId);
					}
					startAdobleToReadPdf(dnr, pdfId, decryptFileName, decryptInputStream);
				} catch (FileNotFoundException e1) {
					
					e1.printStackTrace();
				}*/
				return null;
			}

			protected void onPostExecute(Void result) {
	

			};

		}.execute(params);
			
	}

	public void startAdobleToReadPdf(String dnr, String pdfId, String decryptFileName, InputStream afterEncryptInputStream){
		/*
		InputStream inputStream = null ;
		if(afterEncryptInputStream == null){
			String fileName = dnr + "-" + pdfId + ".pdf";	
			File file = FileManager.getInstance().getExternalStoragePrivateFile(applicationContext, dnr, fileName);
			try {
				inputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(dnr,new FileInputStream(file)));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		
		}else{
			inputStream = afterEncryptInputStream;
		}
		
		FileManager.getInstance().createExternalStoragePrivateFile(applicationContext, inputStream, dnr, decryptFileName);
		decryptFile = FileManager.getInstance().getExternalStoragePrivateFile(applicationContext, dnr, decryptFileName); 
		openPdf(decryptFile);*/
	}
	
	private void openPdf(File file){
		
		
        if (file.exists()) {        	
        	Uri path = Uri.fromFile(file);
 		    Intent intent = new Intent(Intent.ACTION_VIEW);
 		    intent.setDataAndType(path, "application/pdf");
 		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 		    
 		    try {	 		    	
 		    	applicationContext.startActivity(intent);
 		    } catch (ActivityNotFoundException e) {
 		     //Log.e(TAG, "ActivityNotFoundException, Open PDF Failed", e);         		   
 		    }
        } else {       	       	
        	//Toast.makeText(applicationContext, applicationContext.getString(R.string.alert_status_service_not_available), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * If the barcode is not encrypted ,it must transfer to encrypt file
	 * @param
	 * @param
	 * @return
	 * @throws FileNotFoundException 
	 */

	public Bitmap getStreamFromEncryptFile(String id, String barcodeNumber) throws FileNotFoundException {
		Bitmap mBitmap = null;
		/*InputStream inputStream = null;
		InputStream decryptInputStream = null;

		String name = id + "-" + barcodeNumber + ".png";
		File barcodeFile = new File(FileManager.getInstance().getExternalStoragePrivateFilePath(applicationContext, id, name));
		Log.d(TAG, "barcodeFile=====" + barcodeFile);
		inputStream = new FileInputStream(barcodeFile);
	
		if (inputStream != null) {
			Log.d(TAG, "inputStream != null=====");
			try {
				decryptInputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(id, inputStream));
				Log.d(TAG, "try=====" + decryptInputStream);
			} catch (Exception e) {
				Log.d(TAG, "Exception=====");
				inputStream = new FileInputStream(barcodeFile);
				InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(id, inputStream));
				barcodeFile.delete();
				FileManager.getInstance().createExternalStoragePrivateFile(applicationContext, afterDecryptInputStream, id, name);
				inputStream = new FileInputStream(barcodeFile);
				try {
					decryptInputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(id, inputStream));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				byte[] data = Utils.getBytes(decryptInputStream);
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				return mBitmap;
			}
			byte[] data = Utils.getBytes(decryptInputStream);
			Log.d(TAG, "data=====" + data);
			mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Log.d(TAG, "mBitmap=====" + mBitmap);
		} */
		return mBitmap;
	}
	
	public void deleteUndecryptPdfFile(DossierAftersalesResponse dossierAftersalesResponse, HomePrintTicket homePrintTicket){
		if (dossierAftersalesResponse != null && homePrintTicket != null) {
			String decryptFileName = dossierAftersalesResponse.getDnrId() + "-" + homePrintTicket.getPdfId() + "-" + "decrypt" + ".pdf";
			
			FileManager.getInstance().deleteExternalStoragePrivateFile(applicationContext, dossierAftersalesResponse.getDnrId(), decryptFileName);
		}		
	}
	
	
	public List<TickesHelper> getCanceledHelpers() {

		return tickesCanceledHelpers;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	public List<Order> getOrderHistoryList() {
		return orderHistoryList;
	}

	public void setOrderHistoryList(List<Order> orderHistoryList) {
		this.orderHistoryList = orderHistoryList;
	}

	public List<Order> getOrderCanceledList() {
		return orderCanceledList;
	}

	public void setOrderCanceledList(List<Order> orderCanceledList) {
		this.orderCanceledList = orderCanceledList;
	}

	public boolean hasOrder() {
		boolean isHasOrder = false;
		if (orderList != null && orderList.size() > 0) {
			isHasOrder = true;
		}
		if (orderHistoryList != null && orderHistoryList.size() > 0) {
			isHasOrder = true;
		}
		if (orderCanceledList != null && orderCanceledList.size() > 0) {
			isHasOrder = true;
		}
		return isHasOrder;
	}

	public List<Order> sortOrderByDirectionForFuture(List<Order> orderList) {
		// List<Order> orders = new ArrayList<Order>();
		/*
		 * for (Order order : orderList) {
		 * 
		 * if (!StringUtils.equalsIgnoreCase("00:00",
		 * DateUtils.timeToString(applicationContext,
		 * order.getDepartureDate()))) {
		 * //System.out.println("getDepartureDate=======" +
		 * DateUtils.timeToString(getApplicationContext(),
		 * order.getDepartureDate())); orders.add(order); } } for (Order order :
		 * orderList) {
		 * 
		 * if (StringUtils.equalsIgnoreCase("00:00",
		 * DateUtils.timeToString(applicationContext,
		 * order.getDepartureDate()))) {
		 * //System.out.println("getDepartureDate=======" +
		 * DateUtils.timeToString(getApplicationContext(),
		 * order.getDepartureDate())); orders.add(order); } }
		 */

		for (int i = 0; i < orderList.size(); i++) {
			Order orderI = orderList.get(i);
			for (int j = i + 1; j < orderList.size(); j++) {

				Order orderJ = orderList.get(j);
				if (!orderI.isHasDepartureTime()
						&& orderI.getDirection() != Direction.Outward) {
					if ((orderI.getDirection() == Direction.Return && orderJ
							.getDirection() == Direction.Outward)
							|| (orderI.getDirection() == Direction.Single
									|| orderI.getDirection() == Direction.unknown
									&& orderJ.getDirection() == Direction.Outward || orderJ
									.getDirection() == Direction.Return)) {

						// System.out.println(i + "   DNR::::" + orderI.getDNR()
						// +"  getDepartureDate=======" +
						// DateUtils.timeToString(applicationContext,
						// orderI.getDepartureDate()));
						// System.out.println(j + "   DNR::::" + orderJ.getDNR()
						// +"  getDepartureDate=======" +
						// DateUtils.timeToString(applicationContext,
						// orderJ.getDepartureDate()));

						orderList.remove(j);
						orderList.add(i, orderJ);
					}
				}
			}
		}

		return orderList;
	}

	public void deletePastTicket() {
		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(applicationContext);
		int dossierAftersalesLifetime = 0;
		if (this.generalSetting != null) {
			dossierAftersalesLifetime = this.generalSetting.getDossierAftersalesLifetime();
		}
		List<Order> pasttedOrders = assistantDatabaseService
				.selectOrdersCollection(MyTicketsActivity.FLAG_FIND_ORDER_PASTTED, dossierAftersalesLifetime + "");
		for (Order order : pasttedOrders) {
			boolean hasNotPastOrder = assistantDatabaseService.hasNotPastOrder(order);
			if (!hasNotPastOrder && order != null) {
				assistantDatabaseService.deleteOrder(order.getDNR());
				deleteFileOfPasttedTicket(order);
			}
		}
	}

	private void deleteFileOfPasttedTicket(Order order) {

		File f = FileManager.getInstance().getFileDirectory(applicationContext,
				order.getDNR(), order.getDNR());

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file2 : files) {
				if (file2 != null) {
					FileManager.getInstance()
							.deleteExternalStoragePrivateFile(
									applicationContext, order.getDNR(),
									file2.getName());

				}
			}
		}
		f.delete();
	}

	/**
	 * Call AssistantService's refrshDossierAfterSale method and get
	 * OfferResponse asynchronously.
	 * 
	 * @param: DNR
	 * @param: settingService
	 * @param: hasConnection
	 * @return: AsyncDossierAfterSaleResponse
	 */

	
	public AsyncDossierAfterSaleResponse refrshDossierAfterSale(Order order, ISettingService settingService) {
		AsyncDossierAfterSaleResponse aresponse = new AsyncDossierAfterSaleResponse();
		aresponse.registerReceiver(applicationContext);
			// Offload processing to IntentService
		AddExistingTicketIntentService.startService(applicationContext, order, settingService.getCurrentLanguagesKey());
			// Return the async response who will receive the final return
		return aresponse;
						
	}

	public AsyncDossierAfterSaleResponse refrshDossierDetail(List<DossierDetailParameter> dossiers, ISettingService settingService, boolean isUpload, boolean isWaitDownload) {
		AsyncDossierAfterSaleResponse aresponse = new AsyncDossierAfterSaleResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		UploadDossierIntentService.startService(applicationContext, dossiers, settingService.getCurrentLanguagesKey(), isUpload, isWaitDownload);
		// Return the async response who will receive the final return
		return aresponse;

	}

	public void onActivityDestoryDeleteFile() {
		if (createPdfFile != null && createPdfFile.exists()) {
			createPdfFile.delete();
		}
	}

	public List<StationBoard> getStationBoards() {
		IStationBoardDataService stationBoardDataService = new StationBoardDataService();
		List<StationBoard> stationBoards = stationBoardDataService.getStationBoards(applicationContext);
		return stationBoards;		
	}
	
	public Map<String,String> getStationBoardTrainCategory(){
		StationBoardDataService stationBoardDataService = new StationBoardDataService();
		return stationBoardDataService.readHafasTrainCategories(applicationContext);
	}

    public List<StationBoard> getRealTimeForTravelSegments(Context context, String travelSegmentId, String dnr, boolean isAll){
    	List<StationBoard> stationBoards = new ArrayList<StationBoard>();
    	IStationBoardDataService stationBoardDataService = new StationBoardDataService();
    	stationBoards = stationBoardDataService.getRealTimeForTravelSegments(applicationContext, travelSegmentId, dnr, isAll);
    	
    	return stationBoards;
    }
    public List<StationBoard> getDuplicatedStationBoard(Context context, String id){
    	List<StationBoard> stationBoards = null;
    	IStationBoardDataService stationBoardDataService = new StationBoardDataService();
    	stationBoards = stationBoardDataService.getDuplicatedStationBoard(applicationContext, id);
    	
    	return stationBoards;
    }
    
    public StationBoard getParentRealTimeForTravelSegments(Context context, String travelSegmentId){
    	StationBoard stationBoard = null;
    	IStationBoardDataService stationBoardDataService = new StationBoardDataService();
    	stationBoard = stationBoardDataService.getParentTravelSegment(applicationContext, travelSegmentId);
    	
    	return stationBoard;
    }
    
    public DossierAftersalesResponse getDossierAftersalesResponseFromFile(String DNR){
    	IAssistantDataService assistantDataService = new AssistantDataService();
    	DossierAftersalesResponse dossierAftersalesResponse = assistantDataService.getDossierAftersalesResponseFromFile(applicationContext, DNR);
    	return dossierAftersalesResponse;
    }
    
    /**
     * RefreshRealTime When an update of the app is performed to a version supporting the stationboard, 
     * 		the database must first be updated so that stationboard items are available for evaluation before checking for real-time information, 
     * 		when this call would fail, this is not blocking and the user can continue using the app.
     * @param currentLanguage
     * @throws Exception
     */
    public void refreshRealTimeFirstTime(String currentLanguage, List<Order> orderList) throws Exception{
    	
    	IStationBoardDataService stationBoardDataService = new StationBoardDataService();
    	boolean isSucced = stationBoardDataService.getCreateStationBoardStatus(applicationContext);
    	if (!isSucced) {
    		if (orderList != null && orderList.size() > 0) {
    			List<Order> newOrders = groupOrders(orderList);
				for (Order order : newOrders) {
		    		DossierAftersalesResponse dossierAftersalesResponse = getDossierAftersalesResponseFromFile(order.getDNR());
		    		stationBoardDataService.createStationBoard(applicationContext, dossierAftersalesResponse, currentLanguage);
				}
		    	
			}
		}

    	RealTimeAsyncTask realTimeAsyncTask = new RealTimeAsyncTask(currentLanguage, applicationContext, null);
		realTimeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    public int refreshRealTime(String currentLanguage) throws ParseException, InvalidJsonError, JSONException, TimeOutError, 
    	RequestFail, IOException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
    	int stationBoardBulkCallErrorCount = 0;
    	StationBoardDataService stationBoardDataService = new StationBoardDataService();
		StationBoardCollection stationBoardCollection = getAllStationBoardCollection();
		if (stationBoardCollection != null && stationBoardCollection.getStationBoards() != null && stationBoardCollection.getStationBoards().size() > 0) {
			stationBoardBulkCallErrorCount = stationBoardDataService.executeStationBoardBulkQuery(stationBoardCollection, 
					currentLanguage, applicationContext);
		}
		return stationBoardBulkCallErrorCount;
    }
    
    public StationBoardCollection getAllStationBoardCollection(){
    	StationBoardDataService stationBoardDataService = new StationBoardDataService();
		StationBoardCollection stationBoardCollection = stationBoardDataService.getStationBoardWithTypeIsA(applicationContext, null);
		return stationBoardCollection;
    }
    
    public String getExistStationCode(String stationCode){
    	if ("BEBMI".equalsIgnoreCase(stationCode)) {
			return stationCode;
		}else if ("NLASC".equalsIgnoreCase(stationCode)) {
			return stationCode;
		}else if ("DEKOH".equalsIgnoreCase(stationCode)) {
			return stationCode;
		}else if ("FRPNO".equalsIgnoreCase(stationCode)) {
			return stationCode;
		}else if ("GBSPX".equalsIgnoreCase(stationCode)) {
			return stationCode;
		}
    	return null;
    }
    
    public InputStream getStationFloorPlan(String stationCode, String language){
    	
    	
    	AssetManager am = applicationContext.getAssets();  
    	language = language.substring(0, 2);
    	String existStationCode = getExistStationCode(stationCode);
    	InputStream is = null;
    	String fileName = existStationCode + "_" + language;
    	System.out.println("fileName=====" + fileName);
    	try {
			is = am.open(FileManager.FOLDER_PDF + "/" + fileName + ".jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return is;
    }
    
    public void showStationFloorPlan(String stationCode, String language){
    	AssetManager am = applicationContext.getAssets();  
    	language = language.substring(0, 2);
    	String existStationCode = getExistStationCode(stationCode);
    	String fileName = existStationCode + "_" + language;
    	try {
			InputStream is = am.open(FileManager.FOLDER_PDF + "/" + fileName + FileManager.POSTFIX_ZIP);	
			ZipInputStream zipInputStream = new ZipInputStream(is);  
			//ZipEntry zipEntry = zipInputStream.getNextEntry();  
			//byte[] buffer = new byte[1024 * 1024];  
			//int count = 0;  
			
			FileManager.getInstance().createExternalStoragePrivateZipFile(applicationContext, zipInputStream, 
					FileManager.FOLDER_PDF, fileName + FileManager.POSTFIX_PDF);
			
			File file = FileManager.getInstance().getExternalStoragePrivateFile(applicationContext, 
					FileManager.FOLDER_PDF, fileName + FileManager.POSTFIX_PDF); 
			
	        openPdf(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
    
    private List<Order> groupOrders(List<Order> orders){
    	List<Order> newOrders = null;
    	Map<String, Order> ordersMap = new HashMap<String, Order>();
    	if (orders != null && orders.size() > 0) {
    		for (int i = 0; i < orders.size(); i++) {

    			if (!ordersMap.containsKey(orders.get(i).getDNR())) {
    				ordersMap.put(orders.get(i).getDNR(), orders.get(i));
    			}
    		}    		
    	}
    	newOrders = new ArrayList<Order>(ordersMap.values());    
    	return newOrders;
    }
    
    public void refreshDossierAftersales(List<Order> orders, String currentLanguage){

    	Log.d(TAG, "RefreshDossier...");
    	List<Order> dossiers = new ArrayList<Order>();
    	
		
		if (orders != null && orders.size() > 0) {

			
			List<Order> newOrders = groupOrders(orders);

	    	for (Order order : newOrders) {
	    	
	    		if (!hasExchangeable(order)) {
	    			
	    			Log.e(TAG, "RefreshDossier...to get Exchangeable");
	    				
	    			dossiers.add(order);    								
	    			
				}
			}
		}

    	if (dossiers.size() > 0) {
    		Log.e(TAG, "RefreshDossier...");
    		if (dossiers.size() == 1) {
    			DossierAfterSaleIntentService.startService(applicationContext, dossiers.get(0), currentLanguage, true, null, true);
			}else {
				DossierAfterSaleIntentService.startService(applicationContext, null, currentLanguage, true, dossiers, true);
			}
    		
    		
        	ExecuteDossierAftersalesTask.isDossierCallFinished = false;
        	if (applicationContext != null) {
				Intent broadcastIntent = new Intent(ServiceConstant.DOSSIER_SERVICE_ACTION);
				broadcastIntent.putExtra(ExecuteDossierAftersalesTask.SERVICE_STATE_KEY, ExecuteDossierAftersalesTask.isDossierCallFinished);
				applicationContext.sendBroadcast(broadcastIntent);
			}
        	
		}else {
			RealTimeAsyncTask realTimeAsyncTask = new RealTimeAsyncTask(currentLanguage, applicationContext, null);
			realTimeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
    	
    }
    
    public boolean hasExchangeable(Order order){
    	if (order != null && order.getOrderState() == OrderItemStateType.OrderItemStateTypeConfirmed.ordinal()) {
			if (order.getExchangeable() == null || order.getRefundable() == null) {
				Log.e(TAG, "RefreshDossier...to get Exchangeable");    				
				return false;
			}    				
		}
    	return true;
    }

	public List<List<Station>> getAllStations() {
		return allStations;
	}

	public void setAllStations(List<List<Station>> allStations) {
		/*this.allStations = new ArrayList<List<Station>>();
		for (List<Station> stations : allStations) {
			this.allStations.add(stations);
		}*/
		this.allStations = allStations;
	}
    

}
