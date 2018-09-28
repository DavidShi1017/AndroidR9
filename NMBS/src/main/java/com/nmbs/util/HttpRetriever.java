package com.nmbs.util;


import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Retrieve InputStream data by URL.
 *  
 * @author @author DAVID 
 */
public class HttpRetriever {

	private static HttpRetriever instance;
	//private final static String TAG = HttpRetriever.class.getSimpleName();
	public static HttpRetriever getInstance() {
		if (instance == null) {
			instance = new HttpRetriever();

		}
		return instance;
	}

	private HttpRetriever() {
		
	}
	
	public InputStream retrieveStream(String stringURL) throws Exception {
		if (stringURL == null || "".equals(stringURL)) {
			return null;
		}
		InputStream inputStream = null;

			URL url = new URL(stringURL);
			if (url.getProtocol().toLowerCase().equals("https")) {
				Log.e("HttpRetriever", "https...");
				//HttpsURLConnection urlConnection = GetHttps(stringURL);
				inputStream = GetHttps(stringURL);
				//inputStream = urlConnection.getInputStream();
				Log.e("HttpRetriever", "inputStream..." + inputStream);
			}else{
				URLConnection urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(20000);
				inputStream = urlConnection.getInputStream();
			}

/*		HttpClient httpClient = this.getHttpClient(6 * 10 * 1000);
		HttpGet httpGet = new HttpGet(stringURL);

		HttpResponse response = httpClient.execute(httpGet);
		Log.e("HOMEBANNER", "getStatusCode..." + response.getStatusLine().getStatusCode() + "..........." + stringURL);
		if(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201){
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
		}else {
			throw new Exception();
		}*/

		return inputStream;
		 
		
	}


	private InputStream GetHttps(String https) throws Exception {

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new MyTrustManager() }, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
		HttpsURLConnection conn = (HttpsURLConnection) new URL(https).openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept-Encoding", "UTF-8");
		conn.setConnectTimeout(120000);
		//conn.setReadTimeout(120000);

		//.setDoOutput(false);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
						"application/x-shockwave-flash, application/xaml+xml, " +
						"application/vnd.ms-xpsdocument, application/x-ms-xbap, " +
						"application/x-ms-application, application/vnd.ms-excel, " +
						"application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "en");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Accept-Encoding", "identity");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; " +
						".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; " +
						".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		//设置为长连接
		conn.setRequestProperty("Connection", "Keep-Alive");

		conn.connect();
		//Log.e("HOMEBANNER", https +"conn..." + https);
		//Log.e("HOMEBANNER", https +"conn..." + conn.getResponseCode());
		//conn.setRequestProperty("Accept-Charset", "UTF-8");
		//conn.get
		Log.e("conn", "getContentType--------->" + conn.getContentType());
		if(conn.getContentType().contains("text/html")){
			throw new Exception();
		}
		if(conn.getResponseCode() != 200){
			throw new Exception();
		}
		InputStream inputStream = conn.getInputStream();

//		conn.setDoInput(true);
		//conn.connect();
		return inputStream;
	}

	public HttpClient getHttpClient(int timeOut) {
		HttpClient httpClient = getNewHttpClient(timeOut);
		return httpClient;
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

	private class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private class MyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		public X509Certificate[] getAcceptedIssuers() {

			return null;
		}
	}
}
