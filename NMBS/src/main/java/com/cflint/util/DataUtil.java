package com.cflint.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.protocol.HTTP;

public class DataUtil {

		public static String ReadStringFromInputStream(InputStream is) throws IOException{
			
			InputStreamReader jsonInputStreamReader = new InputStreamReader(is,HTTP.UTF_8);
			
			char[] buffer = new char[1000];
			StringBuffer sb = new StringBuffer(10000);
			int numberofchars=0;
			do {
					sb.append(buffer, 0, numberofchars);
					numberofchars=jsonInputStreamReader.read(buffer);
			} while (numberofchars > 0);
			
			return sb.toString();
		}
		
		
}
