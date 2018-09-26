package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import com.nmbs.util.ComparatorOfferQueryDate;

public class AllLastOfferQuery implements Serializable{


	private static final long serialVersionUID = 1L;
	public static final int MAX_FAVORITE_OFFER_QUERIES_SIZE = 20;
	public static final int MAX_LAST_OFFER_QUERIES_SIZE = 10;
	
	@SerializedName("LastOfferQueries")	
	private List<OfferQuery> lastOfferQueries = new ArrayList<OfferQuery>();
	
	private List<OfferQuery> previousOfferQueries;
	
	private List<OfferQuery> favoriteOfferQueries;

	public List<OfferQuery> getLastOfferQueries() {
		return lastOfferQueries;
	}

	public void setLastOfferQueries(List<OfferQuery> lastOfferQueries) {
		this.lastOfferQueries = lastOfferQueries;
		setPreviousOfferQueries();
		setFavoriteOfferQueries();
	}
	
	public void addLastOfferQueries(OfferQuery OfferQuery){
		lastOfferQueries.add(OfferQuery);
	}
	
	public void deleteFavoriteOfferQueries(OfferQuery offerQuery){
		favoriteOfferQueries.remove(offerQuery);
	}
	
	public void deletePreviousOfferQueries(OfferQuery offerQuery){
		previousOfferQueries.remove(offerQuery);
	}
	
	public void deleteAllOfferQueries(boolean isFavorite){
		
		if (isFavorite) {
			deleteFavoriteOfferQueries();
		}else {
			deletePreviousOfferQueries();
		}
	}
	
	private void deletePreviousOfferQueries(){
		previousOfferQueries.clear();
	}
	
	private void deleteFavoriteOfferQueries(){
		favoriteOfferQueries.clear();
	}
	
	public void setPreviousOfferQueries() {
		previousOfferQueries = new ArrayList<OfferQuery>();
		for (OfferQuery offerQuery : lastOfferQueries) {
			if (offerQuery != null && !offerQuery.isFavorite()) {
				previousOfferQueries.add(offerQuery);
			}
		}
	}

	public void setFavoriteOfferQueries() {
		favoriteOfferQueries = new ArrayList<OfferQuery>();
		for (OfferQuery offerQuery : lastOfferQueries) {
			if (offerQuery != null && offerQuery.isFavorite()) {
				favoriteOfferQueries.add(offerQuery);
			}
		}
	}

	public List<OfferQuery> getPreviousOfferQueries(){
		
		Comparator<OfferQuery> comp = new ComparatorOfferQueryDate();
		
        Collections.sort(previousOfferQueries, comp); 	 
		return previousOfferQueries;
	} 
	
	public List<OfferQuery> getFavoriteOfferQueries(){
		
		Comparator<OfferQuery> comp = new ComparatorOfferQueryDate();
		
        Collections.sort(favoriteOfferQueries, comp); 
		return favoriteOfferQueries;
	}
	
	 public void mergeOfferQueries(){		 
		 lastOfferQueries.clear();
		 lastOfferQueries.addAll(favoriteOfferQueries);
		 lastOfferQueries.addAll(previousOfferQueries);
	 }

}
