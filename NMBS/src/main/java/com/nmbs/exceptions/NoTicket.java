package com.nmbs.exceptions;
/**
 *When Ticket is null,Throw the exception
 *@author Alice
 */
public class NoTicket extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NoTicket()
    {  
		 super("NoTicket.");
    }   
	
	public NoTicket(final String s)
    {  
		 super(s);
    }   
}
