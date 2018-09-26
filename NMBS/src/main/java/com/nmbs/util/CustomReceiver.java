package com.nmbs.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class CustomReceiver extends BroadcastReceiver {

	private static final String CAMPAIGN_SOURCE_PARAM = "utm_source";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.d("debug", "waking receiver");
	    Log.d("debug", "waking receiver" + intent.getAction());
	    Log.d("debug", "waking receiver" + intent.getDataString());
	    Log.d("debug", "waking receiver" + intent.getStringExtra("referrer"));
	    Uri uri = intent.getData();
		/*
	    MapBuilder.createAppView().setAll(getReferrerMapFromUri(uri));
	    new CampaignTrackingReceiver().onReceive(context, intent);
	    */
	}/*
	private Map<String,String> getReferrerMapFromUri(Uri uri) {

	    MapBuilder paramMap = new MapBuilder();

	    // If no URI, return an empty Map.
	    if (uri == null) {
	        Log.d("debug", "intent null");

	        return paramMap.build();
	    }

	    Log.d("debug", "waking receiver" + uri.getQueryParameter(CAMPAIGN_SOURCE_PARAM));
	    if (uri.getQueryParameter(CAMPAIGN_SOURCE_PARAM) != null) {

	        // MapBuilder.setCampaignParamsFromUrl parses Google Analytics
	        // campaign
	        // ("UTM") parameters from a string URL into a Map that can be set
	        // on
	        // the Tracker.
	        paramMap.setCampaignParamsFromUrl(uri.toString());

	        Log.d("debug", paramMap.get(Fields.CAMPAIGN_SOURCE));

	        // If no source parameter, set authority to source and medium to
	        // "referral".
	    } else if (uri.getAuthority() != null) {

	        paramMap.set(Fields.CAMPAIGN_MEDIUM, "referral");
	        paramMap.set(Fields.CAMPAIGN_SOURCE, uri.getAuthority());

	    }
	    return paramMap.build();
	}*/
}
