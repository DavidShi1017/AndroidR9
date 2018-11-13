package com.cflint.exceptions;
/**
 *Parse Json Exception
 *@author Alice
 */
public class InvalidJsonError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidJsonError()
    {  
		 super("Parse Json Fail.");
    }   
	
	public InvalidJsonError(final String s)
    {  
		 super(s);
    }   
}
