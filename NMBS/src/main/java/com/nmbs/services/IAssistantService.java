package com.nmbs.services;

import android.content.Context;
import android.graphics.Bitmap;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.Connection;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierParameter;
import com.nmbs.model.DossierResponse;
import com.nmbs.model.FAQCategory;
import com.nmbs.model.FavoriteStation;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.HomePrintTicket;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.Order;
import com.nmbs.model.Station;
import com.nmbs.model.StationBoard;
import com.nmbs.model.StationBoardCollection;
import com.nmbs.model.StationBoardLastQuery;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.services.impl.AsyncDossierAfterSaleResponse;
import com.nmbs.services.impl.AsyncStationBoardResponse;
import com.nmbs.services.impl.AsyncStationDetailResponse;
import com.nmbs.util.TickesHelper;

import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IAssistantService {

	public boolean insertOrder(OfferQuery offerQuery, Order order, Connection selectedReturnConnection);

	public List<Order> searchOrders(int flag, String dossierAftersalesLifetime);
	public StationBoardLastQuery getLastQuery();
	public List<Order> searchHistoryOrders();

	public AsyncStationBoardResponse searchStationBoard(StationBoardQuery stationBoardQuery, ISettingService settingService);

	public AsyncStationDetailResponse searchStation(String stationCode,
			ISettingService settingService);


	public String getCurrentTicketDnr();
	public void setCurrentTicketDnr(String currentTicketDnr);
	public AsyncDossierAfterSaleResponse searchDossierAfterSale(Order order, List<Order> orders, ISettingService settingService, boolean hasConnection, boolean isRefreshAllRealTime);
	public List<Order> uniteOrders(List<Order> orders, List<Order> historyOrders, List<Order> orderCanceledList);
	//public boolean isTimeToRefresh();
	public Order buildBookData(Connection selsetedDepartureConnection, DossierResponse dossierResponse, OfferQuery offerQuery, DossierParameter dossierParameter);
	public List<TickesHelper> getTickesHelpers();
	public List<TickesHelper> getTickesHistoryHelpers();
	public List<TickesHelper> getCanceledHelpers();
	public void deleteDataByDNR(String dnr);
	public void setTickesHelpers(List<TickesHelper> tickesHelpers);
	
	public Bitmap getStreamFromEncryptFile(String id, String barcodeNumber) throws FileNotFoundException;
	public GeneralSetting getGeneralSetting();

	public void setGeneralSetting(GeneralSetting generalSetting);
	public List<Order> getOrderList();

	public void setOrderList(List<Order> orderList);

	public List<Order> getOrderHistoryList();

	public void setOrderHistoryList(List<Order> orderHistoryList);

	public List<Order> getOrderCanceledList();

	public void setOrderCanceledList(List<Order> orderCanceledList);
	public boolean hasOrder();
	public List<Order> sortOrderByDirectionForFuture(List<Order> orders);
	public void deletePastTicket();
	public AsyncDossierAfterSaleResponse refrshDossierAfterSale(Order order, ISettingService settingService);
	public void openPDF(String dnr, String pdfUrl);
	
	public void deleteUndecryptPdfFile(DossierAftersalesResponse dossierAftersalesResponse, HomePrintTicket homePrintTicket);
	
	public List<FavoriteStation> getFavoriteStationCode(Context context);
	public boolean insertStationCode(FavoriteStation favoriteStation,Context context);
	public boolean deleteStationCode(String code,Context context);
	public List<StationBoard> getStationBoards();
	public List<Station> getHafasStations(String language) throws JSONException, InvalidJsonError;
	public List<StationBoard> getRealTimeForTravelSegments(Context context, String travelSegmentId, String dnr, boolean isAll);
	public DossierAftersalesResponse getDossierAftersalesResponseFromFile(String DNR);
	
	public int refreshRealTime(String currentLanguage) throws ParseException, InvalidJsonError, JSONException, TimeOutError, 
	RequestFail, IOException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;
	public void refreshRealTimeFirstTime(String currentLanguage, List<Order> orderList) throws Exception;
	public StationBoard getParentRealTimeForTravelSegments(Context context, String travelSegmentId);
	public StationBoardCollection getAllStationBoardCollection();
	public Map<String,String> getStationBoardTrainCategory();
	public void showStationFloorPlan(String stationCode, String language);
	public String getExistStationCode(String stationCode);
	public InputStream getStationFloorPlan(String stationCode, String language);
	public void refreshDossierAftersales(List<Order> orderList, String currentLanguage);
	public boolean hasExchangeable(Order order);
	public List<List<Station>> getAllStations();
	public void setAllStations(List<List<Station>> allStations);
	public List<StationBoard> getDuplicatedStationBoard(Context context, String id);

	public AsyncDossierAfterSaleResponse refrshDossierDetail(List<DossierDetailParameter> dossiers, ISettingService settingService, boolean isUpload, boolean isWaitDownload);
}
