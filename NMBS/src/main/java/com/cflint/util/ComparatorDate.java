package com.cflint.util;

import java.util.Comparator;

import com.cflint.model.Order;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorDate implements Comparator<Order> {

	private boolean isFuture;

	public ComparatorDate(boolean isFuture) {
		this.isFuture = isFuture;
	}

	public int compare(Order order1, Order order2) {

		if (isFuture) {
			return order1.getSortDepartureDate().compareTo(
					order2.getSortDepartureDate());
		} else {
			return order2.getSortDepartureDate().compareTo(
					order1.getSortDepartureDate());
		}

	}

}
