package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyDataState implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<CollectionItem> listCollectionItems;
	private List<CollectionItem> titles;
	private List<CollectionItem> paymentOptions;
	private List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
	public List<CollectionItem> getListCollectionItems() {
		
		return listCollectionItems;
	}
	public void setListCollectionItems(List<CollectionItem> listCollectionItems) {
		this.listCollectionItems = listCollectionItems;
	}
	public List<CollectionItem> getTitles() {
		return titles;
	}
	public void setTitles(List<CollectionItem> titles) {
		this.titles = titles;
	}
	public List<CollectionItem> getPaymentOptions() {
		return paymentOptions;
	}
	public void setPaymentOptions(List<CollectionItem> paymentOptions) {
		this.paymentOptions = paymentOptions;
	}
	public List<DeliveryMethod> getDeliveryMethods() {
		return deliveryMethods;
	}
	public void setDeliveryMethods(List<DeliveryMethod> deliveryMethods) {
		this.deliveryMethods = deliveryMethods;
	}
	
	
}
