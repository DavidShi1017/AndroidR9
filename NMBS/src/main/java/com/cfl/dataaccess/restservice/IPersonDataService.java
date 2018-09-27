package com.cfl.dataaccess.restservice;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.exceptions.NoTicket;
import com.cfl.model.Account;
import com.cfl.model.Person;

/**
 * Call web service get Person Response.
 */
public interface IPersonDataService{

	/**
	 * Call web service asynchronously and get Json file
	 * @param account
	 * @param context
	 * @return
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws NoTicket
	 * @throws TimeOutError
	 * @throws RequestFail
	 * @throws ParseException
	 * @throws IOException
	 */
	public Person executeRegister(Account account, Context context) 
	throws InvalidJsonError,JSONException, TimeOutError, RequestFail, ParseException, IOException, ConnectionError, BookingTimeOutError;
	
	/**
	 * Call web service asynchronously and get Person response.
	 * @param account
	 * @param context
	 * @return Person
	 * @throws TimeOutError
	 */
	public Person executeLogin(Account account,Context context) throws TimeOutError;
	
}
