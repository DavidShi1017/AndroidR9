package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;



public class Ticket implements Serializable{

	private static final long serialVersionUID = 1L;

	@SerializedName("TicketId")
	private String ticketId;

	@SerializedName("TicketNumber")
	private String ticketNumber;

	@SerializedName("TicketPrice")
	private double ticketPrice;


	@SerializedName("TicketPriceCurrency")
	private String ticketPriceCurrency;

	@SerializedName("ComfortClass")
	private int comfortClass;

	@SerializedName("TariffId")
	private String tariffId;

	@SerializedName("ProductionType")
	private String productionType;

	@SerializedName("ProductionTypeExtraInfo")
	private String productionTypeExtraInfo;

	@SerializedName("IsPDFTicket")
	private boolean isPDFTicket;

	@SerializedName("IsBarcodeTicket")
	private boolean isBarcodeTicket;

	@SerializedName("PDFId")
	private String pdfId;

	@SerializedName("BarcodeId")
	private String barcodeId;

	@SerializedName("BarcodeURL")
	private String barcodeURL;

	@SerializedName("IsExchangeable")
	private boolean isExchangeable;

	@SerializedName("NotExchangeableReasonText")
	private String notExchangeableReasonText;

	@SerializedName("ExchangeableExtraInfoText")
	private String exchangeableExtraInfoText;

	@SerializedName("IsRefundable")
	private boolean isRefundable;

	@SerializedName("NotRefundableReasonText")
	private String notRefundableReasonText;

	@SerializedName("RefundableExtraInfoText")
	private String refundableExtraInfoText;

	@SerializedName("ProvisionallyCancelledText")
	private String provisionallyCancelledText;

	@SerializedName("CancelledInventoryText")
	private String cancelledInventoryText;

	@SerializedName("ReservationCancelledText")
	private String reservationCancelledText;

	@SerializedName("SegmentPassengerIds")
	private String [] segmentPassengerIds;







	public String getTicketId() {
		return ticketId;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public double getTicketPrice() {
		return ticketPrice;
	}

	public String getTicketPriceCurrency() {
		return ticketPriceCurrency;
	}

	public int getComfortClass() {
		return comfortClass;
	}

	public String getTariffId() {
		return tariffId;
	}

	public String getProductionType() {
		return productionType;
	}

	public String getProductionTypeExtraInfo() {
		return productionTypeExtraInfo;
	}

	public boolean isPDFTicket() {
		return isPDFTicket;
	}

	public boolean isBarcodeTicket() {
		return isBarcodeTicket;
	}

	public String getBarcodeId() {
		return barcodeId;
	}

	public String getBarcodeURL() {
		return barcodeURL;
	}

	public String getNotExchangeableReasonText() {
		return notExchangeableReasonText;
	}

	public String getExchangeableExtraInfoText() {
		return exchangeableExtraInfoText;
	}

	public String getNotRefundableReasonText() {
		return notRefundableReasonText;
	}

	public String getRefundableExtraInfoText() {
		return refundableExtraInfoText;
	}

	public String getProvisionallyCancelledText() {
		return provisionallyCancelledText;
	}

	public String getCancelledInventoryText() {
		return cancelledInventoryText;
	}

	public String getReservationCancelledText() {
		return reservationCancelledText;
	}

	public String[] getSegmentPassengerIds() {
		return segmentPassengerIds;
	}

	@SerializedName("PassengerIds")
	private String [] passengerIds;

	@SerializedName("TravelSegmentIds")
	private String [] travelSegmentIds;

	@SerializedName("BarCodes")
	private String [] barCodes;

	@SerializedName("PdfId")
	private String PdfId;

	/*@SerializedName("IsExchangeable")
	private boolean exchangeable;*/

/*	@SerializedName("IsRefundable")
	private boolean refundable;*/

	public String[] getPassengerIds() {
		return passengerIds;
	}

	public String[] getTravelSegmentIds() {
		return travelSegmentIds;
	}

	public String[] getBarCodes() {
		return barCodes;
	}

	public boolean isExchangeable() {
		return isExchangeable;
	}

	public boolean isRefundable() {
		return isRefundable;
	}

	public String getPdfId() {
		return pdfId;
	}

}
