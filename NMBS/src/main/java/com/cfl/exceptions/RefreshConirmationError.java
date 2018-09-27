package com.cfl.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class RefreshConirmationError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RefreshConirmationError()
    {  
		 super("request server BookingTimeOutError.");
    }   
	
	public RefreshConirmationError(final String s)
    {  
		 super(s);
    }   
}
