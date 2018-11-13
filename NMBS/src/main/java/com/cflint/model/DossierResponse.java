package com.cflint.model;

import java.io.Serializable;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DossierResponse extends RestResponse implements Serializable{
	
	public enum OrderItemStateType{
		OrderItemStateTypeCancelled,
		OrderItemStateTypeUnknown,
		OrderItemStateTypeProvisional,
		OrderItemStateTypeConfirmed
	}
	public enum PaymentState{
		InProgress,
		Success,
		Failure		
	}
	public enum ComfortClass{
		FIRST,SECOND
	}
			
	private static final long serialVersionUID = 1L;
	@SerializedName("DnrId")
	private String dnrId;
	@SerializedName("PnrIds")
	private List<String> pnrIds;
	@SerializedName("HasInsurance")
	private boolean hasInsurance;
	@SerializedName("HasReservations")
	private boolean  hasReservations;
	@SerializedName("HasOverbookings")
	private boolean hasOverbookings ;
	@SerializedName("TotalPrice")
	private Price  totalPrice ;
	@SerializedName("TotalTravelPrice")
	private Price totalTravelPrice;
	@SerializedName("TotalInsurancePrice")
	private Price totalInsurancePrice ;
	@SerializedName("OrderItemState")
	private OrderItemStateType orderItemState;
	@SerializedName("PaymentState")
	private PaymentState paymentState;
	@SerializedName("SelectedDeliveryMethodCode")
	private String selectedDeliveryMethodCode;  
	@SerializedName("SelectedPaymentMethodCode")
	private String selectedPaymentMethodCode;
	@SerializedName("HomePrintTickets")
	private List<String> homePrintTickets  ;
	@SerializedName("PinCode")
	private String pinCode;
	@SerializedName("DeliveryOptions")
	private List<DeliveryOption> deliveryOptions;
	@SerializedName("Passengers")
	private List<Passenger> passengers;
	@SerializedName("SeatLocations")
	private List<SeatLocationForOD> seatLocations;
	@SerializedName("PaymentUrl")
	private String paymentUrl;
	@SerializedName("Inbound")
	private ConnectionData inbound;
	@SerializedName("Outbound")
	private ConnectionData outbound;
	@SerializedName("TotalDossierValue")
	private Price totalDossierValue;
	@SerializedName("TotalDiscountPrice")
	private Price totalDiscountPrice;
	@SerializedName("OriginStationName")
	private String originStationName;
	@SerializedName("OriginStationRCode")
	private String originStationRCode;
	@SerializedName("DestinationStationName")
	private String destinationStationName;
	@SerializedName("DestinationStationRCode")
	private String destinationStationRCOde;
	@SerializedName("NrOfPassengers")
	private int nrOfPassengers;
	@SerializedName("IsTwoWay")
	private boolean isTwoWay;
	@SerializedName("SelectedDeliveryMethod")
	private String selectedDeliveryMethod;
	@SerializedName("SelectedPaymentMethod")
	private String selectedPaymentMethod;
	@SerializedName("ComfortClass")
	private ComfortClass comfortClass;
	
	public DossierResponse(String dnrId, List<String> pnrlds, boolean hasInsurance, boolean  hasResevations, boolean hasOverbookings,
			Price  totalPrice, Price totalTravelPrice, Price insuranceCostPrice, OrderItemStateType orderItemState, PaymentState paymentState,
			String selectedDeliveryMethodCode, String selectedPaymentMethodCode, List<String> homePrintTickets, String pinCode, 
			List<DeliveryOption> deliveryOptions, List<Passenger> passengers, List<SeatLocationForOD> seatLocations,ComfortClass comfortClass) {				
	}
	public ComfortClass getComfortClass() {
		return comfortClass;
	}
	public ConnectionData getInbound() {
		return inbound;
	}
	public ConnectionData getOutbound() {
		return outbound;
	}
	public DossierResponse() {				
	}

	public String getDnrId() {
		return dnrId;
	}
	public List<String> getPnrIds() {
		return pnrIds;
	}
	public boolean isHasInsurance() {
		return hasInsurance;
	}
	public boolean isHasReservations() {
		return hasReservations;
	}
	public boolean isHasOverbookings() {
		return hasOverbookings;
	}
	public Price getTotalPrice() {
		return totalPrice;
	}
	public Price getTotalTravelPrice() {
		return totalTravelPrice;
	}
	
	public Price getInsuranceCostPrice() {
		return totalInsurancePrice;
	}
	public OrderItemStateType getOrderItemState() {
		return orderItemState;
	}
	public String getSelectedDeliveryMethodCode() {
		return selectedDeliveryMethodCode;
	}
	public String getSelectedPaymentMethodCode() {
		return selectedPaymentMethodCode;
	}
	public List<String> getHomePrintTickets() {
		return homePrintTickets;
	}
	public String getPinCode() {
		return pinCode;
	}
	public List<DeliveryOption> getDeliveryOptions() {
		return deliveryOptions;
	}
	public List<SeatLocationForOD> getSeatLocations() {
		return seatLocations;
	}
	
	public PaymentState getPaymentState() {
		return paymentState;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public String getPaymentUrl() {
		return paymentUrl;
	}
	public Price getTotalInsurancePrice() {
		return totalInsurancePrice;
	}
	public Price getTotalDossierValue() {
		return totalDossierValue;
	}
	public Price getTotalDiscountPrice() {
		return totalDiscountPrice;
	}
	public String getOriginStationName() {
		return originStationName;
	}
	public String getDestinationStationName() {
		return destinationStationName;
	}
	public String getDestinationStationRCOde() {
		return destinationStationRCOde;
	}
	public int getNrOfPassengers() {
		return nrOfPassengers;
	}
	public boolean isTwoWay() {
		return isTwoWay;
	}
	public String getSelectedDeliveryMethod() {
		return selectedDeliveryMethod;
	}
	public String getSelectedPaymentMethod() {
		return selectedPaymentMethod;
	}
	
}
