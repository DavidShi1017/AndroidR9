package com.cflint.exceptions;

public class DonotContainTicket extends Exception {


	private static final long serialVersionUID = 1L;

	public DonotContainTicket()
    {  
		 super("Can't upload DNR cacaues the dossier doesn't contain a ticket.");
    }   
	
	public DonotContainTicket(final String s)
    {  
		 super(s);
    }  
}
