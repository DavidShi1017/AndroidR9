package com.nmbs.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class DBookingCATError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBookingCATError()
    {  
		 super("request server timeout.");
    }   
	
	public DBookingCATError(final String s)
    {  
		 super(s);
    }   
}
