package com.nmbs.dataaccess.restservice.impl;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.nmbs.R;
import com.nmbs.dataaccess.converters.PersonResponseConverter;
import com.nmbs.dataaccess.restservice.IPersonDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.log.LogUtils;
import com.nmbs.model.Account;
import com.nmbs.model.Person;
import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.ObjectToJsonUtils;

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
			LogUtils.d("executeRegister", "RequestFail", e);
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
