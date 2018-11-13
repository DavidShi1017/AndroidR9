package com.cflint.util;


import java.io.IOException;
import java.io.InputStream;


import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/*import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;*/
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;


import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.RequestFail;

import com.cflint.log.LogUtils;
import com.cflint.push.C2DMessaging;

/**
 * Call web service asynchronously.
 */

public class HTTPRestServiceCaller {

	private static final String TAG = HTTPRestServiceCaller.class.getSimpleName();
	
	public static final int HTTP_DELETE_METHOD = 0;
	public static final int HTTP_POST_METHOD = 1; // requestHttpMethod
	public static final int HTTP_GET_METHOD = 2; // requestHttpMethod
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String API_VERSION = "API-Version";
	private static final String API_KEY = "API-Key";
	private static final String ACCEPT_LANGUAGE = "Accept-Language";
	private static final String CONTENT_TYPE_VALUE = "application/json";
	//private static final String API_VERSION_VALUE = "0.3";
	private static final String API_KEY_VALUE = "5eda8268-206b-4d4a-aead-f24e080b31a8";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";

	private static final String API_CHANNEL= "API-Channel";

	/* NMBS */
	//private static final String API_CHANNEL_VALUE = "A8C2E148-5A4E-42BA-9C09-E0DABB1B863C";

	/* CFL */
	private static final String API_CHANNEL_VALUE = "AB9C7FD6-0F34-40CC-9B6A-C888E4903A6B";

	/*private static final String CONTENT_LENGTH = "Content-Length";
	private static String CONTENT_LENGTH_VALUE = "A8C2E148-5A4E-42BA-9C09-E0DABB1B863C";*/

	private static final String CONTENT_EXPECT = "Expect";
	private static final String CONTENT_EXPECT_VALUE = "100-continue";

	private Map<String, String> lastModified = new HashMap<String, String>();
	private String receiveLocation;
	private int statusCode ;
	//public static final String API_VERSION_VALUE_1 = "0.1";
	public static final String API_VERSION_VALUE_2 = "0.2";
	public static final String API_VERSION_VALUE_3 = "0.3";
	public static final String API_VERSION_VALUE_4 = "4.0";
	public static final String API_VERSION_VALUE_6 = "6.0";
	public static final String API_VERSION_VALUE_7 = "7.0";
	private HttpClient httpClient;

	public String executePushHttpRequest(Context context, String url, String postXmlData) throws RequestFail{
		HttpClient httpclient = getHttpClient(30000, url, context);
		HttpPost httpPost = new HttpPost(url);
		String httpResponseEntityStr = "";
		//Log.d(TAG, "serverUrl is: " + url + "------postJson is: " + postXmlData);
		//Log.d(TAG, "postXml is: " + postXmlData);
		try {
			StringEntity tmp = new StringEntity(postXmlData, HTTP.UTF_8);
			httpPost.setEntity(tmp);
			HttpResponse httpResponse;
			httpResponse = httpclient.execute(httpPost);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			//Log.d(TAG, "statusCode is: " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					httpResponseEntityStr = EntityUtils.toString(httpEntity);
					return httpResponseEntityStr;
				}
			} else {
				httpResponseEntityStr = "";
			}
		}catch (ConnectTimeoutException e) {
			throw new RequestFail();
		} catch (Exception e){
			e.printStackTrace();
			httpResponseEntityStr = "";
			throw new RequestFail();
		}
		return httpResponseEntityStr;
	}

	/**
	 * Connect to the third part server, send the registration id to the third.
	 * also send others.
	 * 
	 * @param context
	 * @param deviceType
	 *            0 means Android
	 * @param actionType
	 *            1 means reg, 0 means unreg
	 * @throws RequestFail
	 */
	public String executeHTTPRequest(Context context, int deviceType,
			int actionType) throws RequestFail {
		// Log.d(TAG, "Connecting the third part server starting..... ");
		String serverUrl;
		String httpResponseEntityStr = "false";
		String registrationId = C2DMessaging.getInstance(context)
				.getRegistrationId(context);
		serverUrl = context.getString(R.string.server_url_registration)
				+ "?DeviceToken=" + registrationId + "&DeviceType="
				+ deviceType + "&ActionType=" + actionType;

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(serverUrl);

		HttpResponse httpResponse;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("DeviceToken",
					registrationId));
			nameValuePairs.add(new BasicNameValuePair("DeviceType", String
					.valueOf(deviceType)));
			nameValuePairs.add(new BasicNameValuePair("ActionType", String
					.valueOf(actionType)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpResponse = httpclient.execute(httppost);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// Obtains the message entity of this response
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					httpResponseEntityStr = EntityUtils.toString(httpEntity);
					// Log.d(TAG, "Pass ," + " HttpResponse = " +
					// httpResponseEntityStr);
				}
			} else {
				// Log.d(TAG, "Error " + "statusCode= "+statusCode+ " , URL " +
				// serverUrl);
			}

		} catch (ClientProtocolException e) {
			httpResponseEntityStr = "false";
		} catch (IOException e) {
			httpResponseEntityStr = "false";
		}
		if (!"true".equals(httpResponseEntityStr)) {
			throw new RequestFail();
		} else {
			// Log.d(TAG, "Connecting the third part server ending..... ");
			return httpResponseEntityStr;
		}

	}

	/**
	 * Connect to the server for getting the response. StatusCode = 201, means
	 * creating account or creating offer GUID successfully .
	 * 
	 * @param context
	 * @param
	 * @param acceptLanguage
	 * @return String
	 * @throws RequestFail
	 * @throws ConnectionError
	 * @throws ConnectTimeoutException
	 * @throws IOException
	 * @throws ParseException
	 * @throws IOException
	 */

	public String executeHTTPRequest(Context context, String postJson,
			String serverUrl, String acceptLanguage, int httpMethodFlag,
			int timeOut, boolean isMastdataWorking,
			String lastModifiedInPreferences, String serviceApiVersion) throws RequestFail,
			ConnectionError, BookingTimeOutError, ConnectTimeoutException {
		if (httpClient == null) {
			httpClient = this.getHttpClient(timeOut, serverUrl, context);
		}
		
		
		
		
		HttpResponse response = null;

		//Log.d(TAG, "serverUrl is: " + serverUrl + "------postJson is: " + postJson);
		//Log.d(TAG, "postJson is: " + postJson);
		// Log.d(TAG ,"isMastdataWorking: " + isMastdataWorking );
		 //Log.d(acceptLanguage ,"acceptLanguage: "+ acceptLanguage);

		try {
			if (httpMethodFlag == HTTP_POST_METHOD) {
				StringEntity tmp = new StringEntity(postJson, HTTP.UTF_8);
				//Log.d(TAG ,"http flag: httpPost" );
				HttpPost httpPost = new HttpPost();
				httpPost = (HttpPost) this.getHttpUriRequest(HTTP_POST_METHOD,
						serverUrl, acceptLanguage, lastModifiedInPreferences,
						isMastdataWorking, serviceApiVersion, postJson);
				httpPost.setEntity(tmp);
				response = httpClient.execute(httpPost);

				//Log.d(TAG ,"http flag: httpPost..." + response);
			} else if (httpMethodFlag == HTTP_GET_METHOD) {
				//Log.d(TAG ,"http flag: HttpGet" );
				HttpGet httpGet = new HttpGet();
				httpGet = (HttpGet) this.getHttpUriRequest(HTTP_GET_METHOD,
						serverUrl, acceptLanguage, lastModifiedInPreferences,
						isMastdataWorking, serviceApiVersion, postJson);
				response = httpClient.execute(httpGet);
			} else {
				HttpDelete httpDelete;
				httpDelete = (HttpDelete) this.getHttpUriRequest(
						HTTP_DELETE_METHOD, serverUrl, acceptLanguage,
						lastModifiedInPreferences, isMastdataWorking, serviceApiVersion, postJson);
				response = httpClient.execute(httpDelete);
			}
		} catch (ConnectTimeoutException e) {
			e.getStackTrace();
			httpClientShutdown(isMastdataWorking);
			throw new ConnectTimeoutException();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			httpClientShutdown(isMastdataWorking);
			throw new RequestFail();
		} catch (Exception e) {
			
			e.printStackTrace();
			httpClientShutdown(isMastdataWorking);
			throw new RequestFail();
		}
		LogUtils.e(TAG, "url is::::: " + serverUrl +"........response http code is: " + response.getStatusLine().getStatusCode() + "....acceptLanguage...." + acceptLanguage);
		//Log.d(TAG, "url is::::: " + serverUrl +"........response http code is: " + response.getStatusLine().getStatusCode());
		getLastHeaderLocation(response);
		getLastHeaderLastModified(isMastdataWorking, response, serverUrl);
		String responseString = getHttpResponse(response, serverUrl, isMastdataWorking);
		return responseString;
	}


	/**
	 * Get the http HttpResponse
	 * 
	 * @param response
	 * @return
	 * @throws RequestFail
	 * @throws ConnectionError
	 * @throws BookingTimeOutError
	 */
	private String getHttpResponse(HttpResponse response, String url, boolean isMastdataWorking) throws RequestFail,
			ConnectionError, BookingTimeOutError {
		
		String responseString = null;
		if (response != null) {
			
			// Log.d(TAG ,"ResponseStatusCode: "+ response.getStatusLine().getStatusCode());
			// Log.d(TAG ,"Content-Encoding: "+ response.getFirstHeader("Content-Encoding"));
			try {
				if (response.getFirstHeader("Content-Encoding") != null) {
					responseString = EntityUtils.toString(new GzipDecompressingEntity(response.getEntity()));
				} else {
					if (response.getEntity() != null) {
						responseString = EntityUtils.toString(response.getEntity());
					}
					
				}

			} catch (Exception e) {
				//httpClientShutdown(isMastdataWorking);
				//Log.d(TAG, "Exception:::url::: " + url);
				e.printStackTrace();
				throw new RequestFail();
			}
			statusCode = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 
					|| response.getStatusLine().getStatusCode() == 304) {
							
			}else {	
				if (404 == response.getStatusLine().getStatusCode()) {
					throw new ConnectionError();
				}
				//Log.d(TAG, "Shutdown:::url::: " + url);
				httpClientShutdown(isMastdataWorking);					
			}

			if (404 == response.getStatusLine().getStatusCode()) {
				throw new ConnectionError();
			} else if (400 == response.getStatusLine().getStatusCode()) {
				throw new RequestFail();
			} else if (422 == response.getStatusLine().getStatusCode()) {
				throw new BookingTimeOutError();
			} /*
			 * else if (510 == response.getStatusLine().getStatusCode()) {
			 * Log.d(TAG ,"Response in 510::::::"+ responseString); // Log.d(TAG
			 * ,"Response: "+ responseString); if
			 * (!StringUtils.contains(responseString, "DBooking_343")) { throw
			 * new RequestFail(); }
			 * 
			 * }
			 */else if (304 != response.getStatusLine().getStatusCode()) {

			}
		}
		
		if (304 == response.getStatusLine().getStatusCode()) {
			responseString = "304";
		}
		LogUtils.e(TAG, "url=====" + url + "-------------->urlResponse: " + responseString);

		return responseString;
	}
	
	
	private void httpClientShutdown(boolean isMastdataWorking){
		if (httpClient != null && httpClient.getConnectionManager() != null && isMastdataWorking) {
			//Log.d(TAG, "shutdown:::: ");
			//httpClient.getConnectionManager().shutdown();
		}		
	}

	/**
	 * Get the receive location in header.
	 * 
	 * @param response
	 */
	private void getLastHeaderLocation(HttpResponse response) {

		if (response.getLastHeader("LOCATION") != null) {
			receiveLocation = response.getLastHeader("LOCATION").toString();
		}
	}

	/**
	 * Get the Last-Modified in header.
	 * 
	 * @param isMastdataWorking
	 * @param response
	 */
	private void getLastHeaderLastModified(boolean isMastdataWorking, HttpResponse response, String url) {
		if (isMastdataWorking) {

			if (response.getStatusLine().getStatusCode() == HttpStatusCodes.SC_OK
					|| response.getStatusLine().getStatusCode() == HttpStatusCodes.SC_CREATED) {
				
					
				//Log.d(TAG, "url:::: " + url + "::::lastModified::::" + response.getLastHeader("Last-Modified").toString());
				String lastModified = response.getLastHeader("Last-Modified").toString();
				this.lastModified.put(url, lastModified);

			}
		}
	}
	
/*	private void getLastHeaderLastModified(GetMethod get) {

		if (get.getResponseHeader("Last-Modified") != null) {
			lastModified = get.getResponseHeader("Last-Modified").toString();

		}
	}*/

/*	private void getLastHeaderLastModified(GetMethod get) {

		if (get.getResponseHeader("Last-Modified") != null) {
			lastModified = get.getResponseHeader("Last-Modified").toString();

		}
	}*/

	public String getReceiveLocation() {
		if (receiveLocation != null) {
			int start = receiveLocation.indexOf(":");
			receiveLocation = receiveLocation.substring(start + 2);
		}
		return receiveLocation;
	}

	/**
	 * Get the HttpClient, Set Timeout is 30000
	 * 
	 * @return HttpClient
	 */
	public HttpClient getHttpClient(int timeOut, String serverUrl, Context context) {

		//HttpClient httpClient = getNewHttpClient(timeOut, context);
		HttpClient httpClient = getNewHttpClient(timeOut);
		return httpClient;
	}

	/**
	 * Get HttpUriRequest, it will be HttpPost or HttpGet by different
	 * requestHttpMethod.
	 * 
	 * @param requestHttpMethod
	 *            POST_TYPE or GET_TYPE
	 * @param url
	 * @param acceptLanguage
	 * @return HttpPost or HttpGet
	 */
	public Object getHttpUriRequest(int requestHttpMethod, String url,
			String acceptLanguage, String lastModified,
			boolean isMastdataWorking, String serviceApiVersion, String postJson) {
		if (url != null) {
			url = url.replaceAll(" ", "%20");
		}
		//Log.e("acceptLanguage" ,"acceptLanguage: "+ acceptLanguage);
		if(acceptLanguage == null || acceptLanguage.isEmpty()){
			acceptLanguage = NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey();
		}
		//Log.e("acceptLanguage" ,"acceptLanguage: "+ acceptLanguage);
		if (requestHttpMethod == HTTP_POST_METHOD) {// POST_TYPE
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
			httpPost.setHeader(API_VERSION, serviceApiVersion);
			httpPost.setHeader(API_KEY, API_KEY_VALUE);
			httpPost.setHeader(ACCEPT_LANGUAGE, acceptLanguage);
			httpPost.setHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
			httpPost.setHeader(API_CHANNEL, API_CHANNEL_VALUE);
			if (isMastdataWorking && !StringUtils.isEmpty(lastModified)) {
				httpPost.setHeader(IF_MODIFIED_SINCE, lastModified);
			}
			if(serviceApiVersion.equalsIgnoreCase(API_VERSION_VALUE_7)){
				if(postJson != null)
					//CONTENT_LENGTH_VALUE = String.valueOf(postJson.length());
				//httpPost.setHeader(CONTENT_LENGTH, CONTENT_LENGTH_VALUE);
				httpPost.setHeader(CONTENT_EXPECT, CONTENT_EXPECT_VALUE);
			}
			
			return httpPost;
		} else if (requestHttpMethod == HTTP_GET_METHOD) {// GET_TYPE

			// Log.d(TAG ,"GET_TYPE: ");
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
			httpGet.setHeader(API_VERSION, serviceApiVersion);
			httpGet.setHeader(API_KEY, API_KEY_VALUE);
			httpGet.setHeader(ACCEPT_LANGUAGE, acceptLanguage);
			httpGet.setHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
			httpGet.setHeader(API_CHANNEL, API_CHANNEL_VALUE);
			if (isMastdataWorking && !StringUtils.isEmpty(lastModified)) {
				// Log.d(TAG ,"Last-Modified: "+ lastModified);
				httpGet.setHeader(IF_MODIFIED_SINCE, lastModified);
			}
			//System.out.println("lastModified=====" + lastModified);
			return httpGet;
		} else {

			HttpDelete httpDelete = new HttpDelete(url);
			httpDelete.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
			httpDelete.setHeader(API_VERSION, serviceApiVersion);
			httpDelete.setHeader(API_KEY, API_KEY_VALUE);
			httpDelete.setHeader(ACCEPT_LANGUAGE, acceptLanguage);
			httpDelete.setHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
			return httpDelete;
		}
	}

	public static HttpClient getNewHttpClient(int timeOut, Context context) {
		try {
			/*KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);

			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);*/

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", newSslSocketFactory(context), 443));
			HttpConnectionParams.setConnectionTimeout(params, timeOut);
			HttpConnectionParams.setSoTimeout(params, timeOut);
			ConnManagerParams.setMaxTotalConnections(params, 5);
			ConnPerRouteBean connPerRoute = new ConnPerRouteBean(5);  
		    ConnManagerParams.setMaxConnectionsPerRoute(params,connPerRoute);  
			//PoolingClientConnectionManager  ccm = new PoolingClientConnectionManager(registry);
			ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params, registry);
			//ccm.setMaxTotal(20);
			//ccm.setDefaultMaxPerRoute(20);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	private static HttpClient getNewHttpClient(int timeOut) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);

			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			HttpConnectionParams.setConnectionTimeout(params, timeOut);
			HttpConnectionParams.setSoTimeout(params, timeOut);
			ConnManagerParams.setMaxTotalConnections(params, 5);
			ConnPerRouteBean connPerRoute = new ConnPerRouteBean(5);  
		    ConnManagerParams.setMaxConnectionsPerRoute(params,connPerRoute);  
			//PoolingClientConnectionManager  ccm = new PoolingClientConnectionManager(registry);
			ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params, registry);
			//ccm.setMaxTotal(20);
			//ccm.setDefaultMaxPerRoute(20);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	private static SSLSocketFactory newSslSocketFactory(Context context) {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            //InputStream in = context.getAssets().open("nmbs.bks"); //name of your keystore file here
            /*InputStream in = context.getResources().openRawResource(R.raw.uat2);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Provide the password of the keystore
                trusted.load(in, "Delaware2014".toCharArray());
            } finally {
                in.close();
            }*/
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER); // This can be changed to less stricter verifiers, according to need
            //rtint.railtourdev.be

        	return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
	public Map<String, String> getLastModified() {
		return lastModified;
	}

	public int getStatusCode(){
		return this.statusCode;
	}
	
}
