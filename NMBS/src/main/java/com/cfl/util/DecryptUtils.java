package com.cfl.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.cfl.log.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shig on 2018/5/31.
 */

public class DecryptUtils {

    public static String  encryptData(){
        final byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        IvParameterSpec ivParameterSpec = new IvParameterSpec("sncb europe app.".getBytes());
        int srcBuffSiz = 1024;
        byte[] srcBuff = new byte[srcBuffSiz];
        Arrays.fill(srcBuff, (byte)0x01);
        byte[] dstBuff = null;
        SecretKeySpec skeySpec = new SecretKeySpec("sncb europe app.sncb europe app.".getBytes(), "AES");
        try {
            Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            dstBuff = ecipher.doFinal("*szJu_Vi".getBytes());
            //String Base64Str = Convert.ToBase64String(myByte);
            Log.e("Test", "test------->" + Base64.encodeToString(dstBuff, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(dstBuff, Base64.DEFAULT);
    }

    public static String decryptData(String str){
        LogUtils.e("Test", "str------->" + str);
        final byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        IvParameterSpec ivParameterSpec = new IvParameterSpec("sncb europe app.".getBytes());
        int srcBuffSiz = 1024;
        byte[] srcBuff = new byte[srcBuffSiz];
        Arrays.fill(srcBuff, (byte)0x01);
        SecretKeySpec skeySpec = new SecretKeySpec("sncb europe app.".getBytes(), "AES");
        String decryptStr = "";
        try {
            Cipher ecipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            ecipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] dstBuff = ecipher.doFinal(Base64.decode(str, Base64.DEFAULT));

            //String Base64Str = Convert.ToBase64String(myByte);
            LogUtils.e("Test", "test------->" + Base64.encodeToString(dstBuff, Base64.DEFAULT));
            LogUtils.e("Test", "test------->" + new String(dstBuff));
            decryptStr =  new String(dstBuff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptStr;
    }
}
