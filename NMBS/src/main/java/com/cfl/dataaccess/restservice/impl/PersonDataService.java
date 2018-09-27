package com.cfl.dataaccess.restservice.impl;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.cfl.R;
import com.cfl.dataaccess.converters.PersonResponseConverter;
import com.cfl.dataaccess.restservice.IPersonDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.exceptions.NoTicket;
import com.cfl.model.Account;
import com.cfl.model.Person;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.ObjectToJsonUtils;

/**
 * Call web service get Person Response.
 */
public class PersonDataService implements IPersonDataService{

	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	PersonResponseConverter personResponseConverter = new PersonResponseConverter();
	
	/**
	 * Call web service asynchronously and get Json file
	 * @param account
	 * @param context
	 * @return Person
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws NoTicket
	 * @throws TimeOutError
	 * @throws RequestFail
	 * @throws ParseException
	 * @throws IOException
	 * @throws ConnectionError 
	 */
	public Person executeRegister(Account account, Context context)throws InvalidJsonError,JSONException, 
	TimeOutError, RequestFail, ParseException, IOException, ConnectionError, BookingTimeOutError{
		
		String httpResponseString = null;
		Person personResponse = null;
		try {
			String postJsonString = ObjectToJsonUtils.getPostAccountStr(account);
			String urlString = context.getString(R.string.server_url_create_account);
			
			httpResponseString = httpRestServiceCaller.executeHTTPRequest(context.getApplicationContext(), postJsonString
					, urlString ,"en", HTTPRestServiceCaller.HTTP_POST_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		} catch (RequestFail e) {
			Log.d("executeRegister", "RequestFail", e);
			e.printStackTrace();
		}		
		if (httpResponseString == null || "".equals(httpResponseString)) {
			throw new RequestFail();
		}		
		personResponse = personResponseConverter.parsePerson(httpResponseString);	
		return personResponse;
	}
	
	/**
	 * Call web service asynchronously and get Person response.
	 * @param account
	 * @param context
	 * @return Person
	 * @throws TimeOutError
	 */
	public Person executeLogin(Account account,Context context) throws TimeOutError{
		
		//String StringHttpResponse = null;
		Person person = new Person(1, 2, new Account(),null,null);
		
		return person;		
		
	}
}
