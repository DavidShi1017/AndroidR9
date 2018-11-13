package com.cflint.util;

import com.cflint.model.MyTicket;

import java.util.Comparator;
import java.util.Date;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorMyTicketDate implements Comparator<MyTicket> {
	public static final int ASC = 0;
	public static final int DESC = 1;
	private int sort;
	public ComparatorMyTicketDate(int sort){
		this.sort = sort;
	}
	public int compare(MyTicket ticket1, MyTicket myTicket2) {
		Date acceptTime1 = ticket1.getSortedDate();
		Date acceptTime2 = myTicket2.getSortedDate();
		if(sort == ASC){
			if(acceptTime1.after(acceptTime2)) {
				return 1;
			}
			return -1;
		}else{
			if(acceptTime1.before(acceptTime2)) {
				return 1;
			}
			return -1;
		}
	}
}
