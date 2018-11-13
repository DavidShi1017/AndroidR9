package com.cflint.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.cflint.model.DossierResponse.OrderItemStateType;


public class DossierAftersalesResponse extends RestResponse {
	private final static String TAG = DossierAftersalesResponse.class
			.getSimpleName();
	private static final long serialVersionUID = 1L;

	@SerializedName("DnrId")
	private String dnrId;

	@SerializedName("State")
	private OrderItemStateType state;

	@SerializedName("TotalPrice")
	private Price totalPrice;

	@SerializedName("TotalDossierValue")
	private Price totalDossierValue;

	@SerializedName("HasInsurance")
	private boolean hasInsurance;

	@SerializedName("SelectedDeliveryMethod")
	private String selectedDeliveryMethod;

	@SerializedName("SelectedDeliveryMethodLabel")
	private String selectedDeliveryMethodLabel;
	
	@SerializedName("SelectedPaymentMethod")
	private String selectedPaymentMethod;
	
	@SerializedName("HomePrintTickets")
	private List<HomePrintTicket> homePrintTickets;

	@SerializedName("PinCodeRequired")
	private boolean pinCodeRequired;

	@SerializedName("StationForPickup")
	private String stationForPickup;

	@SerializedName("TravelSegments")
	private List<TravelSegment> travelSegments = new ArrayList<TravelSegment>();

	@SerializedName("Passengers")
	private List<Passenger> passengers = new ArrayList<Passenger>();

	private List<Ticket> Tickets = new ArrayList<Ticket>();
	
	@SerializedName("Stations")
	private List<StationInformationResult> stations = new ArrayList<StationInformationResult>();
	
	@SerializedName("Refundable")
	private String refundable;
	
	@SerializedName("Exchangeable")
	private String exchangeable;
	
	@SerializedName("FulfillmentFailed")
	private String rulfillmentFailed;

	
	/*
	 * @SerializedName("BarCodes") private List<BarCode> barCodes = new
	 * ArrayList<BarCode>();
	 */

	@SerializedName("TariffDetails")
	private List<TariffDetail> tariffDetails = new ArrayList<TariffDetail>();

	public String getDnrId() {
		return dnrId;
	}

	public Price getTotalDossierValue(){
		return this.totalDossierValue;
	}

	public List<HomePrintTicket> getHomePrintTickets() {
		return homePrintTickets;
	}


	public List<TravelSegment> getTravelSegments() {
		return travelSegments;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public List<TariffDetail> getTariffDetails() {
		return tariffDetails;
	}

	public OrderItemStateType getState() {
		return state;
	}


	public boolean isHasInsurance() {
		return hasInsurance;
	}

	public String getSelectedDeliveryMethod() {
		return selectedDeliveryMethod;
	}

	public String getSelectedPaymentMethod() {
		return selectedPaymentMethod;
	}

	public boolean isPinCodeRequired() {
		return pinCodeRequired;
	}

	public String getStationForPickup() {
		return stationForPickup;
	}

	public List<Ticket> getTickets() {
		return Tickets;
	}

	
	
	public String getSelectedDeliveryMethodLabel() {
		return selectedDeliveryMethodLabel;
	}
	
	
	public String getRefundable() {
		return refundable;
	}

	public String getExchangeable() {
		return exchangeable;
	}

	public String getRulfillmentFailed() {
		return rulfillmentFailed;
	}

	public List<StationInformationResult> getStations(List<String> stationsCode) {
		List<StationInformationResult> stationInformationResultsForCurrentTravelSegment = new ArrayList<StationInformationResult>();
		for (String stationCode : stationsCode) {
			
			for (StationInformationResult station : stations) {
				
				if (station != null && StringUtils.equalsIgnoreCase(stationCode, station.getCode()) && 
						station.getAddress() != null && station.getFacilitiesBlock() != null) {
					stationInformationResultsForCurrentTravelSegment.add(station);
				}
			}
		}
		return stationInformationResultsForCurrentTravelSegment;
	}


	public TariffDetail getTariffById(String tariffId) {
		for (TariffDetail tariffDetail : tariffDetails) {
			if (StringUtils.equalsIgnoreCase(tariffDetail.getId(), tariffId)) {
				return tariffDetail;
			}
		}
		return null;
	}

	public TravelSegment getTravelSegmentById(String travelSegmentId) {
		if (travelSegmentId != null && this.travelSegments != null) {
			for (TravelSegment travelSegment : this.travelSegments) {
				if (StringUtils.equalsIgnoreCase(travelSegment.getId(),
						travelSegmentId)) {
					return travelSegment;
				}
			}
		}
		return null;
	}

	public List<TravelSegment> getReservationForCurrentTravelSegment(
			TravelSegment travelSegment) {

		List<TravelSegment> travelSegments = new ArrayList<TravelSegment>();
		if (travelSegment != null) {
			//
				for (TravelSegment travelSegmentInTravelSegments : this.travelSegments) {
					if (!StringUtils.equalsIgnoreCase("", travelSegmentInTravelSegments.getParentId())&& travelSegmentInTravelSegments.isHasReservation()) {
						if (StringUtils.equalsIgnoreCase(travelSegmentInTravelSegments.getParentId(),travelSegment.getId())) {
							travelSegments.add(travelSegmentInTravelSegments);
						}
					}					
				}
			//}
		}
		
		
		return travelSegments;
	}

	/**
	 * get exchangable or refundable from dossieraftersales response, return yes if there is exchangeable is ture
	 * @param travelSegment
	 * @param isExchangeable
	 * @return
	 */
	public boolean getStateForTravelSegmentID(TravelSegment travelSegment,
			boolean isExchangeable) {
		String travelSegmentIdString = "";
		if (travelSegment != null) {
			travelSegmentIdString = travelSegment.getId();
		}
		for (Ticket ticket : this.getTickets()) {
			String[] travelSegmentIds = ticket.getTravelSegmentIds();
			for (String travelSegmentId : travelSegmentIds) {
				if (isExchangeable) {
					if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
							travelSegmentId) && ticket.isExchangeable()) {
						return true;
					}
				} else {
					if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
							travelSegmentId) && ticket.isRefundable()) {
						return true;
					}

				}
			}
		}
		return false;
	}
	
	/**
	 * check current travelsegment in ticket, return exchangeable value of the ticket which has current travelsegment 
	 * @param travelSegment
	 * @return
	 */
	public boolean getExchangeStateForCurrentTravelSegment(TravelSegment travelSegment){
		boolean isExchangeable = false;
		isExchangeable = getStateForTravelSegmentID(travelSegment, true);
		if (isExchangeable) {
			return true;
		}
		List<TravelSegment> reservations = getReservationForCurrentTravelSegment(travelSegment);
		for (TravelSegment reservation : reservations) {
			isExchangeable = getStateForTravelSegmentID(reservation, true);
			if (isExchangeable) {
				return true;
			}
		}
		return isExchangeable;
	}
	
	/**
	 * check current travelsegment in ticket, return refundable value of the ticket which has current travelsegment 
	 * @param travelSegment
	 * @return
	 */
	public boolean getCancellationStateForCurrentTravelSegment(TravelSegment travelSegment){
		boolean isRefundable = false;
		isRefundable = getStateForTravelSegmentID(travelSegment, false);
		if (isRefundable) {
			return true;
		}
		List<TravelSegment> reservations = getReservationForCurrentTravelSegment(travelSegment);
		for (TravelSegment reservation : reservations) {
			isRefundable = getStateForTravelSegmentID(reservation, false);
			if (isRefundable) {
				return true;
			}
		}
		return isRefundable;
	}
	public Passenger getPassengerByPassengerId(String passengerId) {
		for (Passenger passenger : this.passengers) {
			if (StringUtils.equalsIgnoreCase(passenger.getId(), passengerId)) {
				return passenger;
			}
		}
		return null;
	}

	public List<String> getStaionsCode(TravelSegment travelSegment) {

		List<String> stationsCode = new ArrayList<String>();
		List<String> stationsCodeWithReservation = new ArrayList<String>();
		if (travelSegment != null) {

			
			stationsCode.add(travelSegment.getOriginCode());
			//stationsCode.add(travelSegment.getDestinationCode());
			List<TravelSegment> travelSegments = getReservationForCurrentTravelSegment(travelSegment);
			
			for (int i = 0; i < travelSegments.size(); i++) {
				stationsCode.add(travelSegments.get(i).getOriginCode());
				stationsCode.add(travelSegments.get(i).getDestinationCode());
			}
			stationsCode.add(travelSegment.getDestinationCode());
		}
		
		Map<String, String> stationsCodeMap = new LinkedHashMap<String, String>();
		for (String stationCode : stationsCode) {
			if (!stationsCodeMap.containsKey(stationCode)) {
				
				stationsCodeMap.put(stationCode, stationCode);
			}
		}
		Iterator<Entry<String, String>> iter = stationsCodeMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			
			Entry<String, String> entry = iter.next();
			
			stationsCodeWithReservation.add(entry.getValue());
		}
		return stationsCodeWithReservation;
	}

	public List<Passenger> getPassengers(TravelSegment travelSegment) {
		List<Passenger> passengers = new ArrayList<Passenger>();
		if (travelSegment != null) {
			List<PassengerTariff> passengerTariffs = travelSegment
					.getPassengerTariffs();
			for (int i = 0; i < passengerTariffs.size(); i++) {
				String passengerId = passengerTariffs.get(i).getPassengerId();
				for (int j = 0; j < this.passengers.size(); j++) {
					if (StringUtils.equalsIgnoreCase(passengerId,
							this.passengers.get(j).getId())) {
						passengers.add(this.passengers.get(j));
					}
				}
			}
		}
		return passengers;
	}

	public List<TariffDetail> getTariffDetailsByTravelSegment(
			TravelSegment travelSegment) {
		Map<String, TariffDetail> tariffDetailMap = new HashMap<String, TariffDetail>();
		List<TariffDetail> tariffDetails = new ArrayList<TariffDetail>();
		if (travelSegment != null) {

			List<PassengerTariff> passengerTariffs = travelSegment.getPassengerTariffs();
			for (int i = 0; i < passengerTariffs.size(); i++) {
				String tariffId = passengerTariffs.get(i).getTariffId();
				for (int j = 0; j < this.tariffDetails.size(); j++) {
					

					if (StringUtils.equalsIgnoreCase(tariffId,this.tariffDetails.get(j).getId())) {

						
						//tariffDetails.add(this.tariffDetails.get(j));
						if (!tariffDetailMap.containsKey(this.tariffDetails.get(j).getId())) {
							tariffDetailMap.put(this.tariffDetails.get(j).getId(), this.tariffDetails.get(j));
							Log.e(TAG, "put getDisplayText ===========" + this.tariffDetails.get(j).getDisplayText());
						}
					}
				}
			}
		}
		Iterator<Entry<String, TariffDetail>> iter = tariffDetailMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, TariffDetail> entry = iter.next();
/*			if(entry.getValue() != null){
				Log.d(TAG, "entry.getValue() :" + entry.getValue().getDisplayText());
				if(entry.getValue().getInfoTexts() != null && entry.getValue().getInfoTexts().size() > 0){
					Log.d(TAG, "entry.getValue() :" + entry.getValue().getInfoTexts().get(0).getText());
				}
			}*/		
			tariffDetails.add(entry.getValue());
		}
		return tariffDetails;
	}

	public List<String> getBardcodes() {
		List<String> barcodes = new ArrayList<String>();
		for (Ticket ticket : this.getTickets()) {
			String[] barcodesStrings = ticket.getBarCodes();
			Log.e(TAG, "barcodesStrings size==========="
					+ barcodesStrings.length);
			for (String string : barcodesStrings) {
				barcodes.add(string);
			}
		}
		return barcodes;
	}

	/**
	 * check all tickets, get barcode of the ticket which have current.
	 * 
	 * @param travelSegment
	 * @return List<String> barcodes
	 */
	public List<String> getBarcodeOfCurrentTravelSegment(
			TravelSegment travelSegment) {

		List<String> barcodes = new ArrayList<String>();

		String travelSegmentIdString = "";
		if (travelSegment != null) {
			travelSegmentIdString = travelSegment.getId();
		}
		for (Ticket ticket : this.getTickets()) {
			String[] travelSegmentIds = ticket.getTravelSegmentIds();
			for (String travelSegmentId : travelSegmentIds) {
				if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
						travelSegmentId)) {
					String[] barcodesStrings = ticket.getBarCodes();

					for (String string : barcodesStrings) {
						barcodes.add(string);
					}
				}
			}

		}
		
		return barcodes;
	}

	/**
	 * get barcode for passengers of current travelsegment
	 * 
	 * @param travelSegment
	 * @return
	 */
	public Map<String, List<String>> getPassengerAndBarcodOfCurrentTravelSegment(
			TravelSegment travelSegment) {
		Map<String, List<String>> passengerAndBarcod = new HashMap<String, List<String>>();

		// get passengers from tickets for current travelsegment
		List<String> passengerList = getPassengerStringsOfCurrentTravelSegment(travelSegment);
		for (String passengerName : passengerList) {

			// loop tickets, get barcodes for the passengerstring
			List<String> barcodes = new ArrayList<String>();
			String travelSegmentIdString = "";
			if (travelSegment != null) {
				travelSegmentIdString = travelSegment.getId();
			}
			for (Ticket ticket : this.getTickets()) {
				String[] travelSegmentIds = ticket.getTravelSegmentIds();
				for (String travelSegmentId : travelSegmentIds) {

					if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
							travelSegmentId)
							&& StringUtils.equalsIgnoreCase(passengerName,
									getPassengersString(ticket))) {
						for (String barcodeString : ticket.getBarCodes()) {

							barcodes.add(barcodeString);

						}
						passengerAndBarcod.put(passengerName, barcodes);
					}
				}
			}
		}
		
		return passengerAndBarcod;
	}

	public List<String> getBarcodOfCurrentTravelSegment(
			TravelSegment travelSegment) {

		// loop tickets, get barcodes for the travelSegment
		List<String> barcodes = new ArrayList<String>();
		String travelSegmentIdString = "";
		if (travelSegment != null) {
			travelSegmentIdString = travelSegment.getId();
		}
		for (Ticket ticket : this.getTickets()) {
			String[] travelSegmentIds = ticket.getTravelSegmentIds();
			for (String travelSegmentId : travelSegmentIds) {

				if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
						travelSegmentId)) {
					for (String barcodeString : ticket.getBarCodes()) {
						barcodes.add(barcodeString);
					}
				}
			}
		}

		return barcodes;
	}
	
	/**
	 * get passengerstring from tickets for current travelsegment
	 * 
	 * @param travelSegment
	 * @return
	 */
	public List<String> getPassengerStringsOfCurrentTravelSegment(
			TravelSegment travelSegment) {
		String travelSegmentIdString = "";
		List<String> passengerList = new ArrayList<String>();
		if (travelSegment != null) {
			travelSegmentIdString = travelSegment.getId();
		}

		for (Ticket ticket : this.getTickets()) {
			String[] travelSegmentIds = ticket.getTravelSegmentIds();
			for (String travelSegmentId : travelSegmentIds) {

				if (StringUtils.equalsIgnoreCase(travelSegmentIdString,
						travelSegmentId)) {
					String passengerString = getPassengersString(ticket);

					passengerList.add(passengerString);

				}
			}
		}
		return passengerList;
	}

	/**
	 * //get passenger string, if have names return name seperate by ',', if
	 * not, return passenger
	 * 
	 * @param ticket
	 * @return String PassengersString like "David, Guang"
	 */
	public String getPassengersString(Ticket ticket) {
		List<String> passengerList = new ArrayList<String>();
		String passengerString = "";
		String[] passengerIds = null;
		if (ticket != null) {
			passengerIds = ticket.getPassengerIds();
		}
		if (passengerIds != null) {
			for (int i = 0; i < passengerIds.length; i++) {
				for (Passenger passenger : this.getPassengers()) {
					if (StringUtils.equalsIgnoreCase(passenger.getId(),
							passengerIds[i])) {
						passengerList.add(passenger.getName());
					}
				}
			}
		}
		if (passengerList != null && passengerList.size() > 0) {
			for (int i = 0; i < passengerList.size(); i++) {
				if (i == passengerList.size() - 1) {
					passengerString += passengerList.get(i);
				} else {
					passengerString += passengerList.get(i) + ", ";
				}
			}
		}
		return passengerString;
	}
	
	public String getTariffsDisplayText(TravelSegment travelSegment, List <TariffDetail> tariffDetails){
		String tariffsDisplayText = "";
		if(tariffDetails != null){
			for(PassengerTariff passengerTariff: travelSegment.getPassengerTariffs()){
				for(int i = 0; i < tariffDetails.size(); i ++){
					if(passengerTariff != null){
						if(StringUtils.equalsIgnoreCase(passengerTariff.getTariffId(), tariffDetails.get(i).getId())){
							if(!StringUtils.equalsIgnoreCase(tariffsDisplayText, tariffDetails.get(i).getDisplayText())){
								if (i == tariffDetails.size() - 1) {

									tariffsDisplayText += tariffDetails.get(i).getDisplayText();
								} else {
									tariffsDisplayText += tariffDetails.get(i).getDisplayText() + "\n";
								
								//tariffsDisplayText += tariffDetails.get(i).getDisplayText();
								}
							}

						}
					}				
				}
			}
			
		}
		return tariffsDisplayText;
		
	}
	
	/**
	 * get HomePrintTickets from tickets for current travelsegment
	 * 
	 * @param travelSegment
	 * @return
	 */
	public List<HomePrintTicket> getHomePrintTicketsByTravelSegment(TravelSegment travelSegment) {
		String travelSegmentIdString = "";
		List<HomePrintTicket> homePrintTickets = new ArrayList<HomePrintTicket>();
		List<String> pdfIds = new ArrayList<String>();
		if (travelSegment != null) {
			travelSegmentIdString = travelSegment.getId();
		}

		for (Ticket ticket : this.getTickets()) {
			String[] travelSegmentIds = ticket.getTravelSegmentIds();
			for (String travelSegmentId : travelSegmentIds) {
				
				if (StringUtils.equalsIgnoreCase(travelSegmentIdString,travelSegmentId)) {	
					pdfIds.add(ticket.getPdfId());					
				}
			}
		}		
		homePrintTickets = filterHomePrintTickets(pdfIds);
		return homePrintTickets;
	}
	

	/**
	 * get HomePrintTickets from tickets for current Barcode
	 * 
	 * @param travelSegment
	 * @return
	 */
	public List<HomePrintTicket> getHomePrintTicketsByBarcode(String barcode) {
		
		List<HomePrintTicket> homePrintTickets = new ArrayList<HomePrintTicket>();
		List<String> pdfIds = new ArrayList<String>();

		for (Ticket ticket : this.getTickets()) {
			String[] barCodes = ticket.getBarCodes();
			for (String barCode : barCodes) {
				if (StringUtils.equalsIgnoreCase(barcode,barCode)) {					
					pdfIds.add(ticket.getPdfId());					
				}
			}
		}		
		homePrintTickets = filterHomePrintTickets(pdfIds);
		return homePrintTickets;
	}
	
	
	private List<HomePrintTicket> filterHomePrintTickets(List<String> pdfIds){
				

		List<HomePrintTicket> homePrintTickets = new ArrayList<HomePrintTicket>();
		Map<String, String> pdfIdsMap = new LinkedHashMap<String, String>();
		for (String pdfid : pdfIds) {
			if (!pdfIdsMap.containsKey(pdfid)) {
				
				pdfIdsMap.put(pdfid, pdfid);
			}
		}
		Iterator<Entry<String, String>> iter = pdfIdsMap.entrySet()
				.iterator();
		while (iter.hasNext()) {			
			Entry<String, String> entry = iter.next();
			for (HomePrintTicket homePrintTicket : this.homePrintTickets) {
				if (homePrintTicket != null) {
					if (StringUtils.equalsIgnoreCase(homePrintTicket.getPdfId(), entry.getValue())) {

						homePrintTickets.add(homePrintTicket);
					}
				}
			}						
		}
		return homePrintTickets;
	}
	
	public int getParentTravelSegmentNumber(){
		int count = 0;
		for (int i = 0; i < this.travelSegments.size(); i++) {
			if (StringUtils.equalsIgnoreCase("", travelSegments.get(i).getParentId())) {
				count ++;
			}
			
		}
		return count;
	}
}
