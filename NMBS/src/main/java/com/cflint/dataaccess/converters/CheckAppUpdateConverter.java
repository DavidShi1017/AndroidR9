package com.cflint.dataaccess.converters;

import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.model.CheckAppUpdate;

public class CheckAppUpdateConverter {

	public CheckAppUpdate parse(String jsonString) throws JSONException, InvalidJsonError{
		

		GsonBuilder gsonBuilder = new GsonBuilder();
		
	    Gson gson = gsonBuilder.create();

		
	    CheckAppUpdate checkAppUpdate = gson.fromJson(jsonString, CheckAppUpdate.class);

			
		if (checkAppUpdate == null) {
				throw new InvalidJsonError();
		}
		return checkAppUpdate;	
	}
}
