package com.nmbs.util;

import java.util.Comparator;



import com.nmbs.model.Order;
import com.nmbs.model.SeatLocationForOD.Direction;





/**
 * Order Schedule 
 * @author David
 *
 */

public class ComparatorDirection implements Comparator<Order>{
	
	private boolean isFuture;
	
	public ComparatorDirection(boolean isFuture){
		
		this.isFuture = isFuture;
	}

	public int compare(Order order1,Order order2) {       
		
		if ((order1.getDirection() == Direction.Outward || order1.getDirection() == Direction.Return)
				
				&& (order2.getDirection() == Direction.Outward || order2.getDirection() == Direction.Return ))
				
				 {
			if (isFuture) {
				return order1.getDirection().compareTo(order2.getDirection());
			}else {
				return order2.getDirection().compareTo(order1.getDirection());
			}
			
		}else {
			return 0;
		}       
    }

}
