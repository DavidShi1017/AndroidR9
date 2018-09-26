package com.nmbs.exceptions;
/**
 * No network exception.
 *@author Alice
 */
public class ConnectionError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConnectionError()
    {  
		 super("request server timeout.");
    }   
	
	public ConnectionError(final String s)
    {  
		 super(s);
    }   
}
