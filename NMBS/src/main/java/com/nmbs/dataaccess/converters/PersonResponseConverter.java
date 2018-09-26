package com.nmbs.dataaccess.converters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.model.DebugMessage;
import com.nmbs.model.Message;
import com.nmbs.model.Person;

/**
 * Parse JsonString to Object. 
 *
 */
public class PersonResponseConverter {
	
	/**
	 *  Parse JsonString to Person object
	 * @param jsonString
	 * @param isOneway
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public Person parsePerson(String jsonString)
			throws JSONException, InvalidJsonError {
		Person person = null;
		if(jsonString == null || "".equals(jsonString)){
			return new Person(-1, -1, null, null, null);
		}
		
			JSONObject jsonObject = new JSONObject(jsonString);
			String personId = jsonObject.getString("PersonId");
			String customerId = jsonObject.getString("CustomerId");
			JSONArray messageArray = null;
			messageArray = jsonObject.getJSONArray("Messages");
			JSONObject messageObject;
			List<Message> listMessages = new ArrayList<Message>();
			List<DebugMessage> listDebugMessages = new ArrayList<DebugMessage>();
			Message message;
			DebugMessage debugMessage;
			for (int i = 0; i < messageArray.length(); i++) {
				messageObject = messageArray.getJSONObject(i);

				String description = messageObject.getString("Description");
				String severity = messageObject.getString("Severity");
				String statusCode = messageObject.getString("StatusCode");

				message = new Message(description, severity, statusCode);
				listMessages.add(message);
			}

			JSONArray debugMessageArray = null;
			debugMessageArray = jsonObject.getJSONArray("DebugMessages");
			JSONObject debugMessageObject;

			for (int i = 0; i < debugMessageArray.length(); i++) {
				debugMessageObject = debugMessageArray.getJSONObject(i);

				String origin = debugMessageObject.getString("Origin");
				String type = debugMessageObject.getString("Type");
				String value = debugMessageObject.getString("Value");

				debugMessage = new DebugMessage(origin, type, value);
				listDebugMessages.add(debugMessage);
			}

			person = new Person(Integer.parseInt(personId), Integer
					.parseInt(customerId), null, listMessages,
					listDebugMessages);

		
			return person;

	}
		
}