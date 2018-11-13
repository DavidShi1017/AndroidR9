package com.cflint.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class DBookingInvalidFtpError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBookingInvalidFtpError()
    {  
		 super("request server timeout.");
    }   
	
	public DBookingInvalidFtpError(final String s)
    {  
		 super(s);
    }   
}
