package com.cfl.exceptions;
/**
 *request server timeout exception
 *@author Alice
 */
public class TimeOutError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TimeOutError()
    {  
		 super("request server timeout.");
    }   
	
	public TimeOutError(final String s)
    {  
		 super(s);
    }   
}
