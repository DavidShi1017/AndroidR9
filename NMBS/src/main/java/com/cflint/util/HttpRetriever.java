package com.cflint.util;


import android.content.Context;
import android.util.Log;

import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


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

    public InputStream retrieveStream(String stringURL, Context context) throws Exception {
        if (stringURL == null || "".equals(stringURL)) {
            return null;
        }
        LogUtils.e("crt", "stringURL------->" + stringURL);
        InputStream inputStream = null;

        URL url = new URL(stringURL);
        if (url.getProtocol().toLowerCase().equals("https")) {
            Log.e("HOMEBANNER", "https...");
            //HttpsURLConnection urlConnection = GetHttps(stringURL);
            inputStream = GetHttps(stringURL);
            //inputStream = urlConnection.getInputStream();
            Log.e("HOMEBANNER", "inputStream..." + inputStream);
        } else {
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

		/*SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new EasyX509TrustManager(null) }, new SecureRandom());*/
        LogUtils.e("crt", "https------->" + https);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        String str = Utils.getCrtName(https);

        InputStream in = NMBSApplication.getInstance().getApplicationContext().getAssets().open(str);
        Certificate ca = cf.generateCertificate(in);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null);
        keystore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keystore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        //HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
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
        //Log.e("conn", "getContentType--------->" + conn.getContentType());
        if (conn.getContentType().contains("text/html")) {
            throw new Exception();
        }
        if (conn.getResponseCode() != 200) {
            throw new Exception();
        }
        InputStream inputStream = conn.getInputStream();

//		conn.setDoInput(true);
        //conn.connect();
        return inputStream;
    }
	/*private class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}*/
}
