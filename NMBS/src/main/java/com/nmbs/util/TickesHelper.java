package com.nmbs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nmbs.model.Order;

public class TickesHelper {

	
	private String dnr;
	private boolean isOpen;
	private List<Order> ordersOfDnr = new ArrayList<Order>();
	private int orderState;
	private Date firstTravelSegmentDate;
	
	public String getDnr() {
		return dnr;
	}

	public void setDnr(String dnr) {
		this.dnr = dnr;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public List<Order> getOrdersOfDnr() {
		return ordersOfDnr;
	}

	public int getOrderState() {
		return orderState;
	}

	public void setOrderState(int orderState) {
		this.orderState = orderState;
	}

	public void setOrdersOfDnr(List<Order> ordersOfDnr) {
		this.ordersOfDnr = ordersOfDnr;
	}



	public Date getFirstTravelSegmentDate() {
		return firstTravelSegmentDate;
	}

	public TickesHelper(String dnr, boolean isOpen, List<Order> ordersOfDnr, int orderState, Date firstTravelSegmentDate) {
		this.dnr = dnr;
		this.isOpen = isOpen;
		this.ordersOfDnr = ordersOfDnr;
		this.orderState = orderState;
		this.firstTravelSegmentDate = firstTravelSegmentDate;
	}
	
}
