package com.cfl.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.cfl.R;
import com.cfl.model.PartyMember.FtpCardFeedbackTypes;
import com.cfl.model.PartyMember.PersonType;
import com.cfl.model.validation.IOfferRequestFeedback;
import com.cfl.util.CardUtils;
import com.cfl.util.ComparatorPassengers;
import com.cfl.util.DateUtils;
import com.cfl.util.LocaleChangedUtils;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data model implementation record some info for user input in PlannerActivity.
 */
public class OfferQuery implements Serializable {

	private static final long serialVersionUID = -4434123649712964750L;
	private final static String TAG = OfferQuery.class.getSimpleName();

	public enum ComforClass {
		FIRST, SECOND
	}

	public enum TravelType {
		ONEWAY, RETURN
	}
	
	
	private String originStationName;
	@SerializedName("OriginStationName")
	private String originStationDestinationName;
	@SerializedName("OriginStationRCode")
	private String originStationRCode;
	
	@SerializedName("OriginStationSynonymeName")
	private String originStationSynonymeName;
	
	@SerializedName("DestinationStationSynonymeName")
	private String destinationStationSynonymeName;
	private String destinationStationName;
	@SerializedName("DestinationStationName")
	private String destinationStationDestinationName;
	@SerializedName("DestinationStationRCode")
	private String destinationStationRCode;
	
	@SerializedName("TravelType")
	private TravelType travelType;
	@SerializedName("ComfortClass")
	private ComforClass comforClass;
	private TravelRequest departureQueryParameters = new TravelRequest();
	private TravelRequest returnQueryParameters = new TravelRequest();
	
	@SerializedName("PreferredCurrency")
	private String preferredCurrency;
	
	@SerializedName("TravelParty")
	private List<PartyMember> travelPartyMember = new ArrayList<PartyMember>();
	@SerializedName("CorporateCards")
	private List<CorporateCard> listCorporateCards = new ArrayList<CorporateCard>();
	@SerializedName("GreenPointsNumber")
	private String greenPointsNumber;
	
	/*@SerializedName("MultiSegment")
	private boolean multiSegment = true;*/
	
	@SerializedName("ODScope")
	private String ODScope = "MultiSegmentExtended";
	

	private boolean isHasGreenPointsNumber;
	
	@SerializedName("TicketLanguage")
	private String ticketLanguage;

	
	private boolean isFavorite;
	
	private Date createDate;
	
	private Date lastUsedDate;
    
	/*public void addGroupPartyMember(PartyMember partyMember){
		
		PersonType personType = partyMember.getPersonType();
		
		if (personType == PersonType.KID0 || personType == PersonType.KID4 || personType == PersonType.KID6) {
			groupChildren.add(partyMember);
		}else if (personType == PersonType.KID12 || personType == PersonType.KID15) {
			groupYoungsters.add(partyMember);
		}else {
			groupAdult.add(partyMember);
		}				
	}
	

	public List<PartyMember> getGroupChildren() {
		return groupChildren;
	}

	public List<PartyMember> getGroupYoungsters() {
		return groupYoungsters;
	}

	public List<PartyMember> getGroupAdult() {
		return groupAdult;
	}*/

	
	
	public boolean isHasGreenPointsNumber() {
		return isHasGreenPointsNumber;
	}

	public String getOriginStationSynonymeName() {
		return originStationSynonymeName;
	}

	public void setOriginStationSynonymeName(String originStationSynonymeName) {
		this.originStationSynonymeName = originStationSynonymeName;
	}

	public String getDestinationStationSynonymeName() {
		return destinationStationSynonymeName;
	}

	public void setDestinationStationSynonymeName(
			String destinationStationSynonymeName) {
		this.destinationStationSynonymeName = destinationStationSynonymeName;
	}

	public String getODScope() {
		return ODScope;
	}

	public void setHasGreenPointsNumber(boolean isHasGreenPointsNumber) {
		this.isHasGreenPointsNumber = isHasGreenPointsNumber;
	}

	public String getGreenPointsNumber() {
		return greenPointsNumber;
	}

	public void setGreenPointsNumber(String greenPointsNumber) {
		this.greenPointsNumber = greenPointsNumber;
	}

	public List<CorporateCard> getListCorporateCards() {
		return listCorporateCards;
	}

	public void setListCorporateCards(List<CorporateCard> listCorporateCards) {
		this.listCorporateCards = listCorporateCards;
	}

	// the isCheckedInsurance is temporary;
	private boolean isCheckedInsurance;
	
	public String getDestinationStationDestinationName() {
		return destinationStationDestinationName;
	}

	public void setDestinationStationDestinationName(
			String destinationStationDestinationName) {
		this.destinationStationDestinationName = destinationStationDestinationName;
	}

	public boolean isCheckedInsurance() {
		return isCheckedInsurance;
	}

	public void setCheckedInsurance(boolean isCheckedInsurance) {
		this.isCheckedInsurance = isCheckedInsurance;
	}
	
	@SuppressWarnings("unused")
	private IOfferRequestFeedback offerRequestFeedback;

	public enum OfferRequestFeedbackTypes {
		WRONGTIME, CORRECT, SAMESTATION, DEPARTURELATERTHANRETURN, TOTALTICKETNUMBERWRONG, 
		EMPTYVALUE, EMPTYDEPARTUREDATE, EMPTYRETURNDATE, DEPARTUREDATEWRONG,
		OfferQueryFeedbackTypeNoPartyMember, OfferQueryFeedbackTypeMoreThanSix, OfferQueryFeedbackTypeInvalidCombination, 
		OfferQueryFeedbackTypeReductionCardDup, OfferQueryFeedbackTypeFTPCardDup, OfferQueryFeedbackTypeInvalidEurostar, 
		OfferQueryFeedbackTypeInvalidThalys
		
	}
	


	public void setIOfferRequestFeedback(
			IOfferRequestFeedback offerRequestFeedback) {
		this.offerRequestFeedback = offerRequestFeedback;
	}

	public void clearIOfferRequestFeedback() {
		
		offerRequestFeedback = null;
	}

	public String getOriginStationRCode() {
		return originStationRCode;
	}

	public void setOriginStationRCode(String originStationRCode) {
		this.originStationRCode = originStationRCode;
	}

	public String getDestinationStationRCode() {
		return destinationStationRCode;
	}

	public void setDestinationStationRCode(String destinationStationRCode) {
		this.destinationStationRCode = destinationStationRCode;
	}

	public TravelType getTravelType() {
		return travelType;
	}

	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}

	public ComforClass getComforClass() {
		return comforClass;
	}

	public void setComforClass(ComforClass comforClass) {
		this.comforClass = comforClass;
	}

	public List<PartyMember> getTravelPartyMembers() {
		if (this.travelPartyMember == null) {
			this.travelPartyMember = new ArrayList<PartyMember>();
		}
		return travelPartyMember;
	}

	public void setTravelPartyMember(List<PartyMember> travelPartyMember) {
		this.travelPartyMember = travelPartyMember;
	}

	public void addTravelParty(PartyMember partyMember) {
		travelPartyMember.add(partyMember);
	}
	
	

	public void deleteTravelParty(PartyMember partyMember) {
		travelPartyMember.remove(partyMember);
	}
	
	public void clearTravelParty() {
		travelPartyMember.clear();
	}

	public String getOriginStationName() {
		return originStationName;
	}

	public void setOriginStationName(String originStationName) {
		this.originStationName = originStationName;
	}

	public String getOriginStationDestinationName() {
		return originStationDestinationName;
	}

	public void setOriginStationDestinationName(String originStationDestinationName) {
		this.originStationDestinationName = originStationDestinationName;
	}

	public String getDestinationStationName() {
		return destinationStationName;
	}

	public void setDestinationStationName(String destinationStationName) {
		this.destinationStationName = destinationStationName;
	}

	public TravelRequest getDepartureQueryParameters() {
		return departureQueryParameters;
	}

	public void setDepartureQueryParameters(
			TravelRequest departureQueryParameters) {
		this.departureQueryParameters = departureQueryParameters;
	}

	public TravelRequest getReturnQueryParameters() {
		return returnQueryParameters;
	}

	public void setReturnQueryParameters(TravelRequest returnQueryParameters) {
		this.returnQueryParameters = returnQueryParameters;
	}

	public String getPreferredCurrency() {
		return preferredCurrency;
	}

	public void setPreferredCurrency(String preferredCurrency) {
		this.preferredCurrency = preferredCurrency;
	}
	
	

	/*public boolean isMultiSegment() {
		return multiSegment;
	}*/
	
	

	public String getTicketLanguage() {
		return ticketLanguage;
	}

	public void setTicketLanguage(String ticketLanguage) {
		this.ticketLanguage = ticketLanguage;
	}

	/**
	 * validate ticket number
	 * 
	 * @return
	 */
	public OfferRequestFeedbackTypes validatePartyMember() {
		if (this.travelPartyMember.size() > 2 || this.travelPartyMember.size() <= 0) {
			return OfferRequestFeedbackTypes.TOTALTICKETNUMBERWRONG;
		}
		return OfferRequestFeedbackTypes.CORRECT;
	}

	/**
	 * validate date
	 * 
	 * @return
	 */
	public OfferRequestFeedbackTypes validateDepartReturnDate() {
		if (isDepartDateLaterReturnDate(departureQueryParameters.getDateTime(),
				returnQueryParameters.getDateTime())) {
			return OfferRequestFeedbackTypes.DEPARTURELATERTHANRETURN;
		}
		return OfferRequestFeedbackTypes.CORRECT;
	}

	/**
	 * validate date before the today don't select
	 * 
	 * @return
	 */
	public OfferRequestFeedbackTypes validateDate(Date date) {
		if (isDateAfterToday(date)) {
			return OfferRequestFeedbackTypes.CORRECT;
		}
		return OfferRequestFeedbackTypes.WRONGTIME;
	}

	/**
	 * Validate info for user input.
	 * 
	 * @return
	 */
	public OfferRequestFeedbackTypes validate() {

		this.validatePartyMember();

		if (isEmptyText(originStationRCode)) {
			return OfferRequestFeedbackTypes.EMPTYVALUE;
			//return OfferRequestFeedbackTypes.CORRECT;
		}
		if (isEmptyText(destinationStationRCode)) {
			return OfferRequestFeedbackTypes.EMPTYVALUE;
			//return OfferRequestFeedbackTypes.CORRECT;
		}
		if (isSameStation(originStationRCode, destinationStationRCode)) {

			return OfferRequestFeedbackTypes.SAMESTATION;
		}

		if (isEmptyDepartDate()) {
			
			return OfferRequestFeedbackTypes.EMPTYDEPARTUREDATE;
		}
		if(departureQueryParameters != null){
			if (!isDateAfterToday(departureQueryParameters.getDateTime())) {
				return OfferRequestFeedbackTypes.DEPARTUREDATEWRONG;
			}
		}
	

		if (isEmptyReturnDate()) {

			return OfferRequestFeedbackTypes.EMPTYRETURNDATE;
		}
		if(departureQueryParameters != null && returnQueryParameters != null){
			if (isDepartDateLaterReturnDate(departureQueryParameters.getDateTime(),
					returnQueryParameters.getDateTime())) {
				return OfferRequestFeedbackTypes.DEPARTURELATERTHANRETURN;
			}
		}
		

		return OfferRequestFeedbackTypes.CORRECT;

	};
	
	

	/**
	 * validate Date s null
	 * 
	 * @return boolean if it is empty value return true
	 */
	private boolean isEmptyDepartDate() {
		if (departureQueryParameters.getDateTime() == null) {
			return true;
		}
		return false;
	}

	private boolean isEmptyReturnDate() {
		if (getTravelType() == TravelType.ONEWAY) {
			return false;
		}
		if (returnQueryParameters.getDateTime() == null) {
			return true;
		}
		return false;
	}

	private boolean isEmptyText(String str) {

		if ("".equals(str) || str == null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isSameStation(String from, String to) {
		if (from != null && to != null && from.trim().equals(to.trim()) 
				&& !StringUtils.equalsIgnoreCase("", from) && !StringUtils.equalsIgnoreCase("", to)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDepartDateLaterReturnDate(Date departDate, Date returnDate) {
		Log.d(TAG, "departDate is " + departDate + "   " + "returnDate is "
				+ returnDate);
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyy/MM/dd  HH:mm");

		if (getTravelType() == TravelType.RETURN) {
			if (departDate != null && returnDate != null) {
				try {

					departDate = myFmt.parse(myFmt.format(departDate));
					returnDate = myFmt.parse(myFmt.format(returnDate));

				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (departDate.equals(returnDate)) {
					return true;
				} else {
					return departDate.after(returnDate);
				}

			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isDateAfterToday(Date date) {
		Date today = new Date();
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
		if (date != null) {
			try {
				today = myFmt.parse(myFmt.format(today));
				date = myFmt.parse(myFmt.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (today.equals(date)) {
				return true;
			} else {
				return date.after(today);
			}

		} else {
			return false;
		}
	}

	public int getNumberOfPartyMember(PersonType personType) {
		int k = 0;
		for (int i = 0; i < travelPartyMember.size(); i++) {
			PersonType type = travelPartyMember.get(i).getPersonType();
			if (personType == type) {
				k++;
			}
		}
		return k;
	}

	public void setNumberOfPartyMember(PersonType personType, int k) {
		PartyMember partyMember = null;		
		ReductionCard reductionCard = null;
		
		for (int i = 0; i < k; i++) {
			 partyMember = new PartyMember(personType);
			 reductionCard = new ReductionCard("","");
			 List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
			 reductionCards.add(reductionCard);
			 partyMember.setReductionCards(reductionCards);
			 this.addTravelParty(partyMember);
		}
	}

	// Get the text info that user has selected in UI
	public String getSelectedPassengerText(Context context) {
		String passengersText = "";

		/*
		 * List<PartyMember> partyMembers = getTravelPartyMembers(); if
		 * (partyMembers != null) { for (PartyMember partyMember : partyMembers)
		 * { if (partyMember != null) {
		 * 
		 * System.out.println("partyMember=====" + partyMember.getPersonType());
		 * passengersText = getNumberOfPartyMember(partyMember.getPersonType())
		 * + " " + getPersonTypeName(partyMember.getPersonType(), context);
		 * 
		 * } } }
		 */

		Map<Enum<PersonType>, PartyMember> map = getKindOfPartyMembers();
		Collection<PartyMember> collection = map.values();

		Iterator<PartyMember> it = collection.iterator();
		for (int i = 0; it.hasNext(); i++) {
			PartyMember partyMember = it.next();
			if (i == map.size() - 1) {
				passengersText += getNumberOfPartyMember(partyMember.getPersonType())
						+ " "
						+ getPersonTypeName(partyMember.getPersonType(), context, getNumberOfPartyMember(partyMember.getPersonType()));
			} else {
				/*if ((i > 0) && ((i+1) % 2) == 0) {
					passengersText += getNumberOfPartyMember(partyMember.getPersonType())
							+ " "
							+ getPersonTypeName(partyMember.getPersonType(), context, getNumberOfPartyMember(partyMember.getPersonType())) + ", \n";
				}else{*/
					passengersText += getNumberOfPartyMember(partyMember.getPersonType())
							+ " "
							+ getPersonTypeName(partyMember.getPersonType(), context, getNumberOfPartyMember(partyMember.getPersonType())) + ", ";
				//}	
			}
		}
		/*
		 * passengersText = getNumberOfPartyMember(PersonType.PersonTypeAdult) >
		 * 0 ? passengersText
		 * 
		 * + getNumberOfPartyMember(PersonType.PersonTypeAdult) + " " +
		 * c.getString(R.string.general_adult) : passengersText; passengersText
		 * = getNumberOfPartyMember(PersonType.PersonTypeSenior) > 0 ?
		 * passengersText
		 * 
		 * + getNumberOfPartyMember(PersonType.PersonTypeSenior) + " " +
		 * c.getString(R.string.general_senior) : passengersText; passengersText
		 * = getNumberOfPartyMember(PersonType.PersonTypeYoungster) > 0 ?
		 * passengersText
		 * 
		 * + getNumberOfPartyMember(PersonType.PersonTypeYoungster) + " " +
		 * c.getString(R.string.general_youth) : passengersText;
		 */

		return passengersText;
	}
	
	
	public Map<Enum<PersonType>, PartyMember> getKindOfPartyMembers(){
		
		Map<Enum<PersonType>, PartyMember> map = new LinkedHashMap<Enum<PersonType>, PartyMember>();	
		
		for (PartyMember partyMember : travelPartyMember) {
			if (!map.containsKey(partyMember.getPersonType())) {
				map.put(partyMember.getPersonType(), partyMember);
			}
		}
		return map;
	}
	

	// Get the text info that user has selected in UI
	public String getSelectedDepartureDate(int which, Context c) {
		
		if (getDepartureQueryParameters().getDateTime() != null) {
			if(getReturnQueryParameters() != null && getReturnQueryParameters().getDateTime() != null && which == 0){
				if(getDepartureQueryParameters().getDateTime().after(getReturnQueryParameters().getDateTime())){
					getReturnQueryParameters().setDateTime(getDepartureQueryParameters().getDateTime());
					
				}
			}
			
			return DateUtils.dateToString(c, getDepartureQueryParameters()
					.getDateTime())
					+ " | ";
		} else {
			return c.getString(R.string.planner_view_select_date_and_time);
		}
	}

	// Get the text info that user has selected in UI
	public String getSelectedDepartureTime(Context c) {
		if (getDepartureQueryParameters().getDateTime() != null) {
			return DateUtils.getTime(c, getDepartureQueryParameters()
					.getDateTime());
		} else {
			return "";
		}
	}

	// Get the text info that user has selected in UI
	public String getSelectedReturnDate(int which, Context c) {
		
			if (getReturnQueryParameters() != null && getReturnQueryParameters().getDateTime() != null) {
				if(getDepartureQueryParameters().getDateTime() != null && which== 1){
					if(getReturnQueryParameters().getDateTime().before(getDepartureQueryParameters().getDateTime())){

						getDepartureQueryParameters().setDateTime(getReturnQueryParameters().getDateTime());
					}
				}
				
				return DateUtils.dateToString(c, getReturnQueryParameters()
						.getDateTime())
						+ " | ";
			} else {
				return c.getString(R.string.planner_view_select_date_and_time);
			}

	}

	// Get the text info that user has selected in UI
	public String getSelectedReturnTime(Context c) {
		if(getReturnQueryParameters() != null){
			if (getReturnQueryParameters().getDateTime() != null) {
				return DateUtils.getTime(c, getReturnQueryParameters()
						.getDateTime());
			} else {
				return "";
			}
		}
		return "";
	}

	public void toggleComforClass() {
		if (this.getComforClass() == ComforClass.FIRST) {
			this.setComforClass(ComforClass.SECOND);
		} else {
			this.setComforClass(ComforClass.FIRST);
		}
	}
	
	/**
	 * Get resource text to display for persion name.
	 * @param personType
	 * @param context
	 * @param persionCount if this param > 1, the return value will be plural.
	 * @return
	 */
	
	public String getPersonTypeName(PersonType personType, Context context, int persionCount) {	
		String personTypeString;
		
		switch (personType) {
		case KID0:
			
			if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_children_0_3);
			}else {
				
				personTypeString = context.getString(R.string.general_child_0_3);
			}			
			return personTypeString;
			
		case KID4:
			
			if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_children_4_5);
			}else {				
				personTypeString = context.getString(R.string.general_child_4_5);
			}				
			return personTypeString;
			
		case KID6:
			
			if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_children_6_11);
			}else {				
				personTypeString = context.getString(R.string.general_child_6_11);
			}
			return personTypeString;
			
		case KID12:
			
			if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_youths_12_14);
			}else {				
				personTypeString = context.getString(R.string.general_youth_12_14);
			}
			return personTypeString;
			
		case KID15:
			
			/*if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_youths_15_17);
			}else {
				
				//personTypeString = context.getString(R.string.general_youth_15_17);;
			}
			return personTypeString;*/
		case YOUTH:
			
			if (persionCount > 1) {
				personTypeString = context.getString(R.string.general_youths_15_25);
			}else {				
				personTypeString = context.getString(R.string.general_youth_15_25);;
			}
			return personTypeString;
			
		case ADULT:
			
			if (persionCount > 1) {				
				personTypeString = context.getString(R.string.general_adults_26_59);
			}else {
				personTypeString = context.getString(R.string.general_adult_26_59);;
			}
			return personTypeString;
			
		case SENIOR:
			
			if (persionCount > 1) {				
				personTypeString = context.getString(R.string.general_seniors_60);
			}else {
				personTypeString = context.getString(R.string.general_senior_60);;
			}
			return personTypeString;

		default:
			return "";
			
		}
	}
	
	public OfferRequestFeedbackTypes validateThalysCard(){
	
		if (travelPartyMember != null) {
			for (int i = 0; i < travelPartyMember.size(); i++) {				
				String eurostarFtpCard = travelPartyMember.get(i).getEurostarFtpCard();
				String thalysFtpCard = travelPartyMember.get(i).getThalysFtpCard();
				
				if (eurostarFtpCard != null && !StringUtils.isEmpty(eurostarFtpCard)) {
					if(!CardUtils.isValidFTPCard(eurostarFtpCard)){
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeInvalidEurostar;								
					}	
					if (!CardUtils.luhnTest(eurostarFtpCard)) {
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeInvalidEurostar;
					}
				}
				if (thalysFtpCard != null && !StringUtils.isEmpty(thalysFtpCard)) {
					if(!CardUtils.isValidFTPCard(thalysFtpCard)){
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeInvalidThalys;
					}	
					if (!CardUtils.luhnTest(thalysFtpCard)) {
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeInvalidThalys;
					}
				}			
			}			
			Map<String, String> map = new LinkedHashMap<String, String>();
			
			for (PartyMember partyMember : travelPartyMember) {
				String eurostarFtpCard = partyMember.getEurostarFtpCard();
				//System.out.println("eurostarFtpCard========" + eurostarFtpCard);
				
				String thalysFtpCard = partyMember.getThalysFtpCard();				
				if (eurostarFtpCard != null && !StringUtils.isEmpty(eurostarFtpCard)) {
					//System.out.println("map========" + map.containsKey(eurostarFtpCard));
					if (map.containsKey(eurostarFtpCard)) {
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeFTPCardDup;
					}
					map.put(eurostarFtpCard, eurostarFtpCard);
				}
				//System.out.println("eurostarFtpCard========" + eurostarFtpCard);
				if (thalysFtpCard != null && !StringUtils.isEmpty(thalysFtpCard)) {
					if (map.containsKey(thalysFtpCard)) {
						return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeFTPCardDup;
					}
					map.put(thalysFtpCard, thalysFtpCard);
				}
			}return OfferRequestFeedbackTypes.CORRECT;
		}
		return OfferRequestFeedbackTypes.CORRECT;
	}
	
	public FtpCardFeedbackTypes validateFtpCard() {
		FtpCardFeedbackTypes ftpCardFeedbackTypes;
		boolean isValidate = false;
		if (travelPartyMember != null) {
			for (int i = 0; i < travelPartyMember.size(); i++) {
				ftpCardFeedbackTypes =  travelPartyMember.get(i).validateFtpCard(i);
				//System.out.println("ftpCardFeedbackTypes======FtpCard" + (i) +ftpCardFeedbackTypes);
				if (ftpCardFeedbackTypes == FtpCardFeedbackTypes.CORRECT) {
					isValidate = true;
				}else {
					isValidate = false;
					return ftpCardFeedbackTypes;
				}
			}
			if (isValidate) {
				if (travelPartyMember.size() == 2) {
					if (StringUtils.equalsIgnoreCase(travelPartyMember.get(0).getEurostarFtpCard(), travelPartyMember.get(1).getEurostarFtpCard())) {
						if (StringUtils.isNotEmpty(travelPartyMember.get(0).getEurostarFtpCard()) && StringUtils.isNotEmpty(travelPartyMember.get(1).getEurostarFtpCard())) {
							//travelPartyMember.get(1).setEurostarFtpCard("");
							return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardDup;
						}
						
					}
					if (StringUtils.equalsIgnoreCase(travelPartyMember.get(0).getThalysFtpCard(), travelPartyMember.get(1).getThalysFtpCard())) {
						if (StringUtils.isNotEmpty(travelPartyMember.get(0).getThalysFtpCard()) && StringUtils.isNotEmpty(travelPartyMember.get(1).getThalysFtpCard())) {
							//travelPartyMember.get(1).setThalysFtpCard("");
							return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardDup;
						}						
					}
				}
			}
			return FtpCardFeedbackTypes.CORRECT;
		}				
		return FtpCardFeedbackTypes.CORRECT;
	}
	
	public int getPassengerCountByType(PersonType personType){
		int count = 1;
		for (PartyMember partyMember : travelPartyMember) {
			if (partyMember.getPersonType() == personType) {
				count ++;
			}
		}
/*		if (count == 0) {
			return 1;
		}else {
			return count;
		}*/	
		return count;
	}
	
	public List<PartyMember> getPartyMembersByPassengerType(PersonType personType){
		List<PartyMember> partyMembers = new ArrayList<PartyMember>();
		for (PartyMember partyMember : travelPartyMember) {
			if (partyMember.getPersonType() == personType) {
				partyMembers.add(partyMember);
			}
		}
		return partyMembers;
	}
	
	
	public OfferRequestFeedbackTypes validateTravelParty(){
		
		List<PartyMember> groupChildren = new ArrayList<PartyMember>();	
		List<PartyMember> groupNoBabies = new ArrayList<PartyMember>();
		List<PartyMember> groupAdult = new ArrayList<PartyMember>();
		
		for (PartyMember partyMember : travelPartyMember) {
			PersonType personType = partyMember.getPersonType();
			
			if (personType == PersonType.KID0 || personType == PersonType.KID4 || personType == PersonType.KID6) {
				groupChildren.add(partyMember);
			}else if(personType == PersonType.YOUTH || personType == PersonType.ADULT || personType == PersonType.SENIOR){
				groupAdult.add(partyMember);
			}	
			
			//if (personType != PersonType.KID0) {
				groupNoBabies.add(partyMember);
			//}
		}

		int groupChildrenCount = groupChildren.size();
		int groupNoBabiesCount = groupNoBabies.size();
		int groupAdultCount = groupAdult.size();
		
		if (travelPartyMember.size() == 0){
            return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeNoPartyMember;
        }
        if (groupNoBabiesCount > 6){
            return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeMoreThanSix;
        }
        if ((groupAdultCount * 4) < groupChildrenCount){
        	
            return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeInvalidCombination;
        }
        return OfferRequestFeedbackTypes.CORRECT;


	}
	
	public void rePartyMemberCount(PersonType personType){
		List<PartyMember> deletedPartyMemberTypes = new ArrayList<PartyMember>();
		//System.out.println("beforeChangePersonType==" + personType);
		for (PartyMember partyMember : travelPartyMember) {
			if (personType == partyMember.getPersonType()) {
				deletedPartyMemberTypes.add(partyMember);
			}
		}
		
		for (int i = 0; i < deletedPartyMemberTypes.size(); i++) {
			PartyMember partyMember = deletedPartyMemberTypes.get(i);
			partyMember.setPartyMemberCount(i + 1);
		}
	}
	
	public void sortPassenger(){
		Comparator<PartyMember> comp = new ComparatorPassengers();
	    Collections.sort(travelPartyMember, comp);
	}
	
	
	public void addPassengerByPersonTypeAndSort(int personType) {
		
		PartyMember partyMember = null;
		switch (personType) {
		case 0:
			partyMember = new PartyMember(PersonType.SENIOR);
			partyMember.setPartMemberSortorderField(4);
			break;
		case 1:
			partyMember = new PartyMember(PersonType.ADULT);
			partyMember.setPartMemberSortorderField(7);
			break;
		case 2:
			partyMember = new PartyMember(PersonType.YOUTH);
			partyMember.setPartMemberSortorderField(6);
			break;
		case 3:
			/*partyMember = new PartyMember(PersonType.KID15);
			partyMember.setPartMemberSortorderField(6);*/
			partyMember = new PartyMember(PersonType.KID12);
			partyMember.setPartMemberSortorderField(5);
			break;
		case 4:
			partyMember = new PartyMember(PersonType.KID6);
			partyMember.setPartMemberSortorderField(3);
			break;
		case 5:
			partyMember = new PartyMember(PersonType.KID4);
			partyMember.setPartMemberSortorderField(2);
			break;
		case 6:
			partyMember = new PartyMember(PersonType.KID0);
			partyMember.setPartMemberSortorderField(1);
			break;
		default:
			partyMember = new PartyMember(PersonType.ADULT);
			partyMember.setPartMemberSortorderField(7);
			break;
		}
		partyMember.setPartyMemberCount(getPassengerCountByType(partyMember.getPersonType()));
		travelPartyMember.add(partyMember);
		sortPassenger();
	}

	
	public String getCorporateCardNumberAndGpNumber(Context context){
		
		String gpNumberString = greenPointsNumber;
		if (gpNumberString == null) {
			gpNumberString = "";
		}
		String corporateCardsNumberString = "";
		String cardNumber = "";
		List<CorporateCard> corporateCards = listCorporateCards;
		
		if (corporateCards != null) {
				
			String cardType = "";
			for (int i = 0; i < corporateCards.size(); i++) {
				if (StringUtils.equalsIgnoreCase(CorporateCard.ICP, corporateCards.get(i).getCardType())) {
					cardType = context.getResources().getString(R.string.corporate_cards_view_icp_contract) + ":";
				}else {
					cardType = context.getResources().getString(R.string.corporate_cards_view_ecp_contract) + ":";
				}
				
				if (i == corporateCards.size() - 1) {
					
					cardNumber += cardType + corporateCards.get(i).getCardNumber();
				} else {
					cardNumber += cardType + corporateCards.get(i).getCardNumber() + ", ";
				}
			}
		}
		
		if (!StringUtils.equalsIgnoreCase("", gpNumberString)) {
			if (!StringUtils.equalsIgnoreCase("", cardNumber)) {
				gpNumberString = gpNumberString + ", ";
			}			
		}
		corporateCardsNumberString = gpNumberString + cardNumber;
		return corporateCardsNumberString;
	}
	
	
	public OfferRequestFeedbackTypes validateReductionCard(PartyMember partyMember){
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		
		for (ReductionCard reductionCard : partyMember.getReductionCards()) {
				
			if (map.containsKey(reductionCard.getType()) && reductionCard != null && (!StringUtils.isEmpty(reductionCard.getType()))) {
				return OfferRequestFeedbackTypes.OfferQueryFeedbackTypeReductionCardDup;
			}
			if (!StringUtils.isEmpty(reductionCard.getType()) && reductionCard != null) {
				map.put(reductionCard.getType(), reductionCard.getType());
			}							
		}
		
		return OfferRequestFeedbackTypes.CORRECT;
	}
	
	
	public List<PartyMember> getAllPartyMembersType(){
		List<PartyMember> partyMembers = new ArrayList<PartyMember>();
		PartyMember partyMember = null;
		
		partyMember = new PartyMember(PersonType.ADULT);
		partyMember.setPartMemberSortorderField(7);
		partyMembers.add(partyMember);

		partyMember = new PartyMember(PersonType.YOUTH);
		partyMember.setPartMemberSortorderField(6);
		partyMembers.add(partyMember);
		
		/*partyMember = new PartyMember(PersonType.KID15);
		partyMember.setPartMemberSortorderField(6);
		partyMembers.add(partyMember);*/
		
		partyMember = new PartyMember(PersonType.KID12);
		partyMember.setPartMemberSortorderField(5);
		partyMembers.add(partyMember);
		
		partyMember = new PartyMember(PersonType.SENIOR);
		partyMember.setPartMemberSortorderField(4);
		partyMembers.add(partyMember);
		
		partyMember = new PartyMember(PersonType.KID6);
		partyMember.setPartMemberSortorderField(3);
		partyMembers.add(partyMember);
		
		partyMember = new PartyMember(PersonType.KID4);
		partyMember.setPartMemberSortorderField(2);
		partyMembers.add(partyMember);
		
		partyMember = new PartyMember(PersonType.KID0);
		partyMember.setPartMemberSortorderField(1);
		partyMembers.add(partyMember);

		return partyMembers;
	}
	
	public String buildPassengersText(int position, Context context){
		String passengersText = "";
		if (travelPartyMember != null && travelPartyMember.size() > 0) {	
			PartyMember partyMember = travelPartyMember.get(position);
			passengersText = context.getString(R.string.general_passenger)  
					+ " " 
					+ String.valueOf(position + 1) 
					+ " - " 
					+ getPersonTypeName(partyMember.getPersonType(), context, 0);
					
			List<ReductionCard> reductionCards = partyMember.getReductionCards();	
			if (reductionCards != null && reductionCards.size() > 0) {
				String cardNumber = "";
				for (int i = 0; i < reductionCards.size(); i++) {

					if (i == reductionCards.size() - 1) {

						cardNumber += reductionCards.get(i).getCardNumber();
					} else {
						if (!StringUtils.isEmpty(reductionCards.get(i).getCardNumber())) {
							cardNumber += reductionCards.get(i).getCardNumber() + ", ";
						}
						
					}
				}
				if (!StringUtils.isEmpty(cardNumber)) {
					cardNumber = " - " + cardNumber;
				}
				passengersText = passengersText + cardNumber;
			}			
		}
		
		return passengersText ;
	}
	
	
	public int getPassengerCount(){
		int count = 0;
		for (int i = 0; i < travelPartyMember.size(); i++) {
			if (travelPartyMember.get(i).getPersonType() != PersonType.KID0) {
				count ++;
			}
		}
		return count;
	}
	public int getTicketLanguageSelectedItemInLastOfferQuery(){
		int ticketLanguageSelectedItem = 0;
		
		if (StringUtils.equalsIgnoreCase(ticketLanguage, LocaleChangedUtils.LANGUAGE_EN)) {
			ticketLanguageSelectedItem = 0;
		}else if (StringUtils.equalsIgnoreCase(ticketLanguage, LocaleChangedUtils.LANGUAGE_FR)) {
			ticketLanguageSelectedItem = 1;
		}else if (StringUtils.equalsIgnoreCase(ticketLanguage, LocaleChangedUtils.LANGUAGE_NL)) {
			ticketLanguageSelectedItem = 2;
		}
		
		return ticketLanguageSelectedItem;
	}
	/*public String getReductionCardTypeName(ReductionCardType reductionCardType, Context context) {	
		String name = null;
		if (reductionCardType != null) {
			if (reductionCardType.equals(ReductionCardType.NMBSFIFTYPERCENT)) {
				name = context.getResources().getStringArray(R.array.reduction_card_type)[0];
			}
			else {
				name = context.getResources().getStringArray(R.array.reduction_card_type)[1];
			}
		}
		return name;
	}*/

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUsedDate() {
		return lastUsedDate;
	}

	public void setLastUsedDate(Date lastUsedDate) {
		this.lastUsedDate = lastUsedDate;
	}
	
	/*public ReductionCard getReductionCard(PersonType personType){
		if (this.gettravelParty() != null && this.gettravelParty().size()>0) {
			for (int i = 0; i < this.gettravelParty().size(); i++) {
				if (this.gettravelParty().get(i).getPersonType().equals(personType)) {
					return this.gettravelParty().get(i).getReductionCards();
				}
			}
		}
		return null;
	}*/
	
	
}
