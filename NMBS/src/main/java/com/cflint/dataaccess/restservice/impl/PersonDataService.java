package com.cflint.dataaccess.restservice.impl;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.cflint.R;
import com.cflint.dataaccess.converters.PersonResponseConverter;
import com.cflint.dataaccess.restservice.IPersonDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.exceptions.NoTicket;
import com.cflint.model.Account;
import com.cflint.model.Person;
import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.ObjectToJsonUtils;

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
