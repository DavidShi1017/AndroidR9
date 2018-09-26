package com.nmbs.dataaccess.restservice;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.model.Account;
import com.nmbs.model.Person;

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
