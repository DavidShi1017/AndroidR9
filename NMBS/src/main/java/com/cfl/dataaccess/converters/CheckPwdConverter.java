package com.cfl.dataaccess.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.model.LastUpdateTimestampPassword;
import com.cfl.model.LoginResponse;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckPwdConverter {

    public LastUpdateTimestampPassword parseResponse(String jsonResponse) throws Exception{
        LastUpdateTimestampPassword response = null;
        try {

            GsonBuilder builder = new GsonBuilder();
            registerTypeAdapterForDate(builder);
            //Type type = new TypeToken<ArrayList<UserMessage>>(){}.getType();
            Gson gson = builder.create();
            response = gson.fromJson(jsonResponse, LastUpdateTimestampPassword.class);
            //messageList = gson.fromJson(jsonObject.get("MobileUserMessages").toString(), type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return response;
    }

    private void registerTypeAdapterForDate(GsonBuilder builder){
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {

                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String date = (json.getAsJsonPrimitive().getAsString().replace(
                        "T", " "));
                if(date.indexOf("+") != -1){

                    date = date.substring(0, date.indexOf("+"));
                }
                if (date.indexOf("-") == -1) {
                    format = new SimpleDateFormat(
                            "HH:mm:ss");
                }
                //Log.e("date", "==" + date);
                try {
                    return format.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
