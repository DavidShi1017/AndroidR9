package com.cfl.util;

import java.util.Comparator;

import com.cfl.model.Order;






/**
 * Order Schedule 
 * @author David
 *
 */

public class ComparatorDirectionWithHasDepartureTime implements Comparator<Order>{
	

	public int compare(Order order1,Order order2) {       
		
		if (order1.isHasDepartureTime()) {
			
			return order1.getDepartureDate().compareTo(order2.getDepartureDate());
		}
		return 0;		
    }

}
