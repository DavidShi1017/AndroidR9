package com.nmbs.dataaccess.restservice.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.nmbs.R;
import com.nmbs.dataaccess.converters.AssistantConverter;
import com.nmbs.dataaccess.converters.CustomErrorMessager;
import com.nmbs.dataaccess.converters.MasterResponseConverter;
import com.nmbs.dataaccess.database.AssistantDatabaseService;
import com.nmbs.dataaccess.database.StationDatabaseService;
import com.nmbs.dataaccess.restservice.IAssistantDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.DonotContainTicket;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.JourneyPast;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.log.LogUtils;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.DossierResponse.OrderItemStateType;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.GeneralSettingResponse;
import com.nmbs.model.HomePrintTicket;
import com.nmbs.model.OfferQuery.ComforClass;
import com.nmbs.model.Order;
import com.nmbs.model.SeatLocationForOD.Direction;
import com.nmbs.model.Station;
import com.nmbs.model.StationDetailResponse;
import com.nmbs.model.TariffDetail;
import com.nmbs.model.TravelSegment;
import com.nmbs.util.AESUtils;
import com.nmbs.util.DateUtils;
import com.nmbs.util.FileManager;
import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.HttpRetriever;
import com.nmbs.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssistantDataService extends CustomErrorMessager implements IAssistantDataService {
	
	private static final String TAG = AssistantDataService.class.getSimpleName();
	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	AssistantConverter assistantConverter = new AssistantConverter();
	List<String> allFileNameList = new ArrayList<String>();
	/**
	 * Call web service asynchronously and get List<City>.
	 * @param context
	 * @param languageBeforSetting
	 * @return CityDetailResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws TimeOutError
	 * @throws ParseException
	 * @throws RequestFail
	 * @throws IOException
	 * @throws ConnectionError 
	 * @throws DBookingNoSeatAvailableError 
	 * @throws CustomError 
	 * @throws DBooking343Error 
	 */
	public DossierAftersalesResponse executeDossierAfterSale(
			Context context, Order order, String languageBeforSetting, boolean isNeedCatchCustomerError, boolean isUpload)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, 
			BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{

		// operate titles part.
		String stringHttpResponse = null;
		String serviceVersion = "";
		String DNR = "";
		String email = "";
		if (order != null) {
			DNR = order.getDNR();
			email = order.getEmail();
		}
		String urlString = context.getString(R.string.server_url_get_dossier_aftersales_v4)+ "/" + DNR;
		if (email != null && !StringUtils.isEmpty(email)) {
			urlString += "?email=" + email;
			if (isUpload) {
				urlString += "&actionName=Upload";
			}else {
				urlString += "&actionName=Other";
			}
			serviceVersion = HTTPRestServiceCaller.API_VERSION_VALUE_4;
		}else {
			urlString = context.getString(R.string.server_url_get_dossier_aftersales)+ "/" + DNR;
			serviceVersion = HTTPRestServiceCaller.API_VERSION_VALUE_2;
		}
		
		stringHttpResponse = httpRestServiceCaller
				.executeHTTPRequest(
						context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 
						15000, false, "", serviceVersion);
		//stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, null, "error.json");
		
		//stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, DNR, DNR + ".json");
		DossierAftersalesResponse dossierAftersalesResponse = assistantConverter.parsesDossierAftersalesResponse(stringHttpResponse);
		super.throwErrorMessage(dossierAftersalesResponse, context, "");
		if (dossierAftersalesResponse != null) {
			try {
				stringHttpResponse = AESUtils.encrypt(DNR, stringHttpResponse);
			} catch (Exception e) {
				// TO DO NOTING...
			}
			FileManager.getInstance().createExternalStoragePrivateFileFromString(context, DNR, DNR + ".json", stringHttpResponse);
			LogUtils.d(TAG, "dossierAftersalesResponse......"+ dossierAftersalesResponse.getDnrId());
			//Log.d(TAG, "CityResponse......getCities....name"+ cityResponse.getCities().get(0).getName());
			return dossierAftersalesResponse;
		}
		return null;
	}
	
	public StationDetailResponse executeStationDetail(String stationCode , String language, Context context) 
		throws InvalidJsonError,JSONException, TimeOutError, RequestFail, ParseException, IOException, 
		ConnectionError, BookingTimeOutError{
		String httpResponseString = null;
		StationDetailResponse response = null;
		String separator = "/";
		try {			 
			//https://api.b-europe.com/stations/<stationCode>
			Date date = new Date();
			String dateString = DateUtils.timeToString(date) ;
			String urlString = context.getString(R.string.server_url_get_stations) + "?id=" + dateString;
			urlString = urlString + separator + stationCode;						
			httpResponseString = httpRestServiceCaller.executeHTTPRequest(context.getApplicationContext(), null
					, urlString ,language, HTTPRestServiceCaller.HTTP_GET_METHOD, 30000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		} catch (RequestFail e) {
			//Log.d("executeStationDetail", "RequestFail", e);
			e.printStackTrace();
		}		
		if (httpResponseString == null || "".equals(httpResponseString)) {
			throw new RequestFail();
		}	
		
		response = assistantConverter.parsesStationDetail(httpResponseString);	
		if (response != null) {
			
			
			FileManager.getInstance().createExternalStoragePrivateFileFromString(context, null, stationCode + ".json", httpResponseString);
			//Log.d(TAG, "getStationDetail......"+ response.getStationDetail());
			//Log.d(TAG, "CityResponse......getCities....name"+ cityResponse.getCities().get(0).getName());
			return response;
		}
		return response;
	}
	private int downloadPdf(Context context, DossierAftersalesResponse dossierAftersalesResponse, TravelSegment travelSegment){
		int result = 0;
		String urlString = "";
		boolean hasError = false;
		
		if (dossierAftersalesResponse != null) {
			List<HomePrintTicket> homePrintTickets = dossierAftersalesResponse.getHomePrintTicketsByTravelSegment(travelSegment);
			
			if (homePrintTickets != null && homePrintTickets.size() > 0) {
				for (HomePrintTicket homePrintTicket : homePrintTickets) {
					String fileName = dossierAftersalesResponse.getDnrId() + "-";
					String pdfUrl = homePrintTicket.getPdfUrl();
					//Log.d(TAG, "Starting pdf file......"+ pdfUrl);
					urlString = pdfUrl;
					if(urlString != null && homePrintTicket != null){
						//fileName += homePrintTicket.getPdfId();
						fileName += homePrintTicket.getPdfId() + ".pdf";
					}
					allFileNameList.add(fileName);
					
					boolean has = FileManager.getInstance().hasExternalStoragePrivateFile(context, dossierAftersalesResponse.getDnrId(), "." + fileName);
					LogUtils.d(TAG, "Starting Pdf file......" + fileName );
					LogUtils.d(TAG, "Starting Pdf file urlString......" + urlString );
					if (!has) {
						try {
							/*if (i == 1) {
								urlString += urlString + "1111111";
							}*/
							InputStream inputStream = HttpRetriever.getInstance().retrieveStream(urlString);
							InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierAftersalesResponse.getDnrId(),inputStream)); 
							/*httpDownloader.downloadNetworkFile(urlString, FileManager.getInstance().getFilePath("/pdfpath/"),fileName + ".pdf");  */
							FileManager.getInstance().createExternalStoragePrivateFile(context, afterDecryptInputStream, dossierAftersalesResponse.getDnrId(), fileName);
							   	
						} catch (IOException e) {

							LogUtils.e("loadPdfUrl", "Load PDF from url Error:", e);
							hasError = true;
							//result = -1;
							continue;
						}
						 catch (Exception e) {

							 LogUtils.e("loadPdfUrl", "Exception Load PDF from url Error:", e);
								hasError = true;
								//result = -1;
								continue;
						}
						 //result = 1;
					}	
					
				}																			        
			}
		}		
		if (hasError) {
			result = -1;
		}else {
			result = 1;
		}
        return result;
	}
	
	private int downloadBarcode(Context context, DossierAftersalesResponse dossierAftersalesResponse, TravelSegment travelSegment){
		int result = 0;
		String urlPrefixString = context.getString(R.string.barcode_url_prefix);
		
		boolean hasError = false;
		if (dossierAftersalesResponse != null) {
			String dnrId = dossierAftersalesResponse.getDnrId();
			List<String> barCodes = dossierAftersalesResponse.getBarcodOfCurrentTravelSegment(travelSegment);
			for (String barCode : barCodes) {
				String fileName = dnrId + "-";
				
				String urlString = urlPrefixString + barCode;					
				fileName += barCode + ".png";
				LogUtils.d(TAG, "Starting barcode file......"+ fileName );
				allFileNameList.add(fileName);
				boolean has = FileManager.getInstance().hasExternalStoragePrivateFile(context, dnrId, "." + fileName);

				LogUtils.e("loadPdfUrl", "Bar has......"+ has );
				
				if (!has) {
					try {
						
						InputStream inputStream = HttpRetriever.getInstance().retrieveStream(urlString);
						InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dnrId, inputStream));
						FileManager.getInstance().createExternalStoragePrivateFile(context,  afterDecryptInputStream, dnrId, fileName);
						 
						Bitmap mBitmap = getBarCodeBitmap(context, dnrId, fileName);
						BitmapFactory.Options options = new BitmapFactory.Options();
						//options.inTempStorage = new byte[16 * 1024];
						options.inSampleSize = 2;
						mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(context, dnrId, fileName), options);
						if (mBitmap == null) {
							LogUtils.e("loadbarcodeUrl", "no barcode");
							hasError = true;
							FileManager.getInstance().deleteExternalStoragePrivateFile(context, dnrId, fileName);
						}																				
					}
					catch (Exception e) {
						hasError = true;
						LogUtils.e("loadPdfUrl", "Load Barcode from url Error:", e);
						//result = -1;
						continue;
					}
				}				
			}					
		}	
		if (hasError) {
			result = -1;
		}else {
			result = 1;
		}
        return result;
	}
	
	private Bitmap getBarCodeBitmap(Context context, String dnrId, String fileName) throws FileNotFoundException{
		//File barcodeFile = new File(FileManager.getInstance().getExternalStoragePrivateFilePath(context, dnrId, fileName));
		
		//InputStream inputStream = new FileInputStream(barcodeFile);
		BitmapFactory.Options options = new BitmapFactory.Options();
		//options.inTempStorage = new byte[16 * 1024];
		options.inSampleSize = 1;
		Bitmap mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(context, dnrId, "." + fileName), options);
		//ByteArrayInputStream decryptInputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(dnrId, inputStream));
		//byte[] data = Utils.getBytes(decryptInputStream);
		//mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		return mBitmap;
	}
	
	public void deletePastedFile(Context context, DossierAftersalesResponse dossierAftersalesResponse){
		if (dossierAftersalesResponse != null) {
			
			String dnrString = dossierAftersalesResponse.getDnrId();
			File f = FileManager.getInstance().getFileDirectory(context, dnrString, dnrString + ".json");
			
			if (f.isDirectory()) {
				 File[] files = f.listFiles();          
			     for (File file2 : files) {
			    	 boolean isNeedDelete = true;
			    	 for (String fileName : allFileNameList) {
			    		 //Log.d("File", "file2.getName()......" + file2.getName());
			    		 //Log.d("File", "fileName......" + fileName);
						if (StringUtils.equalsIgnoreCase(file2.getName(), fileName)) {
							isNeedDelete = false;
						}
					}
			    	if (isNeedDelete) {
			    		
			    		 if (file2.getName().indexOf("-") != -1) {
			    			FileManager.getInstance().deleteExternalStoragePrivateFile(context, dnrString, file2.getName());
			    			//Log.d("File", "delete......");
				    		//Log.d("File", "delete the file name is......" + file2.getName());
			    		 }
			    		 
					}
			    }		     
			}			
			
		}
		
	}

	@Override
	public GeneralSetting executeGeneralSetting(Context context) throws Exception {
		return null;
	}

	public boolean reBuildOrder(Context context, DossierAftersalesResponse dossierAftersalesResponse, 
			Order order, String dossierAftersalesLifetime) throws DonotContainTicket, JourneyPast{
		AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(context);
		boolean isHasError = false;
		boolean isJourneyPast = false;
		if(dossierAftersalesResponse != null){
			LogUtils.d("dossier", "dossierAftersalesResponse......" + dossierAftersalesResponse);
			
		if (dossierAftersalesResponse.getState() == OrderItemStateType.OrderItemStateTypeCancelled) {			
			assistantDatabaseService.deleteOrder(dossierAftersalesResponse.getDnrId());
			order = new Order(null, 0, null, OrderItemStateType.OrderItemStateTypeCancelled.ordinal(), 
					order.getDNR(), null, null, null, null, null, order.getDepartureDate(), 
					null, null, false, ComforClass.SECOND, Direction.unknown, false, order.getDepartureDate(), 
					order.getDepartureDate(), true, order.getEmail(), null, null, null, false, null);
				
				assistantDatabaseService.insertOrder(order);
		}else {
			
			assistantDatabaseService.deleteOrder(dossierAftersalesResponse.getDnrId());
			if (dossierAftersalesResponse.getTravelSegments() == null || dossierAftersalesResponse.getTravelSegments().size() == 0) {
				throw new DonotContainTicket();
			}
			
			for (int j = 0; j < dossierAftersalesResponse.getTravelSegments().size(); j++) {
					
				TravelSegment travelSegment = dossierAftersalesResponse.getTravelSegments().get(j);
				
				if (StringUtils.equalsIgnoreCase("", travelSegment.getParentId())) {
					String trainType = travelSegment.getTrainType();
					int personNumber = travelSegment.getPassengerTariffs().size();
					
					String trainNr = travelSegment.getTrainNr();
					int orderState = dossierAftersalesResponse.getState().ordinal();
					
					String refundable = dossierAftersalesResponse.getRefundable();

					String exchangeable = dossierAftersalesResponse.getExchangeable();
					
					String rulfillmentFailed = dossierAftersalesResponse.getRulfillmentFailed();
					
		/*			if(StringUtils.equalsIgnoreCase(order.getDNR(), "UIVQJQZ") || StringUtils.equalsIgnoreCase(order.getDNR(), "PSXSIJZ")){
							orderState = 1;
					}*/
					String dnr = order.getDNR();
					String dossierGUID = order.getDossierGUID();
					String originCode = "";
					String destinationCode = "";
					
					String origin = "";
					String destination = "";											
					origin = travelSegment.getOrigin();
					destination = travelSegment.getDestination();
					
					String travelSegmentID = travelSegment.getId();
					TariffDetail tariffDetail = dossierAftersalesResponse.getTariffById(travelSegment.getMainTariffId());
					boolean includesEBS = false;
					if (tariffDetail != null) {
						includesEBS = tariffDetail.isIncludesEBS();
					}
						
					String trvelClasString = travelSegment.getComfortClass();
						ComforClass travelclass = null;
					if (StringUtils.equalsIgnoreCase(ComforClass.FIRST.toString(), trvelClasString)) {
						travelclass = ComforClass.FIRST;
					}else {
						travelclass = ComforClass.SECOND; 
					}				
						
					StationDatabaseService stationDatabaseService = new StationDatabaseService(context);
					Station originStation = stationDatabaseService.selectStationByStationCode(travelSegment.getOriginCode());
					if(originStation != null){
						originCode = originStation.getCode();
							
					}
					Station destinationStation = stationDatabaseService.selectStationByStationCode(travelSegment.getDestinationCode());
					if(destinationStation != null){
						
						destinationCode = destinationStation.getCode();						
					}					
					String pnr = getPnr(travelSegment);
						
					String departureDateString = DateUtils.dateToString(travelSegment.getDepartureDate());
					String departureTimeString = DateUtils.timeToString(travelSegment.getDepartureTime());
						
					String departureDateTimeString = departureDateString +" " + departureTimeString;
					String sortDepartureDateString = "";
					boolean hasDepartureTime = true;
					Direction direction = travelSegment.getDirection();
					if (StringUtils.equalsIgnoreCase("", departureDateString)) {
						departureDateTimeString = DateUtils.dateTimeToString(travelSegment.getValidityStartDate());

						if (StringUtils.equalsIgnoreCase("00:00", DateUtils.FormatToHHMMFromDate(travelSegment.getValidityStartDate()))) {
							if (direction == Direction.Outward) {
								sortDepartureDateString = DateUtils.dateToString(travelSegment.getValidityStartDate()) + " 00:01:00";
							}else if (direction == Direction.Return) {
								sortDepartureDateString = DateUtils.dateToString(travelSegment.getValidityStartDate()) + " 23:58:00";
							}else {
								sortDepartureDateString = DateUtils.dateToString(travelSegment.getValidityStartDate()) + " 23:59:00";
							}			
							hasDepartureTime = false;
							}
						}	
		
					Date departureDate = DateUtils.stringToDateTime(departureDateTimeString);
					Date sortDepartureDate = null;
					if (hasDepartureTime) {
						sortDepartureDate = departureDate;							
					}else {
						sortDepartureDate = DateUtils.stringToDateTime(sortDepartureDateString);
					}							
					//Log.d("HasDepartureTime", "sortDepartureDateString......" + sortDepartureDate);
					if (dossierAftersalesLifetime != null && !StringUtils.isEmpty(dossierAftersalesLifetime)) {
						Date date = new Date();
						long days = (date.getTime() - departureDate.getTime()) / (24 *60 *60 * 1000);
						LogUtils.d("Lifetime", "days......" + days);
						if (days > Long.valueOf(dossierAftersalesLifetime)) {
							isJourneyPast = true;
							continue ;
						}
					}
					
					isJourneyPast = false;
					boolean isCorrupted = false;
					if (dossierAftersalesResponse.getState() != null && dossierAftersalesResponse.getState() == OrderItemStateType.OrderItemStateTypeConfirmed) {
						int resultLoadPdf = downloadPdf(context, dossierAftersalesResponse, travelSegment);

						
						int resultBarcode = downloadBarcode(context, dossierAftersalesResponse, travelSegment);	
						//Log.d("loadPdfUrl", "first time...resultLoadBarcode......" + resultBarcode);
						if (resultLoadPdf < 0) {
							resultLoadPdf = downloadPdf(context, dossierAftersalesResponse, travelSegment);
							//Log.d("loadPdfUrl", "second time...resultLoadPdf......" + resultLoadPdf);
							if (resultLoadPdf < 0) {
								isCorrupted = true;
								isHasError =true;
							}
						}
							
						if (resultBarcode < 0) {
							resultBarcode = downloadBarcode(context, dossierAftersalesResponse, travelSegment);
							//Log.d("loadPdfUrl", "second time...resultLoadBarcode......" + resultBarcode);
							if (resultBarcode < 0) {
								isCorrupted = true;
								isHasError =true;
							}
						}
					}
					
					String email = order.getEmail();

					String sortFirstChildDepartureDate = getFirstChildDate(dossierAftersalesResponse.getTravelSegments(), travelSegment);

					//Log.d("loadPdfUrl", "isCorrupted......" + isCorrupted);																		
					order = new Order(trainType, personNumber, trainNr, orderState, dnr, origin, originCode, destination, 
							destinationCode, pnr, departureDate, dossierGUID, travelSegmentID, includesEBS, travelclass, 
							direction, hasDepartureTime, sortDepartureDate, DateUtils.stringToDateTime(sortFirstChildDepartureDate),
							isCorrupted, email, refundable, exchangeable, rulfillmentFailed, order.isHasDuplicatedStationboard(), order.getDuplicatedStationboardId());
						
					assistantDatabaseService.insertOrder(order);
						//newOrders.add(order);										
					}
					//deletePastedFile();
					
				}
			}
/*			if(StringUtils.equalsIgnoreCase(orders.get(i).getDNR(), "CBAIQTJ") || StringUtils.equalsIgnoreCase(orders.get(i).getDNR(), "PSXSIJZ")){
				order = new Order(null, 0, null, OrderItemStateType.OrderItemStateTypeCancelled.ordinal(), 
						orders.get(i).getDNR(), null, null, null, 
						null, null, orders.get(i).getDepartureDate(), null, null, false, ComforClass.SECOND);
				
				assistantDatabaseService.insertOrder(order);
			}else {
				reBuildOrder(orders.get(i), orderItemStateType);
			}*/
			deletePastedFile(context, dossierAftersalesResponse);
		}
		if (isJourneyPast) {
			throw new JourneyPast();
		}
		return isHasError;
	}
	
	private String getFirstChildDate(List<TravelSegment> travelSegments, TravelSegment travelSegment){
		String departureDateTimeString = "";
		for (TravelSegment ts : travelSegments) {
			if (StringUtils.equalsIgnoreCase(ts.getParentId(), travelSegment.getId())) {
				String departureDateString = DateUtils.dateToString(ts.getDepartureDate());
				String departureTimeString = DateUtils.timeToString(ts.getDepartureTime());
				
					if (!"".equalsIgnoreCase(departureDateString)) {
						departureDateTimeString = departureDateString +" " + departureTimeString;
						return departureDateTimeString;
					}
				
			}
			
		}
		return travelSegment.getDepartureDate() + " " + travelSegment.getDepartureTime();
	}
	
	private String getPnr(TravelSegment travelSegment){
		
		String pnrString = "";
		if (travelSegment != null) {

			for (int i = 0; i < travelSegment.getPnrIds().size(); i++) {
				if (travelSegment.getPnrIds().get(i) != null && !StringUtils.equalsIgnoreCase(travelSegment
						.getPnrIds().get(i), "null")) {
					//Log.d(TAG, "dossierResponse.getPnrIds().get(i): "+ travelSegment.getPnrIds().get(i));
					if (i == travelSegment.getPnrIds().size() - 1) {

						pnrString += travelSegment.getPnrIds().get(i);
					} else {
						pnrString += travelSegment.getPnrIds().get(i)+ ", ";
					}
				}

				//Log.d(TAG, "pnrString: " + pnrString);
			}
		}

		//Log.d(TAG, "pnrString: " + pnrString);
		if (StringUtils.equalsIgnoreCase(pnrString, "")) {
			pnrString = "/";
		}
		return pnrString;
	}
	
	/*public GeneralSetting executeGeneralSetting(Context context) throws Exception {
		String stringHttpResponse = null;
	
		MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
		InputStream is = context.getResources().openRawResource(R.raw.general);  
		stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		System.out.println("stringHttpResponse======" + stringHttpResponse);
		
		GeneralSettingResponse generalSettingResponse = masterResponseConverter.parseGeneralSettingResponseData(stringHttpResponse);
		if (generalSettingResponse != null) {			
			return generalSettingResponse.getGeneralSetting();
		}
		return null;
	}*/
	
	public DossierAftersalesResponse getDossierAftersalesResponseFromFile(Context context, String DNR){
		String string;
		DossierAftersalesResponse dossierAftersalesResponse = null;
		try {
			LogUtils.d(TAG, "Read Dossier Aftersales response from sd card!!");
			LogUtils.d(TAG, "Read Dossier Aftersales response from sd card!!" + DNR);
			string = FileManager.getInstance().readExternalStoragePrivateFile(context, DNR, DNR + ".json");
			string = AESUtils.decrypt(DNR, string);
			LogUtils.d(TAG, "Read Dossier Aftersales response from sd card!!" + string);
			dossierAftersalesResponse = new AssistantConverter().parsesDossierAftersalesResponse(string);
		} catch (Exception e) {
			LogUtils.e(TAG, "Read Dossier Aftersales response from sd card has Error...");
			e.printStackTrace();
			return dossierAftersalesResponse;
		}	
		return dossierAftersalesResponse;
	}

	
}
