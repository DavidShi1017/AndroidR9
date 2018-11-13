package com.cflint.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class BookingTimeOutError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public BookingTimeOutError()
    {  
		 super("request server BookingTimeOutError.");
    }   
	
	public BookingTimeOutError(final String s)
    {  
		 super(s);
    }   
}
