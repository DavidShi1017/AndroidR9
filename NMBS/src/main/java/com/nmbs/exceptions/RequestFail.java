package com.nmbs.exceptions;
/**
 *request server fail
 *@author Alice
 */
public class RequestFail extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RequestFail()
    {  
		 super("request server fail.");
    }   
	
	public RequestFail(final String s)
    {  
		 super(s);
    }   
}
