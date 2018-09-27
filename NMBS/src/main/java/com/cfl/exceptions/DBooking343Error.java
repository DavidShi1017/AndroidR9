package com.cfl.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class DBooking343Error extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBooking343Error()
    {  
		 super("request server timeout.");
    }   
	
	public DBooking343Error(final String s)
    {  
		 super(s);
    }   
}
