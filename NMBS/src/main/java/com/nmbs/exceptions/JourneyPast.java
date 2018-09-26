package com.nmbs.exceptions;

public class JourneyPast extends Exception {
	private static final long serialVersionUID = 1L;

	public JourneyPast()
    {  
		 super("The ticket won't be visible because the journey is in the past");
    }   
	
	public JourneyPast(final String s)
    {  
		 super(s);
    }  
}
