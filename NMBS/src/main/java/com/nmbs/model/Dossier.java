package com.nmbs.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.util.ComparatorTravelSegmentDate;

public class Dossier implements Serializable {
	
	@SerializedName("DossierId")
	private String dossierId;
	@SerializedName("EarliestTravelDate")
	private Date earliestTravelDate;
	@SerializedName("LatestTravelDate")
	private Date latestTravelDate;
	@SerializedName("LastUpdated")
	private Date lastUpdated;
	@SerializedName("TotalDossierValue")
	private float totalDossierValue;
	@SerializedName("DossierCurrency")
	private String dossierCurrency;
	@SerializedName("HasInsurance")
	private boolean hasInsurance;
	@SerializedName("FullfilmentFailed")
	private boolean fullfilmentFailed;
	@SerializedName("DossierState")
	private String dossierState;
	@SerializedName("MissingPDFs")
	private boolean missingPDFs;
	
	@SerializedName("PickUpStationNames")
	private String pickUpStationNames[];
	@SerializedName("PincodeNeeded")
	private boolean pincodeNeeded;
	@SerializedName("HasGreenpointsPayment")
	private boolean hasGreenpointsPayment;
	@SerializedName("PDFs")
	private List<PDF> pdfs;
	
	@SerializedName("Connections")
	private List<Connection> connections;
	
	@SerializedName("Tariffs")
	private List<Tariff> tariffs = new ArrayList<Tariff>();
	
	@SerializedName("TravelSegments")
	private List<DossierTravelSegment> travelSegments = new ArrayList<DossierTravelSegment>();

	public String getDossierId() {
		return dossierId;
	}

	public Date getEarliestTravelDate() {
		return earliestTravelDate;
	}

	public Date getLatestTravelDate() {
		return latestTravelDate;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public float getTotalDossierValue() {
		return totalDossierValue;
	}

	public String getDossierCurrency() {
		return dossierCurrency;
	}

	public boolean isHasInsurance() {
		//return true;
		return hasInsurance;
	}

	public boolean isFullfilmentFailed() {
		return false;
	}

	public String getDossierState() {
		//return DossierDetailsService.DossierStateInProgress;
		return dossierState;
	}

	public boolean isMissingPDFs() {

		return missingPDFs;
	}

	public String[] getPickUpStationNames() {
		return pickUpStationNames;
	}

	public boolean isPincodeNeeded() {
		return pincodeNeeded;
	}

	public boolean isHasGreenpointsPayment() {
		return hasGreenpointsPayment;
	}

	public List<PDF> getPdfs() {
		return pdfs;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public List<Tariff> getTariffs() {
		return tariffs;
	}

	public void setDossierState(String dossierState) {
		this.dossierState = dossierState;
	}

	public List<DossierTravelSegment> getTravelSegments() {

		/*Comparator<DossierTravelSegment> comp = new ComparatorTravelSegmentDate(travelSegments);
		Collections.sort(travelSegments, comp);*/

		return travelSegments;
	}

	public void setTravelSegments(List<DossierTravelSegment> travelSegments){
		this.travelSegments = travelSegments;
	}
	
	public List<DossierTravelSegment> getTravelSegmentsForDossierList(){
		List<DossierTravelSegment> travelSegments = new ArrayList<>();
		for (DossierTravelSegment dossierTravelSegment : this.travelSegments) {
			if(dossierTravelSegment != null){
				if((dossierTravelSegment.getConnectionId() == null || dossierTravelSegment.getConnectionId().isEmpty()) &&
						(dossierTravelSegment.getParentTravelSegmentId() == null || dossierTravelSegment.getParentTravelSegmentId().isEmpty())){
					travelSegments.add(dossierTravelSegment);
				}
			}
		}
		return travelSegments;
	}
	public DossierTravelSegment getDossierTravelSegment(String id){
		for (DossierTravelSegment dossierTravelSegment : this.travelSegments) {
			if(id != null && !id.isEmpty()){
				if(id.equalsIgnoreCase(dossierTravelSegment.getTravelSegmentId())){
					return dossierTravelSegment;
				}
			}
		}
		return null;
	}

	public String [] getReservationCode(){
		List<String> numbers = new ArrayList<>();
		for (DossierTravelSegment dossierTravelSegment : this.travelSegments) {
			if(dossierTravelSegment != null){
				for(String number : dossierTravelSegment.getInventoryDossierNumbers()){
					if(!numbers.contains(number)){
						numbers.add(number);
					}
					/*for (String numberInList : numbers){
						if(!number.equals(numberInList)){
							numbers.add(number);
						}
					}*/
				}
			}
		}
		String[] arr = numbers.toArray(new String[numbers.size()]);
		return arr;
	}

	public Tariff getTariff(String id){
		for(Tariff tariff : tariffs){
			if(tariff != null && tariff.getTariffId() != null && tariff.getTariffId().equalsIgnoreCase(id)){
				return tariff;
			}
		}
		return null;
	}

	public PDF getPdf(String pdfId){
		for(PDF pdf : pdfs){
			if(pdf != null && pdf.getPdfId()!= null && pdf.getPdfId().equalsIgnoreCase(pdfId)){
				return pdf;
			}
		}
		return null;
	}

	public List<DossierTravelSegment> getChildTravelSegments(DossierTravelSegment dossierTravelSegment){
		List<DossierTravelSegment> dossierChildTravelSegments  = new ArrayList<>();
		if(dossierTravelSegment != null && dossierTravelSegment.getParentTravelSegmentId() != null && dossierTravelSegment.getParentTravelSegmentId().isEmpty()){
			for (DossierTravelSegment dossierTravelSegmentChild : travelSegments){
				if (dossierTravelSegmentChild != null && dossierTravelSegmentChild.getParentTravelSegmentId().equalsIgnoreCase(dossierTravelSegment.getTravelSegmentId())){
					dossierChildTravelSegments.add(dossierTravelSegmentChild);
				}
			}
		}
		return dossierChildTravelSegments;
	}

	public List<DossierTravelSegment> getParentTravelSegments(){
		List<DossierTravelSegment> dossierParentTravelSegments  = new ArrayList<>();
		for (DossierTravelSegment dossierTravelSegment : travelSegments){
			if (dossierTravelSegment != null){
				if(dossierTravelSegment.getParentTravelSegmentId() == null || dossierTravelSegment.getParentTravelSegmentId().isEmpty()){
					dossierParentTravelSegments.add(dossierTravelSegment);
				}
			}
		}
		Comparator<DossierTravelSegment> comp = new ComparatorTravelSegmentDate(travelSegments);
		Collections.sort(dossierParentTravelSegments, comp);
		return dossierParentTravelSegments;
	}

	public DossierTravelSegment getParentTravelSegment(DossierTravelSegment travelSegment){
		for (DossierTravelSegment dossierTravelSegment : travelSegments){
			if (dossierTravelSegment != null && travelSegment.getParentTravelSegmentId().equalsIgnoreCase(dossierTravelSegment.getTravelSegmentId())){
				return dossierTravelSegment;
			}
		}
		return null;
	}

	public List<MyTicket> getMyTicketData(){
		List<MyTicket> myTickets = new ArrayList<>();
		List<Connection> connections = getConnections();
		List<DossierTravelSegment> dtsList = getTravelSegmentsForDossierList();
		for(Connection connection : connections){
			if(connection != null){
				MyTicket myTicket = new MyTicket(connection.getOriginStationName(), connection.getDestinationStationName(),
						connection.getDeparture(), connection.getDeparture());
				myTickets.add(myTicket);
			}
		}
		for(DossierTravelSegment dossierTravelSegment : dtsList){
			if(dossierTravelSegment != null){
				Date sortedDate = dossierTravelSegment.getSortedDate(travelSegments);
				MyTicket myTicket = new MyTicket(dossierTravelSegment.getOriginStationName(), dossierTravelSegment.getDestinationStationName(),
						dossierTravelSegment.getDepartureDate(), sortedDate);
				myTickets.add(myTicket);
			}
		}
		return myTickets;
	}
}
