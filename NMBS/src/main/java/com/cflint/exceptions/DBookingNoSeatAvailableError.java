package com.cflint.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class DBookingNoSeatAvailableError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBookingNoSeatAvailableError()
    {  
		 super("request server timeout.");
    }   
	
	public DBookingNoSeatAvailableError(final String s)
    {  
		 super(s);
    }   
}
