package com.cflint.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;

import com.cflint.application.NMBSApplication;


public class TrustCertainHostNameFactory extends SSLSocketFactory {

    private static TrustCertainHostNameFactory mInstance;

    public TrustCertainHostNameFactory(KeyStore truststore) throws NoSuchAlgorithmException,
            KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
    }

    public static TrustCertainHostNameFactory getDefault(Context context, String url) {
        KeyStore keystore = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in;
            String str = Utils.getCrtName(url);

            in = context.getAssets().open(str);
            Certificate ca = cf.generateCertificate(in);

            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            if (null == mInstance) {
                mInstance = new TrustCertainHostNameFactory(keystore);
            }
        } catch (Exception e) {

        }
        return mInstance;
    }

    @Override
    public Socket createSocket() throws IOException {
        return super.createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {
        return super.createSocket(socket, host, port, autoClose);
    }

}
